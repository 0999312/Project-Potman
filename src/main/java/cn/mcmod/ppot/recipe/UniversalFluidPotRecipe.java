package cn.mcmod.ppot.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import cn.mcmod_mmf.mmlib.recipe.UniversalFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.fluids.FluidStack;

public class UniversalFluidPotRecipe extends BasicPotRecipe {
	
	private UniversalFluid universal_fluid;
	private int fluid_amount = 0;
	
	public UniversalFluidPotRecipe(Object[] item, UniversalFluid fluid, int amount, ItemStack resultItem, int cookingTime,
			int minCookingTime, int maxCookingTime) {
		super(item, Lists.newArrayList(), resultItem, cookingTime, minCookingTime, maxCookingTime);
		universal_fluid = fluid;
		fluid_amount = amount;
	}

	public UniversalFluidPotRecipe(NonNullList<Ingredient> item, UniversalFluid fluid, int amount, ItemStack resultItem,
			int cookingTime, int minCookingTime, int maxCookingTime) {
		super(item, Lists.newArrayList(), resultItem, cookingTime, minCookingTime, maxCookingTime);
		universal_fluid = fluid;
		fluid_amount = amount;
	}
	
	@Override
	public boolean matches(FluidStack fluid, List<ItemStack> inputs) {
		if (inputs.size() != this.getItems().size())
			return false;
		return RecipeMatcher.findMatches(inputs, this.getItems()) != null 
				&& universal_fluid.hasFluid(fluid.getFluid());
	}
	
	@Override
	public List<FluidStack> getFluid() {
		return universal_fluid.getFluidList(fluid_amount);
	}
		
}
