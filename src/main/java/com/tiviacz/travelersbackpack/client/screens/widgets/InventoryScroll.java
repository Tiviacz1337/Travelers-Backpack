package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;

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
        screen.setScrollAmount((int)scrollDistance / 18);
        screen.updateBackpackSlotsPosition();
        return ret;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean ret = super.mouseDragged(event, deltaX, deltaY);
        screen.setScrollAmount((int)scrollDistance / 18);
        screen.updateBackpackSlotsPosition();
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean button) {
        if(isMouseOver(event.x(), event.y())) {
            return super.mouseClicked(event, button);
        }
        return false;
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void setScrollDistance(int amount) {
        this.scrollDistance = (float)amount * 18;
    }
}
