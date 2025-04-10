package com.tiviacz.travelersbackpack.client.screens.widgets;

import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class SettingsWidget extends WidgetBase<AbstractBackpackScreen<?>> {
    private final Point tabUvOpen;
    private final Point tabUvReturn;
    private final boolean isSettingsScreen;

    public SettingsWidget(AbstractBackpackScreen<?> screen, Point pos, boolean isSettingsScreen) {
        super(screen, pos, 24, 24);
        this.tabUvOpen = new Point(42, 0);
        this.tabUvReturn = new Point(42, 18);
        this.isSettingsScreen = isSettingsScreen;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        guiGraphics.blit(BackpackScreen.ICONS, pos.x(), pos.y(), emptyTabUv.x(), emptyTabUv.y(), width, height);
        if(this.isSettingsScreen) {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, tabUvReturn.x(), tabUvReturn.y(), iconSize.x(), iconSize.y());
        } else {
            guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 3, pos.y() + 3, tabUvOpen.x(), tabUvOpen.y(), iconSize.x(), iconSize.y());
        }
    }

    public int getSettingsUser() {
        if(this.screen.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID) {
            BlockPos pos = this.screen.getWrapper().getBackpackPos();
            if(pos != null) {
                return ((BackpackBlockEntity)this.screen.getScreenPlayer().level().getBlockEntity(pos)).getSettingsUser();
            }
        }
        return -1;
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if(isMouseOver(mouseX, mouseY)) {
            if(this.isSettingsScreen) {
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings_back"), mouseX, mouseY);
            } else {
                if(this.screen.getWrapper().getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID) {
                    if(!this.screen.getWrapper().isOwner(this.screen.getScreenPlayer())) {
                        guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings_owner"), mouseX, mouseY);
                        return;
                    }
                } else if(getSettingsUser() != -1) {
                    guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings_other_player"), mouseX, mouseY);
                    return;
                }
                guiGraphics.renderTooltip(screen.getFont(), Component.translatable("screen.travelersbackpack.settings"), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(this.screen.getWrapper().getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID && !this.screen.getWrapper().isOwner(this.screen.getScreenPlayer())) {
            return false;
        }

        if(this.screen.getWrapper().getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID && (getSettingsUser() != -1 && !this.isSettingsScreen)) {
            return false;
        }

        if(isMouseOver(pMouseX, pMouseY)) {
            if(this.isSettingsScreen) {
                //Save Data if changed
                this.screen.sendDataToServer();
                //Open Normal backpack here
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SETTINGS, this.screen.getScreenPlayer().getId(), false);
            } else {
                //Open settings menu here
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.OPEN_SETTINGS, this.screen.getScreenPlayer().getId(), true);
            }
            this.screen.playUIClickSound();
            return true;
        }
        return false;
    }
}