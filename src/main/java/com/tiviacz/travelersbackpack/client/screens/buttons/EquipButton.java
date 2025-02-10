package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundEquipBackpackPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class EquipButton extends Button {
    public EquipButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 157, screen.getImageHeight() - 96, 12, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if(!CapabilityUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 63, 56, 78, 82);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(!CapabilityUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
            if(this.inButton(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.equip"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(!TravelersBackpack.enableIntegration()) {
            if(!CapabilityUtils.isWearingBackpack(screen.getMenu().getPlayerInventory().player)) {
                if(this.inButton((int)mouseX, (int)mouseY)) {
                    PacketDistributorHelper.sendToServer(new ServerboundEquipBackpackPacket(true));
                    return true;
                }
            }
        }
        return false;
    }
}