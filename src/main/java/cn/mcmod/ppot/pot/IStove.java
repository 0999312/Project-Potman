package cn.mcmod.ppot.pot;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

public interface IStove {
	boolean isHeating(World worldIn, IBlockState state);
}
