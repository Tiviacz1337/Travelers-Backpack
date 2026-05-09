package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SleepingBagButton extends Button {
    private final boolean isEquipped;

    public SleepingBagButton(BackpackScreen screen, boolean isEquipped, int xOffset) {
        super(screen, screen.getWidthAdditions() + 145 - xOffset, screen.getMiddleBar(), 12, 12);
        this.isEquipped = isEquipped;
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        drawButton(guiGraphics, mouseX, mouseY, BackpackScreen.ICONS, 91, 83, 78, 82);
    }

    @Override
    public void renderTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if(inButton(mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.travelersbackpack.use_sleeping_bag"));
            tooltip.add(Component.translatable("screen.travelersbackpack.detach_sleeping_bag"));
            guiGraphics.setTooltipForNextFrame(screen.getFont(), tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if(this.inButton(event)) {
            if(this.isEquipped && screen.getWrapper().getBackpackOwner() == null) {
                return false;
            }
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SLEEPING_BAG, this.isEquipped ? screen.getWrapper().getBackpackOwner().blockPosition() : screen.getWrapper().getBackpackPos(), this.isEquipped, KeyHelper.isShiftPressed());
            return true;
        }
        return false;
    }
}