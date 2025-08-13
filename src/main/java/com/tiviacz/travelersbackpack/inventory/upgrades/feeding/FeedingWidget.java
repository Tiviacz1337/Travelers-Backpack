package com.tiviacz.travelersbackpack.inventory.upgrades.feeding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.FilterUpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.network.ServerboundFilterSettingsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class FeedingWidget extends FilterUpgradeWidgetBase<FeedingWidget, FeedingUpgrade> {
    public FeedingWidget(BackpackScreen screen, FeedingUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.feeding_upgrade");

        FilterButton<FeedingWidget> whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.ALLOW_MODE), ButtonStates.ALLOW_FEEDING, new Point(pos.x() + 6, pos.y() + 22));
        FilterButton<FeedingWidget> hungerModeButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.HUNGER_MODE), ButtonStates.HUNGER_MODE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        FilterButton<FeedingWidget> ignoreEffectModeButton = new FilterButton<>(this, upgrade.getFilter().get(FeedingFilterSettings.IGNORE_EFFECT_MODE), ButtonStates.IGNORE_EFFECT_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));

        this.addFilterButton(whitelistButton);
        this.addFilterButton(hungerModeButton);
        this.addFilterButton(ignoreEffectModeButton);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(getFilterButton(ButtonStates.ALLOW_FEEDING).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), WHITELIST_TOOLTIPS.get(getFilterButton(ButtonStates.ALLOW_FEEDING).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.HUNGER_MODE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), HUNGER_MODE_TOOLTIPS.get(getFilterButton(ButtonStates.HUNGER_MODE).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.IGNORE_EFFECT_MODE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(getFilterButton(ButtonStates.IGNORE_EFFECT_MODE).getCurrentState()), mouseX, mouseY);
            }
        }
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