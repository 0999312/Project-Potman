package cn.mcmod.ppot.pot.camppot;

import cn.mcmod.ppot.CommonProxy;
import cn.mcmod.ppot.GuiHandler;
import cn.mcmod.ppot.PotmanMain;
import cn.mcmod_mmf.mmlib.block.BlockFacing;
import cn.mcmod_mmf.mmlib.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class BlockCampfirePot extends BlockFacing implements ITileEntityProvider {
    protected static final AxisAlignedBB CAMPFIRE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    public static final PropertyBool HASRESULT = PropertyBool.create("has_result");
    private final boolean isBurning;
    private static boolean keepInventory;

    public BlockCampfirePot(boolean isBurning) {
        super(Material.WOOD, false);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.WOOD);
        this.isBurning = isBurning;
        this.setDefaultState(getDefaultState().withProperty(HASRESULT, false));
        if (isBurning) {
            this.setLightLevel(0.85F);
        } else {
            this.setCreativeTab(CreativeTabs.DECORATIONS);
        }
    }

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int facing_meta = meta & 3;
		EnumFacing facing;
		switch (facing_meta) {
		case 0:
			facing = EnumFacing.SOUTH;
			break;
		case 1:
			facing = EnumFacing.WEST;
			break;
		case 2:
			facing = EnumFacing.NORTH;
			break;
		case 3:
			facing = EnumFacing.EAST;
			break;
		default:
			facing = EnumFacing.NORTH;
			break;
		}
		return this.getDefaultState().withProperty(FACING, facing).withProperty(HASRESULT, (meta & 4) == 1);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {

		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(HASRESULT, false);
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		int result = state.getValue(HASRESULT)? 1 : 0;
		return state.getValue(FACING).getHorizontalIndex() + (result << 2);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, HASRESULT });
	}
    
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CAMPFIRE_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID && super.canPlaceBlockAt(worldIn, pos);
    }

    public boolean canBlockStay(World worldIn, BlockPos pos) {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
		ItemStack stack = playerIn.getHeldItem(hand);
		TileEntity tile = worldIn.getTileEntity(pos);
		if (hand == EnumHand.MAIN_HAND) {
		    if (tile instanceof TileEntityCampfirePot) {
		        TileEntityCampfirePot tileEntityCampfire = (TileEntityCampfirePot) tile;
		        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(stack, 1));
		        if (handler != null) {
		            FluidUtil.interactWithFluidHandler(playerIn, hand, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing));
		            return true;
		        }
		        
		        if (WorldUtil.getInstance().isItemFuel(stack)) {
		            tileEntityCampfire.setField(0, (tileEntityCampfire.getField(0) + TileEntityFurnace.getItemBurnTime(stack)));
		            int heat = (TileEntityFurnace.getItemBurnTime(stack) / 32);
		            if(heat > 100)
		            	heat = 100;
		            tileEntityCampfire.setField(2, heat);
					if(stack.getItem().hasContainerItem(stack)) stack = stack.getItem().getContainerItem(stack);
						else stack.shrink(1);
					setState(true, tileEntityCampfire.isWorking(), worldIn, pos);
		            return true;
		            
		        }
		        playerIn.openGui(PotmanMain.instance, GuiHandler.ID_CAMPFIREPOT, worldIn, pos.getX(), pos.getY(), pos.getZ());
		        return true;
		    }
		}

		return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    public static void setState(boolean active, boolean hasItem, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        IBlockState facing = worldIn.getBlockState(pos);
        keepInventory = true;
        if (active) {
            worldIn.setBlockState(pos, CommonProxy.CAMP_POT_LIT.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
            worldIn.setBlockState(pos, CommonProxy.CAMP_POT_LIT.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
        } else {
        	worldIn.setBlockState(pos, CommonProxy.CAMP_POT_IDLE.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
            worldIn.setBlockState(pos, CommonProxy.CAMP_POT_IDLE.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
        }
        keepInventory = false;
        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        double d0 = pos.getX() + 0.5D;
        double d2 = pos.getZ() + 0.5D;
        double d4 = rand.nextDouble() * 0.4D - 0.2D;
        if (this.isBurning) {
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4, pos.getY() + 0.2D, d2 + d4, 0.0D, 0.0D, 0.0D);

            if (rand.nextDouble() < 0.15D) {
                worldIn.playSound(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
    }


    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityCampfirePot) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCampfirePot)te);
                worldIn.updateComparatorOutputLevel(pos, this);
            }

        }

        super.breakBlock(worldIn, pos, state);
    }

    public int quantityDropped(Random random) {
        return 0;
    }
    
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCampfirePot();
    }

}