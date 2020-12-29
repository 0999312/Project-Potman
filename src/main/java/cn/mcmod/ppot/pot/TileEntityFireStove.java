package cn.mcmod.ppot.pot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import javax.annotation.Nonnull;

public class TileEntityFireStove extends TileEntity implements ITickable {

	private int burnTime;

	public void setBurningTime(int burn) {
		burnTime = burn;
	}
	
	public int getBurningTime() {
		return burnTime;
	}
	
	protected void refresh() {
		if (hasWorld() && !world.isRemote) {
			IBlockState state = world.getBlockState(pos);
			world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), state, state, 11);
		}
	}

	public boolean isHeating() {
		return this.burnTime > 0;
	}
	
	@Override
	public void update() {
		boolean flag = this.isHeating();
		boolean flag1 = false;
		if (this.isHeating()) {
			--this.burnTime;
		}
		// check can cook
		if (!this.world.isRemote) {
				if (flag != this.isHeating()) {
					flag1 = true;
			        BlockFireStove.setState(this.isHeating(), this.world, this.pos);
				}
		}
		if (flag1)
			this.markDirty();
	}

	@Override
	public void markDirty() {
		super.markDirty();

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
		cmp.setInteger("BurnTime", this.burnTime);
	}

	public void readPacketNBT(NBTTagCompound cmp) {
		this.burnTime = cmp.getInteger("BurnTime");
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



}