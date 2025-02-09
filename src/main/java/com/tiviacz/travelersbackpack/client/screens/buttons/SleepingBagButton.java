package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.network.ServerboundSleepingBagPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SleepingBagButton extends Button {
    private final boolean isEquipped;

    public SleepingBagButton(BackpackScreen screen, boolean isEquipped, int xOffset) {
        super(screen, screen.getWidthAdditions() + 145 - xOffset, screen.getImageHeight() - 96, 12, 12);
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
            PacketDistributor.sendToServer(new ServerboundSleepingBagPacket(this.isEquipped ? screen.getWrapper().getBackpackOwner().blockPosition() : screen.getWrapper().getBackpackPos(), this.isEquipped));
            return true;
        }
        return false;
    }

    public ItemStack getSleepingBagItemFromColor(int colorId) {
        return switch(colorId) {
            case 0 -> ModItems.WHITE_SLEEPING_BAG.getDefaultInstance();
            case 1 -> ModItems.ORANGE_SLEEPING_BAG.getDefaultInstance();
            case 2 -> ModItems.MAGENTA_SLEEPING_BAG.getDefaultInstance();
            case 3 -> ModItems.LIGHT_BLUE_SLEEPING_BAG.getDefaultInstance();
            case 4 -> ModItems.YELLOW_SLEEPING_BAG.getDefaultInstance();
            case 5 -> ModItems.LIME_SLEEPING_BAG.getDefaultInstance();
            case 6 -> ModItems.PINK_SLEEPING_BAG.getDefaultInstance();
            case 7 -> ModItems.GRAY_SLEEPING_BAG.getDefaultInstance();
            case 8 -> ModItems.LIGHT_GRAY_SLEEPING_BAG.getDefaultInstance();
            case 9 -> ModItems.CYAN_SLEEPING_BAG.getDefaultInstance();
            case 10 -> ModItems.PURPLE_SLEEPING_BAG.getDefaultInstance();
            case 11 -> ModItems.BLUE_SLEEPING_BAG.getDefaultInstance();
            case 12 -> ModItems.BROWN_SLEEPING_BAG.getDefaultInstance();
            case 13 -> ModItems.GREEN_SLEEPING_BAG.getDefaultInstance();
            case 15 -> ModItems.BLACK_SLEEPING_BAG.getDefaultInstance();
            default -> ModItems.RED_SLEEPING_BAG.getDefaultInstance();
        };
    }
}