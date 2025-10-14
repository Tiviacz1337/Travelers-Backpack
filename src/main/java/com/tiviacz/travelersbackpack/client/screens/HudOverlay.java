package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.platform.Window;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.FluidTank;
import com.tiviacz.travelersbackpack.item.HoseItem;
import com.tiviacz.travelersbackpack.util.RenderHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import java.util.Collections;

public class HudOverlay {
    public static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/overlay.png");
    private static float animationProgress = 0.0F;

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!TravelersBackpackConfig.getConfig().client.overlay.enableOverlay) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Window mainWindow = mc.getWindow();

        if(!ComponentUtils.isWearingBackpack(player) || mc.options.hideGui || (mc.gameMode != null && mc.gameMode.getPlayerMode() == GameType.SPECTATOR))
            return;

        int scaledWidth = mainWindow.getGuiScaledWidth() - TravelersBackpackConfig.getConfig().client.overlay.offsetX;
        int scaledHeight = mainWindow.getGuiScaledHeight() - TravelersBackpackConfig.getConfig().client.overlay.offsetY;

        int textureX = 10;
        int textureY = 0;

        ItemStack stack = ComponentUtils.getWearingBackpack(player);

        KeyMapping key = KeybindHandler.SWITCH_TOOL;
        boolean moveTools = false;

        if(!stack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY).isEmpty()) {
            moveTools = true;
            RenderInfo renderInfo = stack.get(ModDataComponents.RENDER_INFO);
            FluidTank leftTank = new FluidTank(renderInfo.getCapacity());
            leftTank.setFluid(renderInfo.getLeftFluidStack());
            FluidTank rightTank = new FluidTank(renderInfo.getCapacity());
            rightTank.setFluid(renderInfo.getRightFluidStack());

            if(!renderInfo.getRightFluidStack().isEmpty()) {
                drawGuiTank(guiGraphics, rightTank, scaledWidth + 1, scaledHeight, 21, 8);
            }
            if(!renderInfo.getLeftFluidStack().isEmpty()) {
                drawGuiTank(guiGraphics, leftTank, scaledWidth - 11, scaledHeight, 21, 8);
            }

            if(player != null && player.getMainHandItem().getItem() instanceof HoseItem) {
                int tank = HoseItem.getHoseTank(player.getMainHandItem());

                int selectedTextureX = 0;
                int selectedTextureY = 0;

                if(tank == 1) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23, 256, 256);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth - 12, scaledHeight, selectedTextureX, selectedTextureY, 10, 23, 256, 256);
                }

                if(tank == 2) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth, scaledHeight, selectedTextureX, selectedTextureY, 10, 23, 256, 256);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23, 256, 256);
                }

                if(tank == 0) {
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23, 256, 256);
                    guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23, 256, 256);
                }
            } else {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth, scaledHeight, textureX, textureY, 10, 23, 256, 256);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, OVERLAY, scaledWidth - 12, scaledHeight, textureX, textureY, 10, 23, 256, 256);
            }
        }

        if(stack.has(ModDataComponents.TOOLS_CONTAINER)) {
            //Use component directly, because the client doesn't have ItemStackHandler reloaded with new ItemStacks
            NonNullList<ItemStack> tools = getTools(stack.get(ModDataComponents.TOOLS_CONTAINER).getItems());

            if(key.isDown() && tools.size() > 2) {
                if(animationProgress < 1.0F) {
                    animationProgress += 0.05F;
                }

                for(int i = 0; i < tools.size(); i++) {
                    drawItemStack(guiGraphics, tools.get(i), scaledWidth - (moveTools ? 30 : 0), (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                }
            } else if(!tools.isEmpty()) {
                if(animationProgress > 0.0F) {
                    for(int i = 0; i < tools.size(); i++) {
                        drawItemStack(guiGraphics, tools.get(i), scaledWidth - (moveTools ? 30 : 0), (int)(scaledHeight + 11 - (animationProgress * (i * 15))));
                    }

                    animationProgress -= 0.05F;
                } else {
                    if(!tools.get(0).isEmpty()) {
                        drawItemStack(guiGraphics, tools.get(0), scaledWidth - (moveTools ? 30 : 0), scaledHeight - 4);
                    }

                    if(tools.size() > 1) {
                        if(!tools.get(tools.size() - 1).isEmpty()) {
                            drawItemStack(guiGraphics, tools.get(tools.size() - 1), scaledWidth - (moveTools ? 30 : 0), scaledHeight + 11);
                        }
                    }
                }
            }
        }
    }

    public static void drawGuiTank(GuiGraphics guiGraphics, FluidTank tank, int startX, int startY, int height, int width) {
        RenderHelper.renderScreenTank(guiGraphics, tank, startX, startY, 0, height, width);
    }

    private static void drawItemStack(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
        guiGraphics.renderFakeItem(stack, x, y);
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
    }

    public static NonNullList<ItemStack> getTools(NonNullList<ItemStack> inventory) {
        NonNullList<ItemStack> tools = NonNullList.create();
        for(ItemStack itemStack : inventory) {
            if(!itemStack.isEmpty()) {
                tools.add(itemStack);
            }
        }
        Collections.reverse(tools);
        return tools;
    }
}