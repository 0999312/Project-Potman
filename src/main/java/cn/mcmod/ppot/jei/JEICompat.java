package cn.mcmod.ppot.jei;

import cn.mcmod.ppot.CommonProxy;
import cn.mcmod.ppot.pot.camppot.ContainerCampfirePot;
import cn.mcmod.ppot.pot.camppot.GuiCampfirePot;
import cn.mcmod.ppot.pot.cookingpot.ContainerCookingPot;
import cn.mcmod.ppot.pot.cookingpot.GuiCookingPot;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICompat implements IModPlugin {
	@Override
	public void register(IModRegistry registry) {
		IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		registry.addRecipes(PotRecipeMaker.getRecipes(jeiHelpers),"proj_pot.universal_pot");
		
		registry.addRecipeClickArea(GuiCampfirePot.class, 89, 29, 28, 18,"proj_pot.universal_pot");
		registry.addRecipeClickArea(GuiCookingPot.class, 89, 29, 28, 18,"proj_pot.universal_pot");
		
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCampfirePot.class,"proj_pot.universal_pot", 0, 9, 10, 36);
		recipeTransferRegistry.addRecipeTransferHandler(ContainerCookingPot.class,"proj_pot.universal_pot", 0, 9, 10, 36);

		registry.addRecipeCatalyst(new ItemStack(CommonProxy.CAMP_POT_IDLE),"proj_pot.universal_pot");
		registry.addRecipeCatalyst(new ItemStack(CommonProxy.COOKING_POT),"proj_pot.universal_pot");

	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new IRecipeCategory[]{
				new CategoryPot(registry.getJeiHelpers().getGuiHelper()),
			}
		);
	}
	
}
