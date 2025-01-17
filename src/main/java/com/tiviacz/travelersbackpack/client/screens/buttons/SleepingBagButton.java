package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.network.ServerboundSleepingBagPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public class SleepingBagButton extends Button {
    public SleepingBagButton(BackpackScreen screen) {
        super(screen, screen.getWidthAdditions() + 152, screen.getImageHeight() - 98, 18, 13);
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
            PacketDistributorHelper.sendToServer(new ServerboundSleepingBagPacket(screen.getWrapper().getBackpackPos()));
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