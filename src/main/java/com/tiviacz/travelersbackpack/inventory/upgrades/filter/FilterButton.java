package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import net.minecraft.client.gui.GuiGraphics;

public class FilterButton<T extends WidgetBase> {
    private final T widget;
    private int currentState;
    private final ButtonStates.ButtonState state;
    private final Point pos;
    private boolean hidden;

    public FilterButton(T widget, int currentState, ButtonStates.ButtonState states, Point pos) {
        this.widget = widget;
        this.currentState = currentState;
        this.state = states;
        this.pos = pos;
        this.hidden = false;
    }

    public ButtonStates.ButtonState getButtonState() {
        return this.state;
    }

    public int getCurrentState() {
        return this.currentState;
    }

    public void nextState() {
        this.currentState = (this.currentState + 1) % this.state.getStatesCount();
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void renderButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(hidden) {
            return;
        }
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), this.state.getButtonIcon(this.currentState).x(), this.state.getButtonIcon(this.currentState).y(), 18, 18);

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
        if(hidden) {
            return false;
        }
        return pMouseX >= pos.x() && pMouseY >= pos.y() && pMouseX < pos.x() + 18 && pMouseY < pos.y() + 18;
    }
}
