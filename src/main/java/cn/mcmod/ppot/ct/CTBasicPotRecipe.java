package cn.mcmod.ppot.ct;

import java.util.List;

import com.google.common.collect.Lists;

import cn.mcmod.ppot.PotmanRegistry;
import cn.mcmod.ppot.recipe.BasicPotRecipe;
import cn.mcmod.ppot.recipe.IPotRecipe;
import cn.mcmod.ppot.recipe.UniversalFluidPotRecipe;
import cn.mcmod_mmf.mmlib.recipe.UniversalFluid;
import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.proj_pot.BasicPotRecipe")
@ZenRegister
public class CTBasicPotRecipe {

	@ZenMethod
	public static void addPotRecipe(String reg_name, IIngredient[] input_items, ILiquidStack[] input_fluids,
			IItemStack result) {
		addPotRecipe(reg_name, input_items, input_fluids, result, 200, 8000, 18000);
	}

	@ZenMethod
	public static void addPotRecipe(String reg_name, IIngredient[] input_items, String input_univerfluids,
			int fluid_amont, IItemStack result) {
		addPotRecipe(reg_name, input_items, input_univerfluids, fluid_amont, result, 200, 8000, 18000);
	}

	@ZenMethod
	public static void addPotRecipe(String reg_name, IIngredient[] input_items, ILiquidStack[] input_fluids,
			IItemStack result, int cooking, int min, int max) {
		List<FluidStack> listfluid = Lists.newArrayList();
		for (ILiquidStack ctfluid : input_fluids) {
			listfluid.add(CraftTweakerMC.getLiquidStack(ctfluid));
		}
		NonNullList<Ingredient> items = NonNullList.create();
		for (IIngredient ctingredient : input_items) {
			items.add(CraftTweakerMC.getIngredient(ctingredient));
		}
		BasicPotRecipe recipe = new BasicPotRecipe(items, listfluid, CraftTweakerMC.getItemStack(result), cooking, min,
				max);
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				PotmanRegistry.POT_RECIPE.register(recipe.setRegistryName(reg_name));
			}

			@Override
			public String describe() {
				return "Adding basic pot recipe " + recipe.getRegistryName().toString();
			}
		});
	}

	@ZenMethod
	public static void addPotRecipe(String reg_name, IIngredient[] input_items, String input_univerfluids,
			int fluid_amont, IItemStack result, int cooking, int min, int max) {
		NonNullList<Ingredient> items = NonNullList.create();
		for (IIngredient ctingredient : input_items) {
			items.add(CraftTweakerMC.getIngredient(ctingredient));
		}
		UniversalFluidPotRecipe recipe = new UniversalFluidPotRecipe(items, UniversalFluid.get(input_univerfluids),fluid_amont,
				CraftTweakerMC.getItemStack(result), cooking, min, max);
		CraftTweakerAPI.apply(new IAction() {
			@Override
			public void apply() {
				PotmanRegistry.POT_RECIPE.register(recipe.setRegistryName(reg_name));
			}

			@Override
			public String describe() {
				return "Adding an universal fluid pot recipe " + recipe.getRegistryName().toString();
			}
		});
	}

	@ZenMethod
	public static void removeRecipe(IItemStack result) {
        List<IPotRecipe> removeList = Lists.newArrayList();
        PotmanRegistry.POT_RECIPE.getValuesCollection()
            .stream()
            .filter(x -> RecipesUtil.getInstance().compareItems(x, CraftTweakerMC.getItemStack(result)))
            .forEach(removeList::add);
        for(IPotRecipe recipe : removeList)
			CraftTweakerAPI.apply(new IAction() {
				@Override
				public void apply() {
					IForgeRegistryModifiable<IPotRecipe> modRegistry = (IForgeRegistryModifiable<IPotRecipe>) PotmanRegistry.POT_RECIPE;
					modRegistry.remove(recipe.getRegistryName());
				}

				@Override
				public String describe() {
					return "Removing a pot recipe " + recipe.getRegistryName().toString();
				}
			});
		
	}
	
	@ZenMethod
	public static void removeRecipe(String registryName) {
		IPotRecipe recipe = PotmanRegistry.POT_RECIPE.getValue(new ResourceLocation(registryName));
		if (recipe != null) {
			CraftTweakerAPI.apply(new IAction() {
				@Override
				public void apply() {
					IForgeRegistryModifiable<IPotRecipe> modRegistry = (IForgeRegistryModifiable<IPotRecipe>) PotmanRegistry.POT_RECIPE;
					modRegistry.remove(recipe.getRegistryName());
				}

				@Override
				public String describe() {
					return "Removing a pot recipe " + recipe.getRegistryName().toString();
				}
			});
		}
	}
	
}
