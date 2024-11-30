package com.tiviacz.travelersbackpack.client.screens.buttons;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpackneo.initold.ModItemsNeo;
import com.tiviacz.travelersbackpackneo.network.ServerboundSleepingBagPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

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
            PacketDistributor.sendToServer(new ServerboundSleepingBagPacket(screen.getWrapper().getBackpackPos()));
            return true;
        }
        return false;
    }

    public ItemStack getSleepingBagItemFromColor(int colorId) {
        return switch(colorId) {
            case 0 -> ModItemsNeo.WHITE_SLEEPING_BAG.toStack();
            case 1 -> ModItemsNeo.ORANGE_SLEEPING_BAG.toStack();
            case 2 -> ModItemsNeo.MAGENTA_SLEEPING_BAG.toStack();
            case 3 -> ModItemsNeo.LIGHT_BLUE_SLEEPING_BAG.toStack();
            case 4 -> ModItemsNeo.YELLOW_SLEEPING_BAG.toStack();
            case 5 -> ModItemsNeo.LIME_SLEEPING_BAG.toStack();
            case 6 -> ModItemsNeo.PINK_SLEEPING_BAG.toStack();
            case 7 -> ModItemsNeo.GRAY_SLEEPING_BAG.toStack();
            case 8 -> ModItemsNeo.LIGHT_GRAY_SLEEPING_BAG.toStack();
            case 9 -> ModItemsNeo.CYAN_SLEEPING_BAG.toStack();
            case 10 -> ModItemsNeo.PURPLE_SLEEPING_BAG.toStack();
            case 11 -> ModItemsNeo.BLUE_SLEEPING_BAG.toStack();
            case 12 -> ModItemsNeo.BROWN_SLEEPING_BAG.toStack();
            case 13 -> ModItemsNeo.GREEN_SLEEPING_BAG.toStack();
            case 15 -> ModItemsNeo.BLACK_SLEEPING_BAG.toStack();
            default -> ModItemsNeo.RED_SLEEPING_BAG.toStack();
        };
    }
}