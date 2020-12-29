package cn.mcmod.ppot.jei;

import cn.mcmod.ppot.CommonProxy;
import cn.mcmod.ppot.PotmanMain;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CategoryPot implements IRecipeCategory<IRecipeWrapper>{
	 protected final IDrawable background;
	  private final IDrawable icon;
	  public CategoryPot(IGuiHelper helper) {
		  ResourceLocation backgroundTexture = new ResourceLocation(PotmanMain.MODID+":textures/gui/pot_jei.png");
		  this.icon = helper.createDrawableIngredient(new ItemStack(CommonProxy.CAMP_POT_IDLE));
		  this.background = helper.createDrawable(backgroundTexture, 7, 7, 162, 74);
	}
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public String getModName() {
		return PotmanMain.NAME;
	}

	@Override
	public String getTitle() {
		return I18n.format("jei.proj_pot.category.universal_pot", new Object[0]);
	}

	@Override
	public String getUid() {
		return "proj_pot.universal_pot";
	}
	@Override
	public void setRecipe(IRecipeLayout arg0, IRecipeWrapper arg1, IIngredients arg2) {
		IGuiItemStackGroup items = arg0.getItemStacks();
		
		items.init(0, true, 37, 11);
		int k,l;
	    for (k = 1; k <5; ++k)
	        items.init(k,true, 10 + (k-1) * 18, 29);
	    for (l = 5; l <9; ++l)
        	items.init(l,true, 10 + (l-5) * 18, 47);
		items.init(9, false, 122, 38);
	    
		items.set(arg2);
		IGuiFluidStackGroup fiuld = arg0.getFluidStacks();
		fiuld.init(0, true, 88, 4, 72, 16, arg2.getInputs(VanillaTypes.FLUID).get(0).get(0).amount, false, null);
		if(arg2.getInputs(VanillaTypes.FLUID).get(0).get(0)!=null&&arg2.getInputs(VanillaTypes.FLUID).get(0).get(0).amount>0)
		fiuld.set(0, arg2.getInputs(VanillaTypes.FLUID).get(0));
	}
	public IDrawable getIcon() {
		return icon;
	}

}
