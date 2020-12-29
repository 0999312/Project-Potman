package cn.mcmod.ppot.recipe;

import java.util.List;

import cn.mcmod_mmf.mmlib.util.RecipesUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.FluidStack;

public class SmitingPotRecipe extends BasicPotRecipe {
	private ItemStack itemStack = ItemStack.EMPTY;
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public SmitingPotRecipe(ItemStack item, ItemStack resultItem) {
		super(new Object[] {item}, RecipesUtil.getInstance().EMPTY_FLUID, resultItem, 200, 8000, 18000);
		itemStack = item;
	}
	@Override
	public boolean matches(FluidStack fluid, List<ItemStack> inputs) {
		for(ItemStack input : inputs) {
			if(FurnaceRecipes.instance().getSmeltingResult(input).isEmpty()) {
				return false;
			}else if(!compareItemStacks(FurnaceRecipes.instance().getSmeltingResult(input), getResultItemStack(fluid, inputs))){
				return false;
			}
		}
		return true;
	}
	
    /**
     * Compares two itemstacks to ensure that they are the same. This checks both the item and the metadata of the item.
     */
    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }
	
	@Override
	public ItemStack getResultItemStack(FluidStack fluid, List<ItemStack> inputs) {
		ItemStack result = this.resultItemStack.copy();
		result.setCount(inputs.size());
		return result;
	}
}
