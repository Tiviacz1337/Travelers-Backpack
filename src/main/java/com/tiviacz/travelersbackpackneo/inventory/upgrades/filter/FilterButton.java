package com.tiviacz.travelersbackpackneo.inventory.upgrades.filter;

import com.tiviacz.travelersbackpackneo.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import net.minecraft.client.gui.GuiGraphics;

public class FilterButton<T extends WidgetBase> {
    private T widget;
    private int currentState;
    private ButtonStates.ButtonState states;
    private Point pos;

    public FilterButton(T widget, int currentState, ButtonStates.ButtonState states, Point pos) {
        this.widget = widget;
        this.currentState = currentState;
        this.states = states;
        this.pos = pos;
    }

    public int getCurrentState() {
        return this.currentState;
    }

    public void nextState() {
        this.currentState = (this.currentState + 1) % this.states.getStatesCount();
    }

    public void renderButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), this.states.getButtonIcon(this.currentState).x(), this.states.getButtonIcon(this.currentState).y(), 18, 18);

        //Border
        if(isMouseOver(mouseX, mouseY)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 24, 18, 18, 18);
        }
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isMouseOver(pMouseX, pMouseY)) {
            this.nextState();
            return true;
        }
        return false;
    }

    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return pMouseX >= pos.x() && pMouseY >= pos.y() && pMouseX < pos.x() + 18 && pMouseY < pos.y() + 18;
    }
}
