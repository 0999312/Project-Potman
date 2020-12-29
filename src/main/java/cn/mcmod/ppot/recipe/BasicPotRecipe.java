package cn.mcmod.ppot.recipe;

import java.util.List;

import javax.annotation.Nullable;

import cn.mcmod.ppot.PotmanRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public class BasicPotRecipe extends Impl<IPotRecipe> implements IPotRecipe {
	@Nullable
	public static IPotRecipe get(FluidStack fluid, List<ItemStack> inputs) {
		return PotmanRegistry.POT_RECIPE.getValuesCollection().stream().filter(x -> x.matches(fluid, inputs))
				.findFirst().orElse(null);
	}

	protected NonNullList<Ingredient> items = NonNullList.create();
	protected List<FluidStack> fluidStacks;
	protected ItemStack resultItemStack;

	protected int cookingTime;
	protected int minCookingTime;
	protected int maxCookingTime;

	public BasicPotRecipe(NonNullList<Ingredient> item, List<FluidStack> fluid, ItemStack resultItem, int cookingTime,
			int minCookingTime, int maxCookingTime) {
		this.items = item;
		this.fluidStacks = fluid;
		this.resultItemStack = resultItem;
		this.cookingTime = cookingTime;
		this.minCookingTime = minCookingTime;
		this.maxCookingTime = maxCookingTime;
	}

	public BasicPotRecipe(Object[] item, List<FluidStack> fluid, ItemStack resultItem, int cookingTime,
			int minCookingTime, int maxCookingTime) {
		for (Object in : item) {
			Ingredient ing = CraftingHelper.getIngredient(in);
			if (ing != null) {
				items.add(ing);
			} else {
				String ret = "Invalid Universal Pot recipe: ";
				for (Object tmp : item) {
					ret += tmp + ", ";
				}
				ret += resultItem;
				throw new RuntimeException(ret);
			}
		}
		this.fluidStacks = fluid;
		this.resultItemStack = resultItem;
		this.cookingTime = cookingTime;
		this.minCookingTime = minCookingTime;
		this.maxCookingTime = maxCookingTime;
	}

	public boolean matches(FluidStack fluid, List<ItemStack> inputs) {
		if (inputs.size() != this.getItems().size())
			return false;
		boolean flag = false;
		if (this.getFluid().isEmpty())
			flag = true;
		else {
			for (FluidStack fluid_from : this.getFluid()) {
				if (fluid_from.isFluidEqual(fluid)) {
					flag = true;
					break;
				}
			}
		}
		return RecipeMatcher.findMatches(inputs, this.getItems()) != null && flag;
	}

	public int getMaxCookingTime() {
		return maxCookingTime;
	}

	public int getMinCookingTime() {
		return minCookingTime;
	}

	public int getCookingTime() {
		return cookingTime;
	}

	public NonNullList<Ingredient> getItems() {
		return items;
	}

	public ItemStack getResultItemStack(FluidStack fluid, List<ItemStack> inputs) {
		return resultItemStack;
	}

	public List<FluidStack> getFluid() {
		return fluidStacks;
	}

	@Nullable
	public FluidStack getResultFluid(FluidStack fluid) {
		for (FluidStack k : getFluid()) {
			if (k.isFluidEqual(fluid))
				return k;
		}
		return null;
	}

	@Override
	public ItemStack getResultItemStack() {
		return this.resultItemStack;
	}
}
