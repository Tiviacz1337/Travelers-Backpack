package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.ResultArrowElement;
import net.minecraft.client.gui.GuiGraphics;

public class CraftingWidget extends UpgradeWidgetBase<CraftingUpgrade> {
    private final ResultArrowElement resultArrowElement;
    public final WidgetElement arrowElement = new WidgetElement(new Point(8, 91), new Point(12, 12));

    public CraftingWidget(BackpackScreen screen, CraftingUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(51, 0), "screen.travelersbackpack.crafting_upgrade");
        this.resultArrowElement = new ResultArrowElement(screen, this, this.arrowElement);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        this.resultArrowElement.renderBg(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.resultArrowElement.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.resultArrowElement.mouseClicked(pMouseX, pMouseY, pButton)) {
            return true;
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }
}