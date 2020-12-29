package cn.mcmod.ppot.pot.cookingpot;

import cn.mcmod.ppot.pot.IStove;
import cn.mcmod.ppot.recipe.BasicPotRecipe;
import cn.mcmod.ppot.recipe.IPotRecipe;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

public class TileEntityCookingPot extends TileEntity implements ITickable, IInventory {

	public FluidTank tank = new FluidTank(4000) {
		@Override
		protected void onContentsChanged() {
			TileEntityCookingPot.this.refresh();
		}
	};

	private FluidStack liquidForRendering = null;

	private boolean heating;
	/**
	 * The number of ticks that a fresh copy of the currently-burning item would
	 * keep the furnace burning for
	 */
	private int currentItemBurnTime;
	private int cookTime;
	private int itemCookTime;
	private int maxCookTime;
	
	private int minitemCookTime;
	private int maxitemCookTime;
	
	private boolean working;

	public FluidTank getTank() {
		return this.tank;
	}

	// Render only
	@SideOnly(Side.CLIENT)
	public FluidStack getFluidForRendering(float partialTicks) {
		final FluidStack actual = tank.getFluid();
		int actualAmount;
		if (actual != null && !actual.equals(liquidForRendering)) {
			liquidForRendering = new FluidStack(actual, 0);
		}

		if (liquidForRendering == null) {
			return null;
		}

		actualAmount = actual == null ? 0 : actual.amount;
		int delta = actualAmount - liquidForRendering.amount;
		if (Math.abs(delta) <= 40) {
			liquidForRendering.amount = actualAmount;
		} else {
			int i = (int) (delta * partialTicks * 0.1);
			if (i == 0) {
				i = delta > 0 ? 1 : -1;
			}
			liquidForRendering.amount += i;
		}
		if (liquidForRendering.amount == 0) {
			liquidForRendering = null;
		}
		return liquidForRendering;
	}

