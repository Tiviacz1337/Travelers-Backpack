package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class HudOverlay {
    public static final ResourceLocation OVERLAY = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/overlay.png");

    public static void renderOverlay(ItemStack stack, Minecraft mc, GuiGraphics g) {
        if(mc == null) return;

        var player = mc.player;
        var window = mc.getWindow();
        if(window == null) return;

        int x = window.getGuiScaledWidth() - TravelersBackpackConfig.CLIENT.overlay.offsetX.get();
        int y = window.getGuiScaledHeight() - TravelersBackpackConfig.CLIENT.overlay.offsetY.get();

        RenderInfo info = NbtHelper.getOrDefault(stack, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
        if(info.isEmpty()) return;

        if(!info.getRightFluidStack().isEmpty()) {
            FluidTank right = new FluidTank(info.getCapacity());
            right.setFluid(info.getRightFluidStack());
            drawGuiTank(g, right, x + 1, y, 21, 8);
        }

        if(!info.getLeftFluidStack().isEmpty()) {
            FluidTank left = new FluidTank(info.getCapacity());
            left.setFluid(info.getLeftFluidStack());
            drawGuiTank(g, left, x - 11, y, 21, 8);
        }

        int tankSel = 0;
        if(player != null && player.getMainHandItem().getItem() instanceof HoseItem) {
            tankSel = HoseItem.getHoseTank(player.getMainHandItem());
        }

        g.blit(OVERLAY, x, y, (tankSel == 2) ? 0 : 10, 0, 10, 23);
        g.blit(OVERLAY, x - 12, y, (tankSel == 1) ? 0 : 10, 0, 10, 23);
    }

    public static void drawGuiTank(GuiGraphics guiGraphics, FluidTank tank, int startX, int startY, int height, int width) {
        RenderHelper.renderScreenTank(guiGraphics, tank, startX, startY, 0, height, width);
    }
}