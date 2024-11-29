package com.tiviacz.travelersbackpackneo.client.screens.buttons;

import com.tiviacz.travelersbackpackneo.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract class Button implements IButton {
    protected final BackpackScreen screen;
    protected final int x;
    protected final int y;
    protected final int width;
    protected final int height;

    public Button(BackpackScreen screen, int x, int y, int width, int height) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawButton(GuiGraphics guiGraphics, int mouseX, int mouseY, ResourceLocation texture, int u1, int v1, int u2, int v2) {
        guiGraphics.blit(texture, screen.getGuiLeft() + x, screen.getGuiTop() + y, u1, v1, width, height);
        if(this.inButton(mouseX, mouseY)) {
            guiGraphics.blit(texture, screen.getGuiLeft() + x, screen.getGuiTop() + y, u2, v2, width, height);
        }
    }

    public boolean inButton(int mouseX, int mouseY) {
        mouseX -= screen.getGuiLeft();
        mouseY -= screen.getGuiTop();
        return x <= mouseX && mouseX <= x + width && y <= mouseY && mouseY <= y + height;
    }

    public boolean isWithinBounds(double mouseX, double mouseY, Point startPos, Point size) {
        return mouseX >= x + startPos.x() && mouseX < x + startPos.x() + size.x() && mouseY >= y + startPos.y() && mouseY < y + startPos.y() + size.y();
    }

    public boolean isWithinBounds(double mouseX, double mouseY, WidgetElement element) {
        return isWithinBounds(mouseX, mouseY, element.pos(), element.size());
    }
}