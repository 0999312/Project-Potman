package cn.mcmod.ppot.jei;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.Lists;

import cn.mcmod.ppot.PotmanRegistry;
import cn.mcmod.ppot.recipe.IPotRecipe;
import cn.mcmod.ppot.recipe.SmitingPotRecipe;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public final class PotRecipeMaker {
	  public static List<ItemFluidRecipe> getRecipes(IJeiHelpers helpers) {
	    IStackHelper stackHelper = helpers.getStackHelper();
//	    helpers.
	    List<ItemFluidRecipe> recipes = new ArrayList<ItemFluidRecipe>();
	    for (IPotRecipe entry : PotmanRegistry.POT_RECIPE.getValuesCollection()) {
	    	List<ItemStack> input = Lists.newArrayList();
	    	List<List<ItemStack>> inputs = Lists.newArrayList();
	    	List<List<FluidStack>> fluidlist = Lists.newArrayList();
	    	
	    	if(entry instanceof SmitingPotRecipe) {
	    		SmitingPotRecipe recipe = (SmitingPotRecipe) entry;
	    		input.add(recipe.getItemStack());
	    		inputs.add(input);
	    		fluidlist.add(Lists.newArrayList(new FluidStack(FluidRegistry.WATER, 0)));
		    	ItemFluidRecipe newrecipe = new ItemFluidRecipe(inputs, fluidlist, entry.getResultItemStack());
		    	recipes.add(newrecipe);
		    	continue;
	    	}
	    	
	    	inputs = stackHelper.expandRecipeItemStackInputs(entry.getItems());

	    	if(!entry.getFluid().isEmpty())
	    		fluidlist.add(entry.getFluid());
	    	else
	    		fluidlist.add(Lists.newArrayList(new FluidStack(FluidRegistry.WATER, 0)));
	    	
	    	ItemFluidRecipe newrecipe = new ItemFluidRecipe(inputs,fluidlist,entry.getResultItemStack());
	    	recipes.add(newrecipe);
	    }
	    return recipes;
	    
	  }
}
