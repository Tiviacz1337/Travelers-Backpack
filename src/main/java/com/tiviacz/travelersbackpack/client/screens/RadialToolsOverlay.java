package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TankWidget;
import com.tiviacz.travelersbackpack.items.BackpackTankItem;
import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.ClientTooltipFlag;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public final class RadialToolsOverlay {
    public static final Identifier TOOLS_OVERLAY = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/gui/tools_overlay.png");
    private static final int ICON_SIZE = 16;
    private static final int ITEM_RING_RADIUS = 42;
    private static final int DEADZONE_RADIUS = 30;

    public static final int ADD_NEW = -999;

    //Client only indicator to draw or nah
    public static boolean drawCrosshair = true;

    public static int renderRadial(GuiGraphics guiGraphics, ItemStack backpack, ItemStack heldItem, NonNullList<ItemStack> tools, boolean canAdd, int centerX, int centerY, int mouseX, int mouseY, float partialTick, float openProgress) {
        drawCrosshair = true;
        if(tools == null) return -1;

        openProgress = Mth.clamp(openProgress, 0.0F, 1.0F);
        float t = openProgress;
        t = t * t * (3.0F - 2.0F * t);
        float scale = t;

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(centerX, centerY);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.pose().translate(-centerX, -centerY);

        int[] plusSlotRef = new int[1];
        ArrayList<Integer> segToSlot = buildSegToSlot(tools, canAdd, plusSlotRef);
        int plusSlot = plusSlotRef[0];

        int x = centerX - 256 / 2;
        int y = centerY - 256 / 2;
        double opacity = Math.min(t, TravelersBackpackConfig.CLIENT.toolsOverlay.opacity.get());

        int i = ARGB.white((float)opacity);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, TOOLS_OVERLAY, x, y, 0, 0, 256, 256, 256, 256, 256, 256, i);

        int result;

        if(heldItem.getItem() instanceof HoseItem) {
            result = renderRadialItems(guiGraphics, backpack, tools, false, false, segToSlot, plusSlot, centerX, centerY, mouseX, mouseY);
        } else {
            result = renderRadialItems(guiGraphics, backpack, tools, canAdd, heldItem.isEmpty(), segToSlot, plusSlot, centerX, centerY, mouseX, mouseY);
        }

        guiGraphics.pose().popMatrix();
        return result;
    }

    public static int renderRadialItems(GuiGraphics guiGraphics, ItemStack backpack, NonNullList<ItemStack> tools, boolean canAdd, boolean handEmpty, ArrayList<Integer> segToSlot, int plusSlot, int centerX, int centerY, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        int segments = segToSlot.size();
        int hoveredSeg = getHoveredIndex(centerX, centerY, mouseX, mouseY, segments);

        int hoveredResult = -1;
        boolean hoveredIsPlus = false;

        if(hoveredSeg >= 0 && hoveredSeg < segToSlot.size()) {
            int slot = segToSlot.get(hoveredSeg);
            hoveredIsPlus = (canAdd && plusSlot != -1 && slot == plusSlot && tools.get(slot).isEmpty());
            hoveredResult = hoveredIsPlus ? ADD_NEW : slot;
        }

        if(segments == 0 && hoveredSeg == -1) {
            return -1;
        }

        float step = (float)(2.0 * Math.PI / segments);
        float start = (float)(-Math.PI / 2.0);

        for(int seg = 0; seg < segments; seg++) {
            float ang = start + seg * step;

            int x = centerX + Mth.floor(Mth.cos(ang) * ITEM_RING_RADIUS) - ICON_SIZE / 2;
            int y = centerY + Mth.floor(Mth.sin(ang) * ITEM_RING_RADIUS) - ICON_SIZE / 2;

            boolean isHovered = (seg == hoveredSeg);
            if(isHovered) {
                guiGraphics.fill(x - 2, y - 2, x + ICON_SIZE + 2, y + ICON_SIZE + 2, 0x80FFFFFF);
            }

            int slot = segToSlot.get(seg);
            boolean isPlusHere = (canAdd && plusSlot != -1 && slot == plusSlot && tools.get(slot).isEmpty());

            if(isPlusHere) {
                renderPlusButton(guiGraphics, font, x, y);
            } else {
                ItemStack stack = tools.get(slot);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
            }
        }

        boolean renderCenteredItem = true;

        if(hoveredIsPlus) {
            guiGraphics.setTooltipForNextFrame(font, Component.translatable("screen.travelersbackpack.add"), mouseX, mouseY);
        } else if(hoveredResult >= 0) {
            ItemStack hoveredStack = tools.get(hoveredResult);
            if(!hoveredStack.isEmpty()) {
                List<Component> tooltip = (TravelersBackpackConfig.CLIENT.toolsOverlay.showTooltip.get() || hoveredStack.getItem() instanceof HoseItem) ? getTooltipFromItem(mc, hoveredStack) : new ArrayList<>(List.of(hoveredStack.getHoverName()));
                //Fluid contents for backpack tanks
                if(hoveredStack.getItem() instanceof BackpackTankItem) {
                    if(!backpack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY).isEmpty()) {
                        RenderInfo renderInfo = backpack.get(ModDataComponents.RENDER_INFO);
                        if(hoveredResult == 2) {
                            FluidTank rightTank = new FluidTank(renderInfo.getCapacity());
                            rightTank.setFluid(renderInfo.getRightFluidStack());
                            tooltip.addAll(TankWidget.getTankTooltip(rightTank));
                        }
                        if(hoveredResult == 3) {
                            FluidTank leftTank = new FluidTank(renderInfo.getCapacity());
                            leftTank.setFluid(renderInfo.getLeftFluidStack());
                            tooltip.addAll(TankWidget.getTankTooltip(leftTank));
                        }
                    }
                } else {
                    renderCenteredItem = false;
                    renderCenteredText(guiGraphics, handEmpty ? Component.translatable("screen.travelersbackpack.take") : Component.translatable("screen.travelersbackpack.swap"), centerX, centerY);
                }
                guiGraphics.setTooltipForNextFrame(font, tooltip, hoveredStack.getTooltipImage(), hoveredStack, mouseX, mouseY);
            }
        }
        if(renderCenteredItem) {
            if(TravelersBackpackConfig.CLIENT.toolsOverlay.renderBackpackIconInCenter.get()) {
                drawCrosshair = false;
                renderCenteredItem(guiGraphics, font, backpack, centerX, centerY, 1.25F);
            }
        }
        return hoveredResult;
    }

    private static ArrayList<Integer> buildSegToSlot(NonNullList<ItemStack> tools, boolean canAdd, int[] outPlusSlot) {
        ArrayList<Integer> segToSlot = new ArrayList<>();

        for(int i = 0; i < tools.size(); i++) {
            if(!tools.get(i).isEmpty()) segToSlot.add(i);
        }

        int plusSlot = -1;
        if(canAdd) {
            for(int i = 0; i < tools.size(); i++) {
                if(tools.get(i).isEmpty()) {
                    plusSlot = i;
                    break;
                }
            }

            if(plusSlot != -1) {
                int insertPos = 0;
                while(insertPos < segToSlot.size() && segToSlot.get(insertPos) < plusSlot) insertPos++;
                segToSlot.add(insertPos, plusSlot);
            }
        }

        outPlusSlot[0] = plusSlot;
        return segToSlot;
    }

    public static List<Component> getTooltipFromItem(Minecraft minecraft, ItemStack item) {
        return item.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, ClientTooltipFlag.of(TooltipFlag.Default.NORMAL));
    }

    public static int getHoveredIndex(int cx, int cy, int mx, int my, int segments) {
        if(segments == 0) {
            return -1;
        }

        int dx = mx - cx;
        int dy = my - cy;

        if(dx * dx + dy * dy < DEADZONE_RADIUS * DEADZONE_RADIUS) return -1;

        double ang = Math.atan2(dy, dx);
        ang = normalize0To2Pi(ang);

        double start = Math.PI * 1.5;
        double rel = normalize0To2Pi(ang - start);

        double step = (Math.PI * 2.0) / segments;

        int idx = (int)Math.floor((rel + step / 2.0) / step);

        idx = ((idx % segments) + segments) % segments;
        return idx;
    }

    private static void renderCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int centerY) {
        drawCrosshair = false;
        Font textRenderer = Minecraft.getInstance().font;
        int textWidth = textRenderer.width(text);
        int textHeight = textRenderer.lineHeight;

        int textX = centerX - textWidth / 2;
        int textY = centerY - textHeight / 2;

        guiGraphics.enableScissor(textX, textY, textX + 40, textY + 40);
        guiGraphics.drawString(Minecraft.getInstance().font, text, textX, textY, 0xFFFFFFFF, true);
        guiGraphics.disableScissor();
    }

    private static void renderPlusButton(GuiGraphics guiGraphics, Font font, int x, int y) {
        String plus = "+";
        float s = 1.25F;

        int px = (int)(x + ICON_SIZE / 2f - font.width(plus) / 2f);
        int py = (int)(y + ICON_SIZE / 2f - font.lineHeight / 2f);

        float cx = x + ICON_SIZE / 2f;
        float cy = y + ICON_SIZE / 2f;

        guiGraphics.pose().pushMatrix();

        guiGraphics.pose().translate(cx, cy);
        guiGraphics.pose().scale(s, s);
        guiGraphics.pose().translate(-cx, -cy);

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(0.5F, 1.5F);
        guiGraphics.enableScissor(px, py, px + 8, py + 8);
        guiGraphics.drawString(font, plus, px, py, 0xFFFFFFFF, false);
        guiGraphics.disableScissor();
        guiGraphics.pose().popMatrix();

        guiGraphics.pose().popMatrix();
    }

    private static void renderCenteredItem(GuiGraphics guiGraphics, Font font, ItemStack stack, int centerX, int centerY, float scale) {
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(centerX, centerY);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.renderItem(stack, -8, -8);
        guiGraphics.renderItemDecorations(font, stack, -8, -8);
        guiGraphics.pose().popMatrix();
    }

    private static double normalize0To2Pi(double a) {
        double twoPi = Math.PI * 2.0;
        a %= twoPi;
        if(a < 0) a += twoPi;
        return a;
    }
}