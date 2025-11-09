package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EquipButton extends Button {
    public EquipButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 157, screen.getMiddleBar(), 12, 12);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 63, 56, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(this.inButton(mouseX, mouseY)) {
            guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.equip"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean button) {
        if(!TravelersBackpack.enableIntegration()) {
            if(this.inButton(event)) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.EQUIP_BACKPACK, true);
                return true;
            }
        }
        return false;
    }
}