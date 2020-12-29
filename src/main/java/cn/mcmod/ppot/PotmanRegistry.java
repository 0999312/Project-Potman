package cn.mcmod.ppot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import cn.mcmod.ppot.recipe.IPotRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class PotmanRegistry {
	public static final IForgeRegistry<IPotRecipe> POT_RECIPE = GameRegistry.findRegistry(IPotRecipe.class);

	static {
		// Make sure all public static final fields have values, should stop people from
		// prematurely loading this class.
		try {
			int publicStaticFinal = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

			for (Field field : PotmanRegistry.class.getFields()) {
				if (!field.getType().isAssignableFrom(IForgeRegistry.class)) {
					PotmanMain.getLogger().warn("Weird field? (Not a registry) {}", field);
					continue;
				}
				if ((field.getModifiers() & publicStaticFinal) != publicStaticFinal) {
					PotmanMain.getLogger().warn("Weird field? (not Public Static Final) {}", field);
					continue;
				}
				if (field.get(null) == null) {
					throw new RuntimeException(
							"Oh nooo! Someone tried to use the registries before they exist. Now everything is broken!");
				}
			}
		} catch (Exception e) {
			PotmanMain.getLogger().fatal("Fatal error! This is likely a programming mistake.", e);
			throw new RuntimeException(e);
		}
	}
}
