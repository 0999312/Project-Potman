package cn.mcmod.ppot;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//
@Mod(modid = PotmanMain.MODID, name = PotmanMain.NAME, version = PotmanMain.VERSION,dependencies = "required-after:mm_lib@[2.2.0,);before:sakura@[1.0.5-1.12.2,)")
public class PotmanMain {
	public static final String MODID = "proj_pot";
	public static final String NAME = "Project Potman";
	public static final String VERSION = "@version@";

	private static final Logger logger = LogManager.getLogger(MODID);

	@Instance(PotmanMain.MODID)
	public static PotmanMain instance;

	@SidedProxy(clientSide = "cn.mcmod.ppot.ClientProxy", serverSide = "cn.mcmod.ppot.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit(event);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	public static Logger getLogger() {
		return logger;
	}
}
