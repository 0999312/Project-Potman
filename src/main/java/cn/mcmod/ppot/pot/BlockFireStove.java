package cn.mcmod.ppot.pot;

import cn.mcmod.ppot.CommonProxy;
import cn.mcmod_mmf.mmlib.block.BlockFacing;
import cn.mcmod_mmf.mmlib.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import java.util.Random;

public class BlockFireStove extends BlockFacing implements ITileEntityProvider, IStove {
    protected static final AxisAlignedBB CAMPFIRE_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
    private final boolean isBurning;

    public BlockFireStove(boolean isBurning) {
        super(Material.IRON, false);
        this.setHardness(0.5F);
        this.setSoundType(SoundType.METAL);
        this.isBurning = isBurning;
        if (isBurning) {
            this.setLightLevel(0.85F);
        } else {
            this.setCreativeTab(CreativeTabs.DECORATIONS);
        }
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
		    if (tile instanceof TileEntityFireStove) {
		        TileEntityFireStove tileEntityCampfire = (TileEntityFireStove) tile;
		        
		        if (WorldUtil.getInstance().isItemFuel(stack)) {
		            tileEntityCampfire.setBurningTime(tileEntityCampfire.getBurningTime() + TileEntityFurnace.getItemBurnTime(stack));
					setState(true, worldIn, pos);
		            return true;
		            
		        }
		        return true;
		    }
		}

		return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.getBlockState(pos.up()).getBlock().onNeighborChange(worldIn, pos.up(), pos);
    }

    public static void setState(boolean active, World worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        IBlockState facing = worldIn.getBlockState(pos);
        if (active) {
            worldIn.setBlockState(pos, CommonProxy.STOVE_LIT.getDefaultState().withProperty(FACING, facing.getValue(FACING)));
            worldIn.setBlockState(pos, CommonProxy.STOVE_LIT.getDefaultState().withProperty(FACING, facing.getValue(FACING)));
        } else {
        	worldIn.setBlockState(pos, CommonProxy.STOVE_IDLE.getDefaultState().withProperty(FACING, facing.getValue(FACING)));
            worldIn.setBlockState(pos, CommonProxy.STOVE_IDLE.getDefaultState().withProperty(FACING, facing.getValue(FACING)));
        }
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFireStove();
    }


	@Override
	public boolean isHeating(World worldIn, IBlockState state) {
		return this.isBurning;
	}

}