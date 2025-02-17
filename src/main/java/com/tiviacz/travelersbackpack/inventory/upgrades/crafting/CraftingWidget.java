package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundTabPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class CraftingWidget extends UpgradeWidgetBase<CraftingUpgrade> {
    public final WidgetElement arrowElement = new WidgetElement(new Point(8, 91), new Point(12, 12));

    public CraftingWidget(BackpackScreen screen, CraftingUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(51, 0), "screen.travelersbackpack.crafting_upgrade");
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);

        if(isTabOpened()) {
            //guiGraphics.blit(TravelersBackpackScreenNew.EXTRAS, pos.x(), pos.y(), 52, 55, width, height);

            if(this.upgrade.shiftClickToBackpack()) {
                guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, pos.x() + arrowElement.pos().x(), pos.y() + arrowElement.pos().y(), 12, 55, arrowElement.size().x(), arrowElement.size().y(), 256, 256);
            } else {
                guiGraphics.blit(RenderType::guiTextured, BackpackScreen.ICONS, pos.x() + arrowElement.pos().x(), pos.y() + arrowElement.pos().y(), 0, 55, arrowElement.size().x(), arrowElement.size().y(), 256, 256);
            }
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened() && isMouseOverShiftClickButton(mouseX, mouseY)) {
            if(this.upgrade.shiftClickToBackpack()) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.crafting_to_backpack"), mouseX, mouseY);
            } else {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.crafting_to_player"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isMouseOverShiftClickButton(pMouseX, pMouseY)) {
            if(isTabOpened()) {
                PacketDistributor.sendToServer(new ServerboundTabPacket(this.dataHolderSlot, !this.upgrade.shiftClickToBackpack(), ServerboundTabPacket.SHIFT_CLICK_TO_BACKPACK));
                this.screen.playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    public boolean isMouseOverShiftClickButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.arrowElement);
    }
}