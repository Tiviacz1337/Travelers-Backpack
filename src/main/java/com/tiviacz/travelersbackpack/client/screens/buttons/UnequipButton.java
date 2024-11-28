package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundEquipBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class UnequipButton extends Button {
    public UnequipButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 152, screen.getImageHeight() - 95, 17, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 60, 66, 60, 66);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(TravelersBackpack.enableIntegration()) {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton(mouseX, mouseY)) {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.unequip_integration"), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!TravelersBackpack.enableIntegration()) {
            if(AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton((int)mouseX, (int)mouseY)) {
                    PacketDistributorHelper.sendToServer(new ServerboundEquipBackpackPacket(false));
                    return true;
                }
            }
        }
        return false;
    }
}