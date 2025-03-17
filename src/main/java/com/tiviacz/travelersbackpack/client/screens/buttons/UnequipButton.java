package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class UnequipButton extends Button {
    public UnequipButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 145, screen.getMiddleBar(), 12, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 63, 67, 78, 82);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            if(this.inButton(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.unequip"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!TravelersBackpack.enableIntegration()) {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton((int)mouseX, (int)mouseY)) {
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.EQUIP_BACKPACK, false);
                    return true;
                }
            }
        }
        return false;
    }
}