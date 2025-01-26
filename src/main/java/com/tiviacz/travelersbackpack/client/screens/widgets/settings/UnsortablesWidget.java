package com.tiviacz.travelersbackpack.client.screens.widgets.settings;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetElement;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundSlotPacket;
import com.tiviacz.travelersbackpack.util.TextUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UnsortablesWidget extends SettingsWidgetBase {
    private final WidgetElement selectAllButton = new WidgetElement(new Point(6, 22), new Point(18, 18));
    private final WidgetElement removeAllButton = new WidgetElement(new Point(24, 22), new Point(18, 18));
    private final Point openTabUv = new Point(0, 72);
    private final Point iconUv = new Point(60, 0);
    private final Point iconHighlightedUv = new Point(60, 18);

    public UnsortablesWidget(BackpackSettingsScreen screen, Point pos) {
        super(screen, pos, new Point(48, 46));
    }

    public boolean isTabOpened() {
        return this.tabOpened;
    }

    public void sendDataToServer() {
        if(!this.screen.unsortableSlots.equals(this.screen.lastUnsortableSlots)) {
            Collections.sort(this.screen.unsortableSlots);
            PacketDistributor.sendToServer(new ServerboundSlotPacket(ServerboundSlotPacket.UNSORTABLES, this.screen.unsortableSlots, List.of()));
            this.screen.lastUnsortableSlots.clear();
            this.screen.lastUnsortableSlots.addAll(this.screen.unsortableSlots);
        }
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        if(this.tabOpened) {
            guiGraphics.blit(BackpackScreen.TABS, pos.x(), pos.y(), openTabUv.x(), openTabUv.y(), openTabSize.x(), openTabSize.y());
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, iconHighlightedUv.x(), iconHighlightedUv.y(), iconSize.x(), iconSize.y()); //Icon Highlighted
            //Buttons
            guiGraphics.blit(BackpackScreen.ICONS, this.pos.x() + this.selectAllButton.pos().x(), this.pos.y() + this.selectAllButton.pos().y(), 132, 18, this.selectAllButton.size().x(), this.selectAllButton.size().y());
            if(isMouseOverSelectAllButton(mouseX, mouseY)) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + selectAllButton.pos().x(), pos.y() + selectAllButton.pos().y(), 24, 18, selectAllButton.size().x(), selectAllButton.size().y());
            }
            guiGraphics.blit(BackpackScreen.ICONS, this.pos.x() + this.removeAllButton.pos().x(), this.pos.y() + this.removeAllButton.pos().y(), 132, 36, this.removeAllButton.size().x(), this.removeAllButton.size().y());
            if(isMouseOverRemoveAllButton(mouseX, mouseY)) {
                guiGraphics.blit(BackpackScreen.ICONS, pos.x() + removeAllButton.pos().x(), pos.y() + removeAllButton.pos().y(), 24, 18, removeAllButton.size().x(), removeAllButton.size().y());
            }
        } else {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), emptyTabUv.x(), emptyTabUv.y(), width, height); //Empty Tab
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, iconUv.x(), iconUv.y(), iconSize.x(), iconSize.y()); //Icon
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isMouseOverIcon(mouseX, mouseY) || (this.tabOpened && (isMouseOverSelectAllButton(mouseX, mouseY) || isMouseOverRemoveAllButton(mouseX, mouseY)))) {
            List<Component> components = new ArrayList<>(TextUtils.getTranslatedSplittedText("screen.travelersbackpack.unsortable", null));
            if(this.tabOpened && isMouseOverSelectAllButton(mouseX, mouseY)) {
                components.add(Component.translatable("screen.travelersbackpack.select_all"));
            }
            if(this.tabOpened && isMouseOverRemoveAllButton(mouseX, mouseY)) {
                components.add(Component.translatable("screen.travelersbackpack.remove_all"));
            }
            guiGraphics.renderComponentTooltip(screen.getFont(), components, mouseX, mouseY);
        }
    }

    public boolean isMouseOverSelectAllButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.selectAllButton);
    }

    public boolean isMouseOverRemoveAllButton(double mouseX, double mouseY) {
        return isWithinBounds(mouseX, mouseY, this.removeAllButton);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isTabOpened()) {
            if(isMouseOverSelectAllButton(pMouseX, pMouseY)) {
                for(int i = 0; i < screen.getWrapper().getStorage().getSlots(); i++) {
                    this.screen.unsortableSlots.add(i);
                }
                this.screen.playUIClickSound();
                return true;
            }
            if(isMouseOverRemoveAllButton(pMouseX, pMouseY)) {
                this.screen.unsortableSlots.clear();
                this.screen.playUIClickSound();
                return true;
            }
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

    @Override
    public int[] getWidgetSizeAndPos() {
        int[] size = new int[4];
        size[0] = pos.x();
        size[1] = pos.y();
        size[2] = this.tabOpened ? openTabSize.x() : width;
        size[3] = this.tabOpened ? openTabSize.y() : height;
        return size;
    }
}