package com.tiviacz.travelersbackpack.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TankWidget;
import com.tiviacz.travelersbackpack.items.BackpackTankItem;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;

public final class RadialToolsOverlay {
    public static final ResourceLocation TOOLS_OVERLAY = new ResourceLocation(TravelersBackpack.MODID, "textures/gui/tools_overlay.png");
    private static final int ICON_SIZE = 16;
    private static final int ITEM_RING_RADIUS = 42;
    private static final int DEADZONE_RADIUS = 30;

    public static final int ADD_NEW = -999;

    public static void blitCentered(GuiGraphics g, ResourceLocation texture, int centerX, int centerY, int size) {
        int x = centerX - size / 2;
        int y = centerY - size / 2;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.blit(texture, x, y, 0, 0, size, size, size, size);
        RenderSystem.disableBlend();
    }

    private static void beginRadialTransform(GuiGraphics g, int centerX, int centerY, float openProgress) {
        openProgress = Mth.clamp(openProgress, 0.0F, 1.0F);
        float t = openProgress;
        t = t * t * (3.0F - 2.0F * t);
        float scale = t;
        float opacity = t;

        g.pose().pushPose();
        g.pose().translate(centerX, centerY, 0);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, opacity);
        g.pose().scale(scale, scale, 1.0F);
        g.pose().translate(-centerX, -centerY, 0);
    }

    private static void endRadialTransform(GuiGraphics g) {
        g.pose().popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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

    public static void renderRadialBackground(GuiGraphics g, int centerX, int centerY) {
        blitCentered(g, TOOLS_OVERLAY, centerX, centerY, 256);
    }

    public static int renderRadialItems(GuiGraphics g, ItemStack backpack, NonNullList<ItemStack> tools, boolean canAdd, ArrayList<Integer> segToSlot, int plusSlot, int centerX, int centerY, int mouseX, int mouseY) {
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

        renderCenteredItem(g, font, backpack, centerX, centerY, 1.25F);

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
                g.fill(x - 2, y - 2, x + ICON_SIZE + 2, y + ICON_SIZE + 2, 0x80FFFFFF);
            }

            int slot = segToSlot.get(seg);
            boolean isPlusHere = (canAdd && plusSlot != -1 && slot == plusSlot && tools.get(slot).isEmpty());

            if(isPlusHere) {
                renderPlusButton(g, font, x, y);
            } else {
                ItemStack stack = tools.get(slot);
                g.renderItem(stack, x, y);
                g.renderItemDecorations(font, stack, x, y);
            }
        }

        if(hoveredIsPlus) {
            g.renderTooltip(font, Component.translatable("screen.travelersbackpack.add_to_tools"), mouseX, mouseY);
        } else if(hoveredResult >= 0) {
            ItemStack hoveredStack = tools.get(hoveredResult);
            if(!hoveredStack.isEmpty()) {
                List<Component> tooltip = getTooltipFromItem(mc, hoveredStack);
                //Fluid contents for backpack tanks
                if(hoveredStack.getItem() instanceof BackpackTankItem) {
                    if(!NbtHelper.getOrDefault(backpack, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY).isEmpty()) {
                        RenderInfo renderInfo = NbtHelper.get(backpack, ModDataHelper.RENDER_INFO);
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
                }
                g.renderTooltip(font, tooltip, hoveredStack.getTooltipImage(), hoveredStack, mouseX, mouseY);
            }
        }

        return hoveredResult;
    }

    public static List<Component> getTooltipFromItem(Minecraft minecraft, ItemStack item) {
        return item.getTooltipLines(minecraft.player, TooltipFlag.NORMAL);
    }

    public static int renderRadial(GuiGraphics g, ItemStack backpack, ItemStack heldItem, NonNullList<ItemStack> tools, boolean canAdd, int centerX, int centerY, int mouseX, int mouseY, float partialTick, float openProgress) {
        if(tools == null) return -1;

        beginRadialTransform(g, centerX, centerY, openProgress);

        int[] plusSlotRef = new int[1];
        ArrayList<Integer> segToSlot = buildSegToSlot(tools, canAdd, plusSlotRef);
        int plusSlot = plusSlotRef[0];

        // pass 1: background
        renderRadialBackground(g, centerX, centerY);

        int result;

        if(heldItem.getItem() instanceof HoseItem) {
            result = renderRadialItems(g, backpack, tools, false, segToSlot, plusSlot, centerX, centerY, mouseX, mouseY);
        } else {
            result = renderRadialItems(g, backpack, tools, canAdd, segToSlot, plusSlot, centerX, centerY, mouseX, mouseY);
        }

        endRadialTransform(g);
        return result;
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

    private static void renderPlusButton(GuiGraphics g, Font font, int x, int y) {
        String plus = "+";
        float s = 1.25F;

        float px = x + ICON_SIZE / 2f - font.width(plus) / 2f;
        float py = y + ICON_SIZE / 2f - font.lineHeight / 2f;

        float cx = x + ICON_SIZE / 2f;
        float cy = y + ICON_SIZE / 2f;

        g.pose().pushPose();

        g.pose().translate(cx, cy, 0);
        g.pose().scale(s, s, 1.0F);
        g.pose().translate(-cx, -cy, 0);

        g.drawString(font, plus, px + 0.5F, py + 1F, 0xFFFFFF, false);

        g.pose().popPose();
    }

    private static void renderCenteredItem(GuiGraphics g, Font font, ItemStack stack, int centerX, int centerY, float scale) {
        g.pose().pushPose();
        g.pose().translate(centerX, centerY, 0);
        g.pose().scale(scale, scale, 1.0f);
        g.renderItem(stack, -8, -8);
        g.renderItemDecorations(font, stack, -8, -8);
        g.pose().popPose();
    }

    private static double normalize0To2Pi(double a) {
        double twoPi = Math.PI * 2.0;
        a %= twoPi;
        if(a < 0) a += twoPi;
        return a;
    }
}