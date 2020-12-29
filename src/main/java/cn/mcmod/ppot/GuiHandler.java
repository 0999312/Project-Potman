package cn.mcmod.ppot;

import cn.mcmod.ppot.pot.camppot.ContainerCampfirePot;
import cn.mcmod.ppot.pot.camppot.GuiCampfirePot;
import cn.mcmod.ppot.pot.camppot.TileEntityCampfirePot;
import cn.mcmod.ppot.pot.cookingpot.ContainerCookingPot;
import cn.mcmod.ppot.pot.cookingpot.GuiCookingPot;
import cn.mcmod.ppot.pot.cookingpot.TileEntityCookingPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int ID_CAMPFIREPOT = 0;
    public static final int ID_POT = 1;
//    public static final int ID_OVEN = 2;
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
			case ID_CAMPFIREPOT:
				if(tile instanceof TileEntityCampfirePot) {
					return new ContainerCampfirePot(player.inventory, (TileEntityCampfirePot) tile);
				}
				
			case ID_POT:
				if(tile instanceof TileEntityCookingPot) {
					return new ContainerCookingPot(player.inventory, (TileEntityCookingPot) tile);
				}
				return null;
	
			default:
				return null;
		}
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
			case ID_CAMPFIREPOT:
				if(tile instanceof TileEntityCampfirePot) {
					return new GuiCampfirePot(player.inventory, (TileEntityCampfirePot) tile);
				}
			case ID_POT:
				if(tile instanceof TileEntityCookingPot) {
					return new GuiCookingPot(player.inventory, (TileEntityCookingPot) tile);
				}
				return null;
	
			default:
				return null;
		}
    }
}