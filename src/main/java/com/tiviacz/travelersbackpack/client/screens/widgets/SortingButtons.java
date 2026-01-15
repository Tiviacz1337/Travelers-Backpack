package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.inventory.sorter.SortSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.KeyHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SortingButtons extends WidgetBase<BackpackScreen> {
    public SortingButtons(BackpackScreen screen, Point pos, int width, int height) {
        super(screen, pos, width, height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), 77, 54, width, height);
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.SORT)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 1, pos.y() + 2, 78, 69, 12, 12);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.QUICK_STACK)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 13, pos.y() + 2, 90, 69, 12, 12);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 25, pos.y() + 2, 102, 69, 12, 12);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER)) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 37, pos.y() + 2, 114, 69, 12, 12);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.SORT)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("screen.travelersbackpack.sort"));
            list.add(Component.translatable("screen.travelersbackpack.sort_shift"));
            list.add(Component.translatable("screen.travelersbackpack.sort_" + screen.getWrapper().getSortType().name().toLowerCase()));
            guiGraphics.renderTooltip(screen.getFont(), list, Optional.empty(), mouseX, mouseY);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.QUICK_STACK)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("screen.travelersbackpack.quick_stack"));
            list.add(Component.translatable("screen.travelersbackpack.quick_stack_shift"));
            guiGraphics.renderTooltip(screen.getFont(), list, Optional.empty(), mouseX, mouseY);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
            List<Component> list = new ArrayList<>();
            list.add(Component.translatable("screen.travelersbackpack.transfer_to_backpack"));
            list.add(Component.translatable("screen.travelersbackpack.transfer_to_backpack_shift"));
            guiGraphics.renderTooltip(screen.getFont(), list, Optional.empty(), mouseX, mouseY);
        }
        if(isButtonHovered(pos, mouseX, mouseY, Buttons.TRANSFER_TO_PLAYER)) {
            guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.transfer_to_player"), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.SORT)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.SORT_BACKPACK, KeyHelper.isShiftPressed());
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.QUICK_STACK)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.QUICK_STACK, KeyHelper.isShiftPressed());
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_BACKPACK)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.TRANSFER_TO_BACKPACK, KeyHelper.isShiftPressed());
            screen.playUIClickSound();
            return true;
        }
        if(isButtonHovered(pos, (int)mouseX, (int)mouseY, Buttons.TRANSFER_TO_PLAYER)) {
            ServerboundActionTagPacket.create(ServerboundActionTagPacket.SORTER, ContainerSorter.TRANSFER_TO_PLAYER, KeyHelper.isShiftPressed());
            screen.playUIClickSound();
            return true;
        }
        return false;
    }

    public boolean isButtonHovered(int mouseX, int mouseY, Buttons button) {
        return (65 + button.ordinal() * 11) + screen.getGuiLeft() <= mouseX && mouseX <= (65 + button.ordinal() * 11) + 8 + screen.getGuiLeft() && -6 + screen.getGuiTop() <= mouseY && mouseY <= -6 + 8 + screen.getGuiTop();
    }

    public boolean isButtonHovered(Point pos, int mouseX, int mouseY, Buttons button) {
        int buttonX = pos.x() + 2 + button.ordinal() * 12;
        int buttonY = pos.y() + 3;
        return buttonX <= mouseX && mouseX < buttonX + 10 && buttonY <= mouseY && mouseY < buttonY + 10;
    }

    public enum Buttons {
        SORT,
        QUICK_STACK,
        TRANSFER_TO_BACKPACK,
        TRANSFER_TO_PLAYER
    }
}