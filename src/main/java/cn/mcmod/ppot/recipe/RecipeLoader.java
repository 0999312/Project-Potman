package cn.mcmod.ppot.recipe;

import java.util.List;
import java.util.Map.Entry;

import cn.mcmod.ppot.PotmanMain;
import cn.mcmod_mmf.mmlib.recipe.UniversalFluid;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@EventBusSubscriber
public class RecipeLoader {
    @SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IPotRecipe> event) {
		event.getRegistry()
				.register(addRecipes(new ItemStack(getItemFromRegistryName("mushroom_stew")),
						new Object[] { "listAllmushroom", "listAllmushroom", new ItemStack(Items.BOWL) },
						 UniversalFluid.get("water"), 100, 400, 20000, 38000).setRegistryName(PotmanMain.MODID, "mushroom_stew"));
		event.getRegistry()
				.register(addRecipes(new ItemStack(getItemFromRegistryName("rabbit_stew")),
						new Object[] { "listAllmushroom", "listAllrabbitraw", "cropPotato", "cropCarrot",
								new ItemStack(Items.BOWL) },
						UniversalFluid.get("water"), 100, 400, 22000, 38000).setRegistryName(PotmanMain.MODID, "rabbit_stew"));
		for (Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
			if (entry.getValue().getItem() instanceof ItemFood)
				RegisterFurnaceRecipes(event, entry.getValue(), entry.getKey());
		}
	}

	private static void RegisterFurnaceRecipes(RegistryEvent.Register<IPotRecipe> event, ItemStack result, ItemStack item) {
		StringBuilder builder = new StringBuilder(result.getItem().getRegistryName().getResourceDomain())
				.append('.').append(result.getItem().getRegistryName().getResourcePath()).append("_meta_").append(result.getMetadata());
		event.getRegistry().register(new SmitingPotRecipe(item, result).setRegistryName(PotmanMain.MODID, builder.append("_furnace").toString()));
		builder = null;
	}

	public static BasicPotRecipe addRecipes(ItemStack result, Object[] list) {
		return addRecipes(result, list, (RecipesUtil.getInstance()).EMPTY_FLUID);
	}

	public static BasicPotRecipe addRecipes(ItemStack result, Object[] list, List<FluidStack> listfluid) {
		return addRecipes(result, list, listfluid, 200, 8000, 18000);
	}

	public static BasicPotRecipe addRecipes(ItemStack result, Object[] list, List<FluidStack> listfluid, int cooking,
			int min, int max) {
		return new BasicPotRecipe(list, listfluid, result, cooking, min, max);
	}

	public static BasicPotRecipe addRecipes(ItemStack result, Object[] list, UniversalFluid fluid, int amount, int cooking,
			int min, int max) {
		return new UniversalFluidPotRecipe(list, fluid, amount, result, cooking, min, max);
	}

	private static Item getItemFromRegistryName(String name) {
		return getItemFromRegistryName(new ResourceLocation("minecraft", name));
	}

	private static Item getItemFromRegistryName(ResourceLocation name) {
		return ForgeRegistries.ITEMS.getValue(name);
	}

}
