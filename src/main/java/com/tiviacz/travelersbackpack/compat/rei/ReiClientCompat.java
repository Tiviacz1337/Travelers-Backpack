package com.tiviacz.travelersbackpack.compat.rei;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.forge.REIPluginClient;

import java.util.ArrayList;
import java.util.List;

@REIPluginClient
public class ReiClientCompat implements REIClientPlugin
{
    @Override
    public void registerExclusionZones(ExclusionZones zones)
    {
        zones.register(TravelersBackpackScreen.class, screen ->
        {
            List<Rectangle> ret = new ArrayList<>();
            int[] s = screen.settingsWidget.getWidgetSizeAndPos();
            ret.add(new Rectangle(s[0], s[1], s[2], s[3]));
            int[] sort = screen.sortWidget.getWidgetSizeAndPos();
            if (screen.sortWidget.isVisible()) ret.add(new Rectangle(sort[0], sort[1], sort[2], sort[3]));
            int[] memory = screen.memoryWidget.getWidgetSizeAndPos();
            if (screen.memoryWidget.isVisible()) ret.add(new Rectangle(memory[0], memory[1], memory[2], memory[3]));
            int[] crafting = screen.craftingWidget.getWidgetSizeAndPos();
            if(screen.craftingWidget.isVisible()) ret.add(new Rectangle(crafting[0], crafting[1], crafting[2], crafting[3]));
            return ret;
        });
    }
}