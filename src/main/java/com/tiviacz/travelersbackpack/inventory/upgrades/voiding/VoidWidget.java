package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.network.ServerboundFilterSettingsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class VoidWidget extends UpgradeWidgetBase<VoidUpgrade> {
    private final FilterButton<VoidWidget> whitelistButton;
    private final FilterButton<VoidWidget> objectButton;
    private final FilterButton<VoidWidget> ignoreModeButton;

    public VoidWidget(BackpackScreen screen, VoidUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.void_upgrade");

        this.whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.ALLOW_MODE), ButtonStates.ALLOW, new Point(pos.x() + 6, pos.y() + 22));
        this.objectButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.OBJECT_CATEGORY), ButtonStates.OBJECT_TYPE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        this.ignoreModeButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.IGNORE_MODE), ButtonStates.IGNORE_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);

        this.renderMatchContentsSlotOverlay(guiGraphics, upgrade.getFilter(), VoidFilterSettings.ALLOW_MODE, VoidFilterSettings.MATCH_CONTENTS, TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get());
        /*if(isTabOpened()) {
            if(upgrade.getFilter().get(VoidFilterSettings.ALLOW_MODE) == VoidFilterSettings.MATCH_CONTENTS) {
                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < 3; j++) {
                        guiGraphics.blit(BackpackScreen.ICONS, pos.x() + 6 + 18 * i, pos.y() + 43 + 18 * j, 24, 36, 18, 18);
                    }
                }
            }
        } */
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if(isTabOpened()) {
            this.whitelistButton.renderButton(guiGraphics, mouseX, mouseY);
            this.objectButton.renderButton(guiGraphics, mouseX, mouseY);
            this.ignoreModeButton.renderButton(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(this.whitelistButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), WHITELIST_TOOLTIPS.get(this.whitelistButton.getCurrentState()), mouseX, mouseY);
            }
            if(this.objectButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), OBJECT_TOOLTIPS.get(this.objectButton.getCurrentState()), mouseX, mouseY);
            }
            if(this.ignoreModeButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(this.ignoreModeButton.getCurrentState()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if(isTabOpened() && isBackpackOwner()) {
            if(this.whitelistButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), objectButton.getCurrentState(), ignoreModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
            if(this.objectButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), objectButton.getCurrentState(), ignoreModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
            if(this.ignoreModeButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), objectButton.getCurrentState(), ignoreModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static final List<Component> WHITELIST_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_allow_voiding"),
            Component.translatable("screen.travelersbackpack.filter_block_voiding"),
            Component.translatable("screen.travelersbackpack.filter_match_contents_voiding"));

    private static final List<Component> OBJECT_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_item"),
            Component.translatable("screen.travelersbackpack.filter_modid"));

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_match_components"),
            Component.translatable("screen.travelersbackpack.filter_ignore_components"));
}