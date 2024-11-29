package com.tiviacz.travelersbackpackneo.client.screens.buttons;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpackneo.capability.AttachmentUtils;
import com.tiviacz.travelersbackpackneo.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.network.ServerboundEquipBackpackPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class EquipButton extends Button {
    public EquipButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 152, screen.getImageHeight() - 95, 17, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(!AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 60, 54, 60, 54);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(TravelersBackpack.enableIntegration()) {
            if(!AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton(mouseX, mouseY)) {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.equip_integration"), mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!TravelersBackpack.enableIntegration()) {
            if(!AttachmentUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton((int)mouseX, (int)mouseY)) {
                    PacketDistributor.sendToServer(new ServerboundEquipBackpackPacket(true));
                    return true;
                }
            }
        }
        return false;
    }
}