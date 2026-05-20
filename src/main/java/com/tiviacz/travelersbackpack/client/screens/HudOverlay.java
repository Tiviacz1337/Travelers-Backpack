package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.item.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

public class HudOverlay {
    public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/overlay.png");

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!TravelersBackpackConfig.getConfig().client.overlay.enableOverlay) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Window window = mc.getWindow();

        if(!ComponentUtils.isWearingBackpack(player) || mc.options.hideGui || (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR))
            return;

        ItemStack stack = ComponentUtils.getWearingBackpack(player);

        int x = window.getGuiScaledWidth() - TravelersBackpackConfig.getConfig().client.overlay.offsetX;
        int y = window.getGuiScaledHeight() - TravelersBackpackConfig.getConfig().client.overlay.offsetY;

        RenderInfo info = stack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
        if(!info.hasTanks()) return;

        if(!info.getRightFluidStack().isEmpty()) {
            FluidTank right = new FluidTank(info.getCapacity());
            right.setFluid(info.getRightFluidStack());
            drawGuiTank(guiGraphics, right, x + 1, y, 21, 8);
        }

        if(!info.getLeftFluidStack().isEmpty()) {
            FluidTank left = new FluidTank(info.getCapacity());
            left.setFluid(info.getLeftFluidStack());
            drawGuiTank(guiGraphics, left, x - 11, y, 21, 8);
        }

        int tankSel = 0;
        if(player != null && player.getMainHandItem().getItem() instanceof HoseItem) {
            tankSel = HoseItem.getHoseTank(player.getMainHandItem());
        }

        guiGraphics.blit(OVERLAY, x, y, (tankSel == 2) ? 0 : 10, 0, 10, 23);
        guiGraphics.blit(OVERLAY, x - 12, y, (tankSel == 1) ? 0 : 10, 0, 10, 23);
    }

    public static void drawGuiTank(GuiGraphics guiGraphics, FluidTank tank, int startX, int startY, int height, int width) {
        RenderHelper.renderScreenTank(guiGraphics, tank, startX, startY, 0, height, width);
    }
}