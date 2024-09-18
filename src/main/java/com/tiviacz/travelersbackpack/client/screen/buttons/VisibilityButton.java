package com.tiviacz.travelersbackpack.client.screen.buttons;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.network.SpecialActionPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class VisibilityButton extends Button
{
    public VisibilityButton(TravelersBackpackHandledScreen screen) {
        super(screen, 225, 42 + screen.inventory.getYOffset(), 18, 18);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            boolean visibility = screen.inventory.getItemStack().getOrDefault(ModComponentTypes.VISIBILITY, true);
            if (visibility) {
                context.drawTexture(TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getX() + this.x, screen.getY() + this.y, 38, 38, this.width, 18);
            } else {
                context.drawTexture(TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getX() + this.x, screen.getY() + this.y, 57, 38, this.width, 18);
            }
            if (this.inButton(mouseX, mouseY)) {
                context.drawTexture(TravelersBackpackHandledScreen.EXTRAS_TRAVELERS_BACKPACK, screen.getX() + this.x, screen.getY() + this.y, 19, 0, this.width, 18);
            }
        }
    }

    @Override
    public void drawMouseoverTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.inButton(mouseX, mouseY) && screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            boolean visibility = screen.inventory.getItemStack().getOrDefault(ModComponentTypes.VISIBILITY, true);
            if (visibility) {
                context.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.hide_backpack"), mouseX, mouseY);
            } else {
                context.drawTooltip(screen.getTextRenderer(), Text.translatable("screen.travelersbackpack.show_backpack"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.inButton((int)mouseX, (int)mouseY) && screen.settingsWidget.isWidgetActive() && !screen.isWidgetVisible(3, screen.rightTankSlotWidget)) {
            ClientPlayNetworking.send(new SpecialActionPacket(screen.inventory.getScreenID(), Reference.TOGGLE_VISIBILITY, 0.0D));
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}