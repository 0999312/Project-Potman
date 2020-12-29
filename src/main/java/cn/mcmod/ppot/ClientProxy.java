package cn.mcmod.ppot;

import cn.mcmod.ppot.pot.camppot.RenderTileEntityCampfirePot;
import cn.mcmod.ppot.pot.camppot.TileEntityCampfirePot;
import cn.mcmod.ppot.pot.cookingpot.RenderTileEntityCookingPot;
import cn.mcmod.ppot.pot.cookingpot.TileEntityCookingPot;
import cn.mcmod_mmf.mmlib.register.BlockRegister;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        BlockRegister.getInstance().registerRender(CAMP_POT_IDLE);
        BlockRegister.getInstance().registerRender(STOVE_IDLE);
        BlockRegister.getInstance().registerRender(COOKING_POT);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCampfirePot.class, new RenderTileEntityCampfirePot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCookingPot.class, new RenderTileEntityCookingPot());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);

    }

}
