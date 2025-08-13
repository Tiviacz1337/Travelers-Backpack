package com.tiviacz.travelersbackpack.inventory.upgrades.voiding;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.FilterUpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.ButtonStates;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.FilterButton;
import com.tiviacz.travelersbackpack.network.ServerboundFilterSettingsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class VoidWidget extends FilterUpgradeWidgetBase<VoidWidget, VoidUpgrade> {

    public VoidWidget(BackpackScreen screen, VoidUpgrade upgrade, Point pos) {
        super(screen, upgrade, pos, new Point(137, 0), "screen.travelersbackpack.void_upgrade");

        FilterButton<VoidWidget> whitelistButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.ALLOW_MODE), ButtonStates.ALLOW, new Point(pos.x() + 6, pos.y() + 22));
        FilterButton<VoidWidget> objectButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.OBJECT_CATEGORY), ButtonStates.OBJECT_TYPE, new Point(pos.x() + 6 + 18, pos.y() + 22));
        FilterButton<VoidWidget> ignoreModeButton = new FilterButton<>(this, upgrade.getFilter().get(VoidFilterSettings.IGNORE_MODE), ButtonStates.IGNORE_MODE, new Point(pos.x() + 6 + 36, pos.y() + 22));

        this.addFilterButton(whitelistButton);
        this.addFilterButton(objectButton);
        this.addFilterButton(ignoreModeButton);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, x, y, mouseX, mouseY);
        this.renderMatchContentsSlotOverlay(guiGraphics, upgrade.getFilter(), VoidFilterSettings.ALLOW_MODE, VoidFilterSettings.MATCH_CONTENTS, TravelersBackpackConfig.SERVER.backpackUpgrades.voidUpgradeSettings.filterSlotCount.get());

        if(isTabOpened() && !upgrade.isTagSelector()) {
            guiGraphics.fill(RenderType.guiOverlay(), pos.x() + 7, pos.y() + 44, pos.x() + 6 + 17, pos.y() + 43 + 17, 0, (0x7F << 24) | (0xC9 << 16) | (0x16 << 8) | 0x16);
        }
    }

    @Override
    public void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if(isTabOpened()) {
            if(!upgrade.isTagSelector() && this.upgrade.getTrashSlotStack().isEmpty() && isWithinBounds(mouseX, mouseY, new Point(7, 44), new Point(17, 17))) {
                guiGraphics.renderTooltip(screen.getFont(), List.of(Component.translatable("screen.travelersbackpack.void_upgrade_trash_slot"), Component.translatable("screen.travelersbackpack.void_upgrade_trash_slot_description")), Optional.empty(), mouseX, mouseY);
            }
            if(getFilterButton(ButtonStates.ALLOW).isMouseOver(mouseX, mouseY)) {
                guiGraphics.renderTooltip(screen.getFont(), WHITELIST_TOOLTIPS.get(getFilterButton(ButtonStates.ALLOW).getCurrentState()), mouseX, mouseY);
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
            Component.translatable("screen.travelersbackpack.filter_allow_voiding"),
            Component.translatable("screen.travelersbackpack.filter_block_voiding"),
            Component.translatable("screen.travelersbackpack.filter_match_contents_voiding"));

    private static final List<Component> OBJECT_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_item"),
            Component.translatable("screen.travelersbackpack.filter_modid"),
            Component.translatable("screen.travelersbackpack.filter_tag"));

    private static final List<Component> IGNORE_MODE_TOOLTIPS = List.of(
            Component.translatable("screen.travelersbackpack.filter_match_components"),
            Component.translatable("screen.travelersbackpack.filter_ignore_components"));
}