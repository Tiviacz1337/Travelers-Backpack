package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class WidgetBase<T extends AbstractBackpackScreen<?>> implements Renderable, GuiEventListener, NarratableEntry {
    protected final Point emptyTabUv = new Point(0, 0);
    protected final Point iconSize = new Point(18, 18);
    public final T screen;
    protected Point pos;
    protected int width;
    protected int height;

    public WidgetBase(T screen, Point pos, int width, int height) {
        this.screen = screen;
        this.pos = pos;
        this.width = width;
        this.height = height;
    }

    public Point getPos() {
        return this.pos;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {

    }

    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {

    }

    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    public void renderAboveBg(GuiGraphics guiGraphics, int xPos, int yPos, int mouseX, int mouseY, float partialTicks) {

    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX >= pos.x() && pMouseY >= pos.y() && pMouseX < pos.x() + width && pMouseY < pos.y() + height;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    public boolean isMouseOverIcon(double mouseX, double mouseY) {
        return mouseX >= pos.x() + 3 && mouseX < pos.x() + 3 + 18 && mouseY >= pos.y() + 3 && mouseY < pos.y() + 3 + 18;
    }

    public boolean in(int mouseX, int mouseY, int x, int y, int width, int height) {
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }

    public boolean isWithinBounds(double mouseX, double mouseY, Point startPos, Point size) {
        return mouseX >= pos.x() + startPos.x() && mouseX < pos.x() + startPos.x() + size.x() && mouseY >= pos.y() + startPos.y() && mouseY < pos.y() + startPos.y() + size.y();
    }

    public boolean isWithinBounds(double mouseX, double mouseY, WidgetElement element) {
        return isWithinBounds(mouseX, mouseY, element.pos(), element.size());
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    public int[] getWidgetSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x();
        size[1] = pos.y();
        size[2] = width;
        size[3] = height;
        return size;
    }
}
