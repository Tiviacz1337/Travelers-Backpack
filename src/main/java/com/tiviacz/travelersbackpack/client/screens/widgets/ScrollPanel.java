package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.CoreShaders;

import java.util.Collections;
import java.util.List;

public abstract class ScrollPanel extends AbstractContainerEventHandler implements Renderable, NarratableEntry {
    private final Minecraft client;
    protected final int width;
    protected final int height;
    protected final int top;
    protected final int bottom;
    protected final int right;
    protected final int left;
    private boolean scrolling;
    protected float scrollDistance;
    protected boolean captureMouse = true;
    protected final int border;

    private final int barWidth;
    private final int barLeft;
    private final int barBgColor;
    private final int barColor;
    private final int barBorderColor;

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border) {
        this(client, width, height, top, left, border, 6);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth) {
        this(client, width, height, top, left, border, barWidth, 0xFF000000, 0xFF808080, 0xFFC0C0C0);
    }

    public ScrollPanel(Minecraft client, int width, int height, int top, int left, int border, int barWidth, int barBgColor, int barColor, int barBorderColor) {
        this.client = client;
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
        this.bottom = height + this.top;
        this.right = width + this.left;
        this.barLeft = this.left + this.width - barWidth;
        this.border = border;
        this.barWidth = barWidth;
        this.barBgColor = barBgColor;
        this.barColor = barColor;
        this.barBorderColor = barBorderColor;
    }

    protected abstract int getContentHeight();

    //protected abstract void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY);

    protected boolean clickPanel(double mouseX, double mouseY, int button) {
        return false;
    }

    private int getMaxScroll() {
        return this.getContentHeight() - (this.height - this.border);
    }

    private void applyScrollLimits() {
        int max = getMaxScroll();

        if(max < 0) {
            max /= 2;
        }

        if(this.scrollDistance < 0.0F) {
            this.scrollDistance = 0.0F;
        }

        if(this.scrollDistance > max) {
            this.scrollDistance = max;
        }
    }

    @Override
    public boolean mouseScrolled(double p_94686_, double p_94687_, double p_94688_, double p_294830_) {
        if(p_294830_ != 0) {
            this.scrollDistance += (float)(-p_294830_ * getScrollAmount());
            applyScrollLimits();
            return true;
        }
        return false;
    }

    protected int getScrollAmount() {
        return 20;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.left && mouseX < this.right &&
                mouseY >= this.top && mouseY < this.bottom;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(super.mouseClicked(mouseX, mouseY, button))
            return true;

        this.scrolling = button == 0 && mouseX >= barLeft && mouseX < right && mouseY >= top && mouseY < bottom;
        if(this.scrolling) {
            return true;
        }
        int mouseListY = ((int)mouseY) - this.top - this.getContentHeight() + (int)this.scrollDistance - border;
        if(mouseX >= left && mouseX < right && mouseListY < 0) {
            return this.clickPanel(mouseX - left, mouseY - this.top + (int)this.scrollDistance - border, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(super.mouseReleased(mouseX, mouseY, button))
            return true;
        boolean ret = this.scrolling;
        this.scrolling = false;
        return ret;
    }

    private int getBarHeight() {
        int barHeight = (height * height) / this.getContentHeight();

        if(barHeight < 32) barHeight = 32;

        if(barHeight > height - border * 2)
            barHeight = height - border * 2;

        return barHeight;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(this.scrolling) {
            int maxScroll = height - getBarHeight();
            double moved = deltaY / maxScroll;
            this.scrollDistance += getMaxScroll() * moved;
            applyScrollLimits();
            return true;
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Tesselator tess = Tesselator.getInstance();

        double scale = client.getWindow().getGuiScale();
        RenderSystem.enableScissor((int)(left * scale), (int)(client.getWindow().getHeight() - (bottom * scale)),
                (int)(width * scale), (int)(height * scale));

        RenderSystem.disableDepthTest();

        int extraHeight = (this.getContentHeight() + border) - height;
        if(extraHeight > 0) {
            int barHeight = getBarHeight();

            int barTop = (int)this.scrollDistance * (height - barHeight) / extraHeight + this.top;
            if(barTop < this.top) {
                barTop = this.top;
            }

            int barBgAlpha = this.barBgColor >> 24 & 0xff;
            int barBgRed = this.barBgColor >> 16 & 0xff;
            int barBgGreen = this.barBgColor >> 8 & 0xff;
            int barBgBlue = this.barBgColor & 0xff;

            RenderSystem.setShader(CoreShaders.POSITION_COLOR);
            BufferBuilder worldr = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            worldr.addVertex(barLeft, this.bottom, 0.0F).setColor(barBgRed, barBgGreen, barBgBlue, barBgAlpha);
            worldr.addVertex(barLeft + barWidth, this.bottom, 0.0F).setColor(barBgRed, barBgGreen, barBgBlue, barBgAlpha);
            worldr.addVertex(barLeft + barWidth, this.top, 0.0F).setColor(barBgRed, barBgGreen, barBgBlue, barBgAlpha);
            worldr.addVertex(barLeft, this.top, 0.0F).setColor(barBgRed, barBgGreen, barBgBlue, barBgAlpha);
            BufferUploader.drawWithShader(worldr.buildOrThrow());

            int barAlpha = this.barColor >> 24 & 0xff;
            int barRed = this.barColor >> 16 & 0xff;
            int barGreen = this.barColor >> 8 & 0xff;
            int barBlue = this.barColor & 0xff;

            worldr = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            worldr.addVertex(barLeft, barTop + barHeight, 0.0F).setColor(barRed, barGreen, barBlue, barAlpha);
            worldr.addVertex(barLeft + barWidth, barTop + barHeight, 0.0F).setColor(barRed, barGreen, barBlue, barAlpha);
            worldr.addVertex(barLeft + barWidth, barTop, 0.0F).setColor(barRed, barGreen, barBlue, barAlpha);
            worldr.addVertex(barLeft, barTop, 0.0F).setColor(barRed, barGreen, barBlue, barAlpha);
            BufferUploader.drawWithShader(worldr.buildOrThrow());

            int barBorderAlpha = this.barBorderColor >> 24 & 0xff;
            int barBorderRed = this.barBorderColor >> 16 & 0xff;
            int barBorderGreen = this.barBorderColor >> 8 & 0xff;
            int barBorderBlue = this.barBorderColor & 0xff;

            worldr = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            worldr.addVertex(barLeft, barTop + barHeight - 1, 0.0F).setColor(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha);
            worldr.addVertex(barLeft + barWidth - 1, barTop + barHeight - 1, 0.0F).setColor(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha);
            worldr.addVertex(barLeft + barWidth - 1, barTop, 0.0F).setColor(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha);
            worldr.addVertex(barLeft, barTop, 0.0F).setColor(barBorderRed, barBorderGreen, barBorderBlue, barBorderAlpha);
            BufferUploader.drawWithShader(worldr.buildOrThrow());
        }

        RenderSystem.disableBlend();
        RenderSystem.disableScissor();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }
}