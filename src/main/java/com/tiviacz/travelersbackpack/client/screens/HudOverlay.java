package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.item.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class HudOverlay {
    public static final Identifier OVERLAY = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/overlay.png");

    public static void renderOverlay(ItemStack stack, Minecraft mc, GuiGraphicsExtractor guiGraphics) {
        if(mc == null) return;

        var player = mc.player;
        var window = mc.getWindow();
        if(window == null) return;

        int x = window.getGuiScaledWidth() - TravelersBackpackConfig.CLIENT.overlay.offsetX.get();
        int y = window.getGuiScaledHeight() - TravelersBackpackConfig.CLIENT.overlay.offsetY.get();

        RenderInfo info = stack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
        if(!info.hasTanks()) return;

        if(!info.getRightFluidStack().isEmpty()) {
            FluidStacksResourceHandler right = new FluidStacksResourceHandler(1, info.getCapacity());
            StacksHandlerUtils.setFluid(right, info.getRightFluidStack());
            drawGuiTank(guiGraphics, right, x + 1, y, 21, 8);
        }

        if(!info.getLeftFluidStack().isEmpty()) {
            FluidStacksResourceHandler left = new FluidStacksResourceHandler(1, info.getCapacity());
            StacksHandlerUtils.setFluid(left, info.getLeftFluidStack());
            drawGuiTank(guiGraphics, left, x - 11, y, 21, 8);
        }

        int tankSel = 0;
        if(player != null && player.getMainHandItem().getItem() instanceof HoseItem) {
            tankSel = HoseItem.getHoseTank(player.getMainHandItem());
        }

        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, x, y, (tankSel == 2) ? 0 : 10, 0, 10, 23, 256, 256);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, x - 12, y, (tankSel == 1) ? 0 : 10, 0, 10, 23, 256, 256);
    }

    public static void drawGuiTank(GuiGraphicsExtractor guiGraphics, FluidStacksResourceHandler tank, int startX, int startY, int height, int width) {
        RenderHelper.renderScreenTank(guiGraphics, tank, startX, startY, 0, height, width);
    }
}