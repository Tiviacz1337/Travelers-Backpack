package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.forge.REIPluginClient;

import java.util.ArrayList;
import java.util.List;

@REIPluginClient
public class ReiClientCompat implements REIClientPlugin {
    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(BackpackSettingsScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            screen.children().stream().filter(w -> w instanceof WidgetBase).forEach(widget -> {
                int[] size = ((WidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            return ret;
        });

        zones.register(BackpackScreen.class, screen -> {
            List<Rectangle> ret = new ArrayList<>();
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            ret.add(new Rectangle(s[0], s[1], s[2], s[3]));

            screen.children().stream().filter(w -> w instanceof UpgradeWidgetBase).forEach(widget -> {
                int[] size = ((UpgradeWidgetBase)widget).getWidgetSizeAndPos();
                ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
            });
            screen.upgradeSlots.forEach(slot -> {
                if(!slot.isHidden()) {
                    int[] size = slot.getUpgradeSlotSizeAndPos();
                    ret.add(new Rectangle(size[0], size[1], size[2], size[3]));
                }
            });
            return ret;
        });
    }
}