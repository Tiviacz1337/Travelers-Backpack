package com.tiviacz.travelersbackpack.inventory.upgrades.magnet;

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

public class MagnetWidget extends FilterUpgradeWidgetBase<MagnetWidget, MagnetUpgrade> {

    public MagnetWidget(BackpackScreen screen, MagnetUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.magnet_upgrade");

        FilterButton<MagnetWidget> whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(MagnetFilterSettings.ALLOW_MODE), ButtonStates.ALLOW_FEEDING, new Point(pos.x() + 6, pos.y() + 22));
        FilterButton<MagnetWidget> objectButton = new FilterButton<>(this, upgrade.getFilter().get(MagnetFilterSettings.OBJECT_CATEGORY), ButtonStates.OBJECT_TYPE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        FilterButton<MagnetWidget> ignoreModeButton = new FilterButton<>(this, upgrade.getFilter().get(MagnetFilterSettings.IGNORE_MODE), ButtonStates.IGNORE_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));

        this.addFilterButton(whitelistButton);
        this.addFilterButton(objectButton);
        this.addFilterButton(ignoreModeButton);
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(getFilterButton(ButtonStates.ALLOW_FEEDING).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), WHITELIST_TOOLTIPS.get(getFilterButton(ButtonStates.ALLOW_FEEDING).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.OBJECT_TYPE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), OBJECT_TOOLTIPS.get(getFilterButton(ButtonStates.OBJECT_TYPE).getCurrentState()), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.IGNORE_MODE).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), IGNORE_MODE_TOOLTIPS.get(getFilterButton(ButtonStates.IGNORE_MODE).getCurrentState()), mouseX, mouseY);
            }
        }
    }

    private static final List<Component> WHITELIST_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_allow"),
            Component.translatable("screen.travelersbackpack.filter_block"));

    private static final List<Component> OBJECT_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_item"),
            Component.translatable("screen.travelersbackpack.filter_modid"),
            Component.translatable("screen.travelersbackpack.filter_tag"));

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_match_components"),
            Component.translatable("screen.travelersbackpack.filter_ignore_components"));
}