package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.mojang.blaze3d.vertex.Tesselator;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class InventoryScroll extends ScrollPanel {
    public final AbstractBackpackScreen<?> screen;

    public InventoryScroll(AbstractBackpackScreen<?> screen, Minecraft client, int width, int height, int top, int left) {
        super(client, width, height, top, left, 0);
        this.screen = screen;
    }

    @Override
    protected int getScrollAmount() {
        return 18;
    }

    @Override
    protected int getContentHeight() {
        return this.screen.getRows() * 18;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean ret = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        screen.setScrollAmount((int)scrollDistance / 18); //.scrollAmount = (int)scrollDistance / 18;
        screen.updateBackpackSlotsPosition();
        return ret;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean ret = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        screen.setScrollAmount((int)scrollDistance / 18); // = (int)scrollDistance / 18;
        screen.updateBackpackSlotsPosition();
        return ret;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isMouseOver(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setScrollDistance(int amount) {
        this.scrollDistance = (float)amount * 18;
    }
}
