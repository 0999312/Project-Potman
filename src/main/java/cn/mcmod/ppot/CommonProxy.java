package cn.mcmod.ppot;

import cn.mcmod.ppot.pot.BlockFireStove;
import cn.mcmod.ppot.pot.TileEntityFireStove;
import cn.mcmod.ppot.pot.camppot.BlockCampfirePot;
import cn.mcmod.ppot.pot.camppot.TileEntityCampfirePot;
import cn.mcmod.ppot.pot.cookingpot.BlockCookingPot;
import cn.mcmod.ppot.pot.cookingpot.TileEntityCookingPot;
import cn.mcmod.ppot.recipe.IPotRecipe;
import cn.mcmod_mmf.mmlib.register.BlockRegister;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.RegistryBuilder;

@EventBusSubscriber
public class CommonProxy {
    public static Block CAMP_POT_IDLE = new BlockCampfirePot(false);
    public static Block CAMP_POT_LIT = new BlockCampfirePot(true);
    
    public static Block STOVE_IDLE = new BlockFireStove(false);
    public static Block STOVE_LIT = new BlockFireStove(true);
    
    public static Block COOKING_POT = new BlockCookingPot();
    
    private static SimpleNetworkWrapper network;
    public static SimpleNetworkWrapper getNetwork() {
        return network;
    }
    
    public void preInit(FMLPreInitializationEvent event) {
    	BlockRegister.getInstance().register(PotmanMain.MODID, CAMP_POT_IDLE, new ItemBlock(CAMP_POT_IDLE), "camp_pot");
    	BlockRegister.getInstance().registerNoItem(PotmanMain.MODID, CAMP_POT_LIT, "camp_pot_lit");
    	
    	BlockRegister.getInstance().register(PotmanMain.MODID, STOVE_IDLE, new ItemBlock(STOVE_IDLE), "fire_stove");
    	BlockRegister.getInstance().registerNoItem(PotmanMain.MODID, STOVE_LIT, "fire_stove_lit");
    	
    	BlockRegister.getInstance().register(PotmanMain.MODID, COOKING_POT, new ItemBlock(COOKING_POT), "cooking_pot");
    	
    	registerTileEntity(TileEntityCampfirePot.class,"camp_pot");
    	registerTileEntity(TileEntityFireStove.class,"fire_stove");
    	registerTileEntity(TileEntityCookingPot.class,"cooking_pot");
    }

    public void init(FMLInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(PotmanMain.MODID);
    	network.registerMessage(PacketHeatControlMessage.PacketHeatMessageHandler.class,PacketHeatControlMessage.class,0,Side.SERVER);
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    private void registerTileEntity(Class<? extends TileEntity> cls, String baseName) {
        GameRegistry.registerTileEntity(cls, new ResourceLocation(PotmanMain.MODID, baseName));
    }
    
    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry event){
    	new RegistryBuilder<IPotRecipe>().setName(new ResourceLocation(PotmanMain.MODID, "universal_pot_recipe")).allowModification().setType(IPotRecipe.class).create();
    }

}
