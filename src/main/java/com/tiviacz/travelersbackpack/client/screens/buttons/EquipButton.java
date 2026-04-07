package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class EquipButton extends Button {
    private final boolean mainHand;

    public EquipButton(BackpackScreen screen, boolean mainHand) {
        super(screen, screen.getWidthAdditions() + 157, screen.getMiddleBar(), 12, 12);
        this.mainHand = mainHand;
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 63, 56, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(this.inButton(mouseX, mouseY)) {
            if(this.mainHand) {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.equip"), mouseX, mouseY);
            } else {
                guiGraphics.setTooltipForNextFrame(screen.getFont(), Component.translatable("screen.travelersbackpack.open_in_hand"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(!TravelersBackpack.enableIntegration() && this.mainHand) {
            if(this.inButton(event)) {
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.EQUIP_BACKPACK, true);
                return true;
            }
        }
        return false;
    }
}