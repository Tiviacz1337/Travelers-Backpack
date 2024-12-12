package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundSorterPacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class SortingButtons extends WidgetBase<BackpackScreen> {
    public SortingButtons(BackpackScreen screen, Point pos, int width, int height) {
        super(screen, pos, width, height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 77, 54, width, height);
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.SORT)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 4, pos.y() + 4, 81, 71, 9, 9);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.QUICK_STACK)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 15, pos.y() + 4, 92, 71, 9, 9);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 26, pos.y() + 4, 103, 71, 9, 9);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 37, pos.y() + 4, 114, 71, 9, 9);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(BackpackDeathHelper.isShiftPressed()) {
            if(isButtonHovered(pos, mouseX, mouseY, Buttons.SORT)) {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(Component.translatable("screen.travelersbackpack.sort").getVisualOrderText());

                guiGraphics.renderTooltip(screen.getFont(), list, mouseX, mouseY);
            }
            if(isButtonHovered(pos, mouseX, mouseY, Buttons.QUICK_STACK)) {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(Component.translatable("screen.travelersbackpack.quick_stack").getVisualOrderText());
                list.add(Component.translatable("screen.travelersbackpack.quick_stack_shift").getVisualOrderText());

                guiGraphics.renderTooltip(screen.getFont(), list, mouseX, mouseY);
            }
            if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
                List<FormattedCharSequence> list = new ArrayList<>();
                list.add(Component.translatable("screen.travelersbackpack.transfer_to_backpack").getVisualOrderText());
                list.add(Component.translatable("screen.travelersbackpack.transfer_to_backpack_shift").getVisualOrderText());

                guiGraphics.renderTooltip(screen.getFont(), list, mouseX, mouseY);
            }
            if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER)) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.SORT)) {
            PacketDistributor.sendToServer(new ServerboundSorterPacket(screen.getWrapper().getScreenID(), ContainerSorter.SORT_BACKPACK, BackpackDeathHelper.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.QUICK_STACK)) {
            PacketDistributor.sendToServer(new ServerboundSorterPacket(screen.getWrapper().getScreenID(), ContainerSorter.QUICK_STACK, BackpackDeathHelper.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
            PacketDistributor.sendToServer(new ServerboundSorterPacket(screen.getWrapper().getScreenID(), ContainerSorter.TRANSFER_TO_BACKPACK, BackpackDeathHelper.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_PLAYER)) {
            PacketDistributor.sendToServer(new ServerboundSorterPacket(screen.getWrapper().getScreenID(), ContainerSorter.TRANSFER_TO_PLAYER, BackpackDeathHelper.isShiftPressed()));
            screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public boolean isButtonHovered(int mouseX, int mouseY, Buttons button) {
        return (65 + button.ordinal() * 11) + screen.getGuiLeft() <= mouseX && mouseX <= (65 + button.ordinal() * 11) + 8 + screen.getGuiLeft() && -6 + screen.getGuiTop() <= mouseY && mouseY <= -6 + 8 + screen.getGuiTop();
    }

    public boolean isButtonHovered(Point pos, int mouseX, int mouseY, Buttons button) {
        //return x <= mouseX && mouseX < x + 8 && y <= mouseY && mouseY < y + 8;
        return (pos.x() + 4 + button.ordinal() * 11) <= mouseX && mouseX <= (pos.x() + 4 + button.ordinal() * 11) + 8 && (pos.y() + 4) <= mouseY && mouseY <= (pos.y() + 4) + 8;
    }

    public enum Buttons {
        SORT,
        QUICK_STACK,
        TRANSFER_TO_BACKPACK,
        TRANSFER_TO_PLAYER
    }
}