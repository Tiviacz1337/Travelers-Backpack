package com.tiviacz.travelersbackpack.client.screens.widgets.settings;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundSlotPacket;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MemoryWidget extends SettingsWidgetBase {
    private final WidgetElement buttonElement = new WidgetElement(new Point(6, 22), new Point(18, 18));
    private final Point matchButtonUv = new Point(96, 36);
    private final Point ignoreButtonUv = new Point(114, 36);
    private final Point openTabUv = new Point(203, 0);
    private final Point iconUv = new Point(78, 0);
    private final Point iconHighlightedUv = new Point(78, 18);
    public boolean matchComponents;

    public MemoryWidget(BackpackSettingsScreen screen, Point pos, boolean matchComponents) {
        super(screen, pos, new Point(30, 46));
        this.matchComponents = matchComponents;
    }

    public boolean isTabOpened() {
        return this.tabOpened;
    }

    public void sendDataToServer() {
        if(!this.screen.memorySlots.equals(this.screen.lastMemorySlots)) {
            this.screen.memorySlots.sort(Comparator.comparingInt(Pair::getFirst));
            List<Pair<Integer, Boolean>> reducedMemoryList = new ArrayList<>();
            for(Pair<Integer, Pair<ItemStack, Boolean>> memoryPair : this.screen.memorySlots) {
                reducedMemoryList.add(Pair.of(memoryPair.getFirst(), memoryPair.getSecond().getSecond()));
            }
            PacketDistributor.sendToServer(new ServerboundSlotPacket(ServerboundSlotPacket.MEMORY, List.of(), reducedMemoryList));
            this.screen.lastMemorySlots.clear();
            this.screen.lastMemorySlots.addAll(this.screen.memorySlots);
        }
    }

    public boolean contains(int index, List<Pair<Integer, Pair<ItemStack, Boolean>>> memory) {
        for(Pair<Integer, Pair<ItemStack, Boolean>> memoryPairs : memory) {
            if(memoryPairs.getFirst() == index) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(this.tabOpened) {
            guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), openTabUv.x(), openTabUv.y(), openTabSize.x(), openTabSize.y());
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, iconHighlightedUv.x(), iconHighlightedUv.y(), iconSize.x(), iconSize.y()); //Icon Highlighted
            //Button
            Point buttonUv = this.matchComponents ? this.matchButtonUv : this.ignoreButtonUv;
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + this.buttonElement.pos().x(), pos.y() + this.buttonElement.pos().y(), buttonUv.x(), buttonUv.y(), this.buttonElement.size().x(), this.buttonElement.size().y());
        } else {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), emptyTabUv.x(), emptyTabUv.y(), width, height); //Empty Tab
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, iconUv.x(), iconUv.y(), iconSize.x(), iconSize.y()); //Icon
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isTabOpened() && isMouseOverMatchComponentsButton(mouseX, mouseY)) {
            guiGraphics.renderTooltip(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(this.matchComponents ? 0 : 1), mouseX, mouseY);
        }
        if(isMouseOverIcon(mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(screen.getFont(), TextUtils.getTranslatedSplittedText("screen.travelersbackpack.memory", null), mouseX, mouseY);
        }
    }

    public boolean isMouseOverMatchComponentsButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.buttonElement);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isTabOpened() && isMouseOverMatchComponentsButton(pMouseX, pMouseY)) {
            this.matchComponents = !this.matchComponents;
            this.screen.playUIClickSound();
            return true;
        }
        if(isMouseOverIcon(pMouseX, pMouseY)) {
            this.tabOpened = !this.tabOpened;
            //Move widgets
            this.screen.updateWidgetsPosition(this);
            //Send data to server if closed
            if(!this.tabOpened) {
                sendDataToServer();
            }
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_match_components"),
            Component.translatable("screen.travelersbackpack.filter_ignore_components"));
}