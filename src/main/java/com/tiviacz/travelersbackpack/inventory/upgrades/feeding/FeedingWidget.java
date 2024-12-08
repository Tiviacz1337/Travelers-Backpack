package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.network.ServerboundFilterSettingsPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FeedingWidget extends UpgradeWidgetBase<FeedingUpgrade> {
    private final FilterButton<FeedingWidget> whitelistButton;
    private final FilterButton<FeedingWidget> hungerModeButton;
    private final FilterButton<FeedingWidget> ignoreEffectModeButton;

    public FeedingWidget(BackpackScreen screen, FeedingUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.feeding_upgrade");

        this.whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.ALLOW_MODE), ButtonStates.ALLOW_FEEDING, new Point(pos.x() + 6, pos.y() + 22));
        this.hungerModeButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.HUNGER_MODE), ButtonStates.HUNGER_MODE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        this.ignoreEffectModeButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.IGNORE_EFFECT_MODE), ButtonStates.IGNORE_EFFECT_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        if (isTabOpened()) {
            this.whitelistButton.renderButton(guiGraphics, mouseX, mouseY);
            this.hungerModeButton.renderButton(guiGraphics, mouseX, mouseY);
            this.ignoreEffectModeButton.renderButton(guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (isTabOpened()) {
            if (this.whitelistButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), WHITELIST_TOOLTIPS.get(this.whitelistButton.getCurrentState()), mouseX, mouseY);
            }
            if (this.hungerModeButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), HUNGER_MODE_TOOLTIPS.get(this.hungerModeButton.getCurrentState()), mouseX, mouseY);
            }
            if (this.ignoreEffectModeButton.isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(this.ignoreEffectModeButton.getCurrentState()), mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        if (isTabOpened() && isBackpackOwner()) {
            if (this.whitelistButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), hungerModeButton.getCurrentState(), ignoreEffectModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
            if (this.hungerModeButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), hungerModeButton.getCurrentState(), ignoreEffectModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
            if (this.ignoreEffectModeButton.mouseClicked(pMouseX, pMouseY, pButton)) {
                PacketDistributor.sendToServer(new ServerboundFilterSettingsPacket(this.dataHolderSlot, List.of(whitelistButton.getCurrentState(), hungerModeButton.getCurrentState(), ignoreEffectModeButton.getCurrentState())));
                this.screen.playUIClickSound();
                return true;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private static final List<Component> WHITELIST_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_allow"),
            Component.translatable("screen.travelersbackpack.filter_block"));

    private static final List<Component> HUNGER_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_always_eat"),
            Component.translatable("screen.travelersbackpack.filter_half_nutrition"),
            Component.translatable("screen.travelersbackpack.filter_full_nutrition"));

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_block_bad_effects"),
            Component.translatable("screen.travelersbackpack.filter_allow_bad_effects"));
}