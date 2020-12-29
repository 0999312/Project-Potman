package cn.mcmod.ppot.pot.cookingpot;

import cn.mcmod.ppot.CommonProxy;
import cn.mcmod.ppot.GuiHandler;
import cn.mcmod.ppot.PotmanMain;
import cn.mcmod.ppot.pot.IStove;
import cn.mcmod_mmf.mmlib.block.BlockFacing;
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
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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

public class BlockCookingPot extends BlockFacing implements ITileEntityProvider {
	public static final PropertyBool HASRESULT = PropertyBool.create("has_result");
    private static boolean keepInventory;

    public BlockCookingPot() {
        super(Material.IRON, false);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.METAL);
        this.setDefaultState(getDefaultState().withProperty(HASRESULT, false));
        this.setCreativeTab(CreativeTabs.DECORATIONS);
     
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
        Block tile = worldIn.getBlockState(pos.down()).getBlock();
        return tile instanceof IStove && super.canPlaceBlockAt(worldIn, pos);
    }

    public boolean canBlockStay(World worldIn, BlockPos pos) {
        Block tile = worldIn.getBlockState(pos.down()).getBlock();
        return tile instanceof IStove;
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
		    if (tile instanceof TileEntityCookingPot) {
		        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ItemHandlerHelper.copyStackWithSize(stack, 1));
		        if (handler != null) {
		            FluidUtil.interactWithFluidHandler(playerIn, hand, tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing));
		            return true;
		        }

		        playerIn.openGui(PotmanMain.instance, GuiHandler.ID_POT, worldIn, pos.getX(), pos.getY(), pos.getZ());
		        return true;
		    }
		}

		return true;
    }

    public static void setState(boolean hasItem, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        IBlockState facing = worldIn.getBlockState(pos);
        keepInventory = true;
        worldIn.setBlockState(pos, CommonProxy.COOKING_POT.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
        worldIn.setBlockState(pos, CommonProxy.COOKING_POT.getDefaultState().withProperty(FACING, facing.getValue(FACING)).withProperty(HASRESULT, hasItem), 3);
        keepInventory = false;
        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!keepInventory) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityCookingPot) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityCookingPot)te);
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
        return new TileEntityCookingPot();
    }

}