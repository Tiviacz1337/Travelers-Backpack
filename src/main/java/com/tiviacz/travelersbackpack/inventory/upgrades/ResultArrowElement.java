package com.tiviacz.travelersbackpack.inventory.upgrades;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;

public class ResultArrowElement {
    private final BackpackScreen backpackScreen;
    private final UpgradeWidgetBase<?> upgradeWidgetBase;
    private final WidgetElement arrowElement;

    public ResultArrowElement(BackpackScreen backpackScreen, UpgradeWidgetBase<?> upgradeWidgetBase, WidgetElement arrowElement) {
        this.backpackScreen = backpackScreen;
        this.upgradeWidgetBase = upgradeWidgetBase;
        this.arrowElement = arrowElement;
    }

    public void renderBg(GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(upgradeWidgetBase.isTabOpened() && upgradeWidgetBase.getUpgrade() instanceof IMoveSelector selector) {
            if(selector.shiftClickToBackpack(upgradeWidgetBase.getUpgrade().getDataHolderStack())) {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, upgradeWidgetBase.getPos().x() + arrowElement.pos().x(), upgradeWidgetBase.getPos().y() + arrowElement.pos().y(), 12, 55, arrowElement.size().x(), arrowElement.size().y(), 256, 256);
            } else {
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, BackpackScreen.ICONS, upgradeWidgetBase.getPos().x() + arrowElement.pos().x(), upgradeWidgetBase.getPos().y() + arrowElement.pos().y(), 0, 55, arrowElement.size().x(), arrowElement.size().y(), 256, 256);
            }
        }
    }

    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(upgradeWidgetBase.isTabOpened() && isMouseOverShiftClickButton(mouseX, mouseY) && upgradeWidgetBase.getUpgrade() instanceof IMoveSelector selector) {
            if(selector.shiftClickToBackpack(upgradeWidgetBase.getUpgrade().getDataHolderStack())) {
                guiGraphics.setTooltipForNextFrame(backpackScreen.getFont(), Component.translatable("screen.travelersbackpack.crafting_to_backpack"), mouseX, mouseY);
            } else {
                guiGraphics.setTooltipForNextFrame(backpackScreen.getFont(), Component.translatable("screen.travelersbackpack.crafting_to_player"), mouseX, mouseY);
            }
        }
    }

    public boolean mouseClicked(MouseButtonEvent event) {
        if(isMouseOverShiftClickButton(event.x(), event.y())) {
            if(upgradeWidgetBase.isTabOpened() && upgradeWidgetBase.getUpgrade() instanceof IMoveSelector selector) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.UPGRADE_TAB, upgradeWidgetBase.getUpgrade().getDataHolderSlot(), !selector.shiftClickToBackpack(upgradeWidgetBase.getUpgrade().getDataHolderStack()), ServerActions.SHIFT_CLICK_TO_BACKPACK);
                backpackScreen.playUIClickSound();
                return true;
            }
        }
        return false;
    }

    public boolean isMouseOverShiftClickButton(double mouseX, double mouseY) {
        return upgradeWidgetBase.isWithinBounds(mouseX, mouseY, arrowElement);
    }
}