	protected void refresh() {
		if (hasWorld() && !world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 11);
		}
	}

	@Override
	public void update() {
		boolean flag = this.isHeating();
		boolean flag2 = this.isWorking();
		boolean flag1 = false;
		if (!this.isHeating()) {
			this.currentItemBurnTime = 0;
		}else if(this.currentItemBurnTime <=0) {
			this.currentItemBurnTime = 50;
		}
		// check can cook
		if (!this.world.isRemote) {
			Block source = getWorld().getBlockState(getPos().down()).getBlock();
			if(source instanceof IStove) {
				IStove heat_source = (IStove) source;
				this.heating = heat_source.isHeating(getWorld(), getWorld().getBlockState(getPos().down()));
			}
				
			if (this.getTank() != null) {

				ArrayList<ItemStack> inventoryList = Lists.newArrayList();
				for (int i = 0; i < 9; i++) {
					if (!this.inventory.get(i).isEmpty()) {
						inventoryList.add(this.inventory.get(i).copy());
					}
				}
				ItemStack itemstack = this.inventory.get(9);
				FluidStack tank_fluid = this.getTank().getFluid();
				if (isRecipes(tank_fluid, inventoryList)) {
					IPotRecipe current_recipe = BasicPotRecipe.get(tank_fluid, inventoryList);
					ItemStack result = current_recipe.getResultItemStack(tank_fluid, inventoryList);
					FluidStack fluidStack = current_recipe.getResultFluid(tank_fluid);
					
					if(this.maxCookTime != current_recipe.getCookingTime())
						this.maxCookTime = current_recipe.getCookingTime();
					if(this.minitemCookTime != current_recipe.getMinCookingTime())
						this.minitemCookTime = current_recipe.getMinCookingTime();
					if(this.maxitemCookTime != current_recipe.getMaxCookingTime())
						this.maxitemCookTime = current_recipe.getMaxCookingTime();
					this.working = RecipesUtil.getInstance().canIncrease(result, itemstack) && isHeating();
					if (working) {
						cookTime += 1;
						itemCookTime += currentItemBurnTime;
					} else {
						cookTime = 0;
						itemCookTime = 0;
					}
					
					if(itemCookTime > maxitemCookTime) {
						cookTime = 0;
						itemCookTime = 0;
						for (int i = 0; i < 9; i++) {
							if (!(this.inventory.get(i).getItem().getContainerItem(this.inventory.get(i)).isEmpty())) {
								if (this.inventory.get(i).getCount() == 1) {
									this.inventory.set(i, this.inventory.get(i).getItem()
											.getContainerItem(this.inventory.get(i)).copy());
								} else
									this.decrStackSize(i, 1);
								Block.spawnAsEntity(getWorld(), getPos(),
										this.inventory.get(i).getItem().getContainerItem(this.inventory.get(i).copy()));
							} else
								this.decrStackSize(i, 1);
						}
						// If pot is a recipe that uses a liquid, it consumes only that amount of liquid
						if (fluidStack != null && fluidStack.amount > 0) {
							this.tank.drain(fluidStack, true);
						}
					}
					if (cookTime >= current_recipe.getCookingTime()) {
						cookTime = 0;
						if(itemCookTime >= current_recipe.getMinCookingTime()) {
							if (itemstack.isEmpty()) {
								this.inventory.set(9, result.copy());
							} else if (itemstack.isItemEqual(result)) {
								itemstack.grow(result.getCount());
							}
							
							// If pot is a recipe that uses a liquid, it consumes only that amount of liquid
							if (fluidStack != null && fluidStack.amount > 0) {
								this.tank.drain(fluidStack, true);
							}

							for (int i = 0; i < 9; i++) {
								if (!(this.inventory.get(i).getItem().getContainerItem(this.inventory.get(i)).isEmpty())) {
									if (this.inventory.get(i).getCount() == 1) {
										this.inventory.set(i, this.inventory.get(i).getItem()
												.getContainerItem(this.inventory.get(i)).copy());
									} else
										this.decrStackSize(i, 1);
									Block.spawnAsEntity(getWorld(), getPos(),
											this.inventory.get(i).getItem().getContainerItem(this.inventory.get(i).copy()));
								} else
									this.decrStackSize(i, 1);
							}
							flag1 = true;
						}
						itemCookTime = 0;
					}

				} else {
					this.working = false;
					cookTime = 0;
					itemCookTime = 0;
					maxitemCookTime = 0;
					minitemCookTime = 0;
				}
				if(flag != this.isHeating()) {
					flag1 = true;
				}
				if(flag2 != this.isWorking()) {
					flag1 = true;
					BlockCookingPot.setState(this.isWorking(), this.world, this.pos);
				}
			}
			if (flag1)
				this.markDirty();
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();

	}
	
	public boolean isWorking() {
		return this.working;
	}
	protected NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(),
			ItemStack.EMPTY);



	@Override
	public String getName() {
		return "container.proj_pot.cookingpot";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 10;
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.inventory) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory.get(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);

		if (!itemstack.isEmpty()) {
			this.markDirty();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStackHelper.getAndRemove(inventory, index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inventory.set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		}
		return player.getDistanceSq(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		this.markDirty();
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		this.markDirty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index < 9;
	}

	@Override
	public int getField(int id) {
		switch (id) {
		case 0:
			return this.cookTime;
		case 1:
			return this.currentItemBurnTime;
		case 2:
			return this.itemCookTime;
		case 3:
			return this.maxCookTime;
		case 4:
			return this.minitemCookTime;
		case 5:
			return this.maxitemCookTime;
		default:
			return 0;

		}
	}

	@Override
	public void setField(int id, int value) {
		switch (id) {
		case 0:
			this.cookTime = value;
			break;
		case 1:
			this.currentItemBurnTime = value;
			break;
		case 2:
			this.itemCookTime = value;
			break;
		case 3:
			this.maxCookTime = value;
			break;
		case 4:
			this.minitemCookTime = value;
			break;
		case 5:
			this.maxitemCookTime = value;
			break;
		}
	}

	public int getFieldCount() {
		return 6;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	public NonNullList<ItemStack> getInventory() {
		return inventory;
	}

//	@SideOnly(Side.CLIENT)
//	public int getBurnTimeRemainingScaled(int par1) {
//		return this.burnTime * par1 / 200;
//	}

	/**
	 * @return
	 */
    protected boolean isRecipes(FluidStack fluid,List<ItemStack> items) {
    	IPotRecipe recipe = BasicPotRecipe.get(fluid, items);
    	if(recipe!=null) {
	    	ItemStack result = recipe.getResultItemStack(fluid, items);
	        if (!result.isEmpty()) {
	        	return true;
	        }
        }
        return false;
    }

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState,
			@Nonnull IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Nonnull
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1nbtTagCompound) {
		NBTTagCompound ret = super.writeToNBT(par1nbtTagCompound);
		writePacketNBT(ret);
		return ret;
	}

	@Nonnull
	@Override
	public final NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound par1nbtTagCompound) {
		super.readFromNBT(par1nbtTagCompound);
		readPacketNBT(par1nbtTagCompound);
	}

	public void writePacketNBT(NBTTagCompound cmp) {
		ItemStackHelper.saveAllItems(cmp, this.inventory);
		cmp.setBoolean("isHeating", this.heating);
		cmp.setInteger("CookTime", this.cookTime);
		cmp.setInteger("ItemCookTime", this.itemCookTime);
		cmp.setInteger("currentItemBurnTime", this.currentItemBurnTime);
		cmp.setBoolean("isWorking", this.working);
		NBTTagCompound tankTag = this.tank.writeToNBT(new NBTTagCompound());
		cmp.setTag("Tank", tankTag);
		if (tank.getFluid() != null) {
			liquidForRendering = tank.getFluid().copy();
		}
	}

	public void readPacketNBT(NBTTagCompound cmp) {
		this.inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(cmp, this.inventory);
		this.heating = cmp.getBoolean("isHeating");
		this.cookTime = cmp.getInteger("CookTime");
		this.itemCookTime = cmp.getInteger("ItemCookTime");
		this.working = cmp.getBoolean("isWorking");
		this.currentItemBurnTime = cmp.getInteger("currentItemBurnTime");
		this.tank.readFromNBT(cmp.getCompoundTag("Tank"));
	}

	@Override
	public final SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writePacketNBT(tag);
		return new SPacketUpdateTileEntity(pos, -999, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		readPacketNBT(packet.getNbtCompound());
	}

	public int getMinItemCookTime() {
		return minitemCookTime;
	}

	public int getMaxItemCookTime() {
		return maxitemCookTime;
	}

	public void setMaxItemCookTime(int maxitemCookTime) {
		this.maxitemCookTime = maxitemCookTime;
	}

	public boolean isHeating() {
		return heating;
	}

}