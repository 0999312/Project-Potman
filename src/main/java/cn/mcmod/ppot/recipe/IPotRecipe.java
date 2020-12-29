package cn.mcmod.ppot.recipe;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IPotRecipe extends IForgeRegistryEntry<IPotRecipe> {
	public NonNullList<Ingredient> getItems();
	public List<FluidStack> getFluid();
	public boolean matches(FluidStack fluid, List<ItemStack> inputs);
	public FluidStack getResultFluid(FluidStack fluid);
	public ItemStack getResultItemStack();
	public ItemStack getResultItemStack(FluidStack fluid, List<ItemStack> inputs);
	public int getCookingTime();
	public int getMaxCookingTime();
	public int getMinCookingTime();

}
