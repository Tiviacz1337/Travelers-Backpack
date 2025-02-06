package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.network.ServerboundSleepingBagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class SleepingBagButton extends Button {
    private final boolean isEquipped;

    public SleepingBagButton(BackpackScreen screen, boolean isEquipped) {
        super(screen, screen.getWidthAdditions() + (isEquipped ? 134 : 152), screen.getImageHeight() - 98, 18, 13);
        this.isEquipped = isEquipped;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.renderItem(getSleepingBagItemFromColor(screen.getWrapper().getSleepingBagColor()), screen.getGuiLeft() + x, screen.getGuiTop() + y);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
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
            case 0 -> ModItems.WHITE_SLEEPING_BAG.toStack();
            case 1 -> ModItems.ORANGE_SLEEPING_BAG.toStack();
            case 2 -> ModItems.MAGENTA_SLEEPING_BAG.toStack();
            case 3 -> ModItems.LIGHT_BLUE_SLEEPING_BAG.toStack();
            case 4 -> ModItems.YELLOW_SLEEPING_BAG.toStack();
            case 5 -> ModItems.LIME_SLEEPING_BAG.toStack();
            case 6 -> ModItems.PINK_SLEEPING_BAG.toStack();
            case 7 -> ModItems.GRAY_SLEEPING_BAG.toStack();
            case 8 -> ModItems.LIGHT_GRAY_SLEEPING_BAG.toStack();
            case 9 -> ModItems.CYAN_SLEEPING_BAG.toStack();
            case 10 -> ModItems.PURPLE_SLEEPING_BAG.toStack();
            case 11 -> ModItems.BLUE_SLEEPING_BAG.toStack();
            case 12 -> ModItems.BROWN_SLEEPING_BAG.toStack();
            case 13 -> ModItems.GREEN_SLEEPING_BAG.toStack();
            case 15 -> ModItems.BLACK_SLEEPING_BAG.toStack();
            default -> ModItems.RED_SLEEPING_BAG.toStack();
        };
    }
}