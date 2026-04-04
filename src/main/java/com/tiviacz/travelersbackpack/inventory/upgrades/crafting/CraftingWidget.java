package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.BackpackCraftingGridAddition;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.ICraftingTweaks;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.ResultArrowElement;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

public class CraftingWidget extends UpgradeWidgetBase<CraftingUpgrade> {
    private final ResultArrowElement resultArrowElement;
    public final WidgetElement arrowElement = new WidgetElement(new Point(8, 91), new Point(12, 12));
    private static ICraftingTweaks craftingTweaksAddition = ICraftingTweaks.EMPTY;

    public CraftingWidget(BackpackScreen screen, CraftingUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(51, 0), "screen.travelersbackpack.crafting_upgrade");
        this.resultArrowElement = new ResultArrowElement(screen, this, this.arrowElement);
        this.getCraftingTweaksAddition().setScreen(screen);
        if(isTabOpened()) {
            this.getCraftingTweaksAddition().onCraftingSlotsDisplayed();
        }
    }

    @Override
    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(isTabOpened()) {
            if(isCraftingTweaksAdditionEnabled()) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS, pos.x(), pos.y(), tabUv.x(), tabUv.y(), width - 19, height, 256, 256);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS, pos.x() + width - 20, pos.y(), tabUv.x() + 66, tabUv.y(), 20, height, 256, 256);
            } else {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.TABS, pos.x(), pos.y(), tabUv.x(), tabUv.y(), width, height, 256, 256);
            }
            guiGraphics.item(StacksHandlerUtils.getStackInSlot(screen.getWrapper().getUpgrades(), this.dataHolderSlot), pos.x() + 4, pos.y() + 4);
        }
        this.resultArrowElement.renderBg(guiGraphics, x, y, mouseX, mouseY);
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);
        this.resultArrowElement.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean pButton) {
        if(this.resultArrowElement.mouseClicked(event)) {
            return true;
        }
        if(isMouseOverIcon(event)) {
            if(this.upgrade.isTabOpened()) {
                craftingTweaksAddition.onCraftingSlotsHidden();
            } else {
                craftingTweaksAddition.onCraftingSlotsDisplayed();
            }
        }
        return super.mouseClicked(event, pButton);
    }

    public static void setCraftingTweaksAddition(ICraftingTweaks addition) {
        craftingTweaksAddition = addition;
    }

    public ICraftingTweaks getCraftingTweaksAddition() {
        return craftingTweaksAddition;
    }

    public boolean isCraftingTweaksAdditionEnabled() {
        return craftingTweaksAddition instanceof BackpackCraftingGridAddition;
    }
}