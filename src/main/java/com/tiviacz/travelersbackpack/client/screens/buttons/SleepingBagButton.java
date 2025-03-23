package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SleepingBagButton extends Button {
    private final boolean isEquipped;

    public SleepingBagButton(BackpackScreen screen, boolean isEquipped, int xOffset) {
        super(screen, screen.getWidthAdditions() + 145 - xOffset, screen.getMiddleBar(), 12, 12);
        this.isEquipped = isEquipped;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 91, 83, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(inButton(mouseX, mouseY)) {
            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.use_sleeping_bag"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(this.inButton((int)mouseX, (int)mouseY)) {
            if(this.isEquipped && screen.getWrapper().getBackpackOwner() == null) {
                return false;
            }
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SLEEPING_BAG, this.isEquipped ? screen.getWrapper().getBackpackOwner().blockPosition() : screen.getWrapper().getBackpackPos(), this.isEquipped);
            return true;
        }
        return false;
    }
}