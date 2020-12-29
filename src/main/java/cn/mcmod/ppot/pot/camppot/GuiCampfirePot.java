package cn.mcmod.ppot.pot.camppot;

import cn.mcmod_mmf.mmlib.util.ClientUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiCampfirePot extends GuiContainer {

    private static final ResourceLocation mortarGuiTextures = new ResourceLocation("proj_pot:textures/gui/pot.png");

    private TileEntityCampfirePot tilePot;

    public GuiCampfirePot(InventoryPlayer inventory, TileEntityCampfirePot tile) {
        super(new ContainerCampfirePot(inventory, tile));
        this.tilePot = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(mortarGuiTextures);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

        int var7;
        int lh;
        // Flame
        if (this.tilePot.isBurning()) {
            var7 = this.tilePot.getBurnTimeRemainingScaled(12);
            this.drawTexturedModalRect(k + 94, l + 62 - var7, 176, 12 - var7, 14, var7 + 2);
            
            lh = this.getHeatControlProgressScaled(50);
            this.drawTexturedModalRect(k + 118, l + 53, 176, 31, lh + 1, 10);
        }

        int l2 = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(k + 92, l + 30, 176, 14, l2 + 1, 16);
        int l3 = this.getItemCookProgressScaled(161);
        this.drawTexturedModalRect(k + 7, l + 70, 0, 166, l3 + 1, 5);
        {
//        	getMinCookProgressScaled(pixels)
        	  int l4 = this.getMinCookProgressScaled(161);
        	  int l5 = this.getMaxCookProgressScaled(161);
        	  
              this.drawTexturedModalRect(k + 4 + l4, l + 66, 190, 6, 7, 6);
              this.drawTexturedModalRect(k + 4 + l4, l + 73, 190, 0, 7, 6);

              this.drawTexturedModalRect(k + 4 + l5, l + 66, 197, 6, 7, 6);
              this.drawTexturedModalRect(k + 4 + l5, l + 73, 197, 0, 7, 6);
        }

        if (this.tilePot.getTank().getFluid() != null) {
            FluidTank fluidTank = this.tilePot.getTank();
            int heightInd = (int) (72 * ((float) fluidTank.getFluidAmount() / (float) fluidTank.getCapacity()));
            if (heightInd > 0) {
                ClientUtils.getInstance().drawRepeatedFluidSprite(fluidTank.getFluid(), k + 167- heightInd, l + 11 , heightInd, 16f);
            }

        }
    }

    private int getCookProgressScaled(int pixels) {
        int i = this.tilePot.getField(1);
        int j = this.tilePot.getField(4);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }
    
    private int getHeatControlProgressScaled(int pixels) {
        int i = this.tilePot.getField(2);
        return i != 0 ? i * pixels / 100 : 0;
    }
    
    
    private int getItemCookProgressScaled(int pixels) {
        int i = this.tilePot.getField(3);
        int j = this.tilePot.getField(4) * 100;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }
    
    private int getMinCookProgressScaled(int pixels) {
        int i = this.tilePot.getField(5);
        int j = this.tilePot.getField(4) * 100;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }
    private int getMaxCookProgressScaled(int pixels) {
        int i = this.tilePot.getField(6);
        int j = this.tilePot.getField(4) * 100;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        
        
    }

}