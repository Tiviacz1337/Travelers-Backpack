package com.tiviacz.travelersbackpack.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class TravelersBackpackConfig
{
    public static TravelersBackpackConfigData getConfig()
    {
        return AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).getConfig();
    }

    public static void register()
    {
        AutoConfig.register(TravelersBackpackConfigData.class, JanksonConfigSerializer::new);

        // Listen for when the server is reloading (i.e. /reload), and reload the config
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((s, m) ->
                AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load());
    }

    public static void loadItemsFromConfig(String[] configArray, List<Item> targetList)
    {
        for(String registryName : configArray)
        {
            Identifier id = Identifier.tryParse(registryName);

            if(Registry.ITEM.getOrEmpty(id).isPresent())
            {
                targetList.add(Registry.ITEM.get(id));
            }
        }
    }

    public static void loadEntityTypesFromConfig(String[] configArray, List<EntityType> targetList)
    {
        for(String registryName : configArray)
        {
            Identifier id = Identifier.tryParse(registryName);

            if(Registry.ENTITY_TYPE.getOrEmpty(id).isPresent())
            {
                targetList.add(Registry.ENTITY_TYPE.get(id));
            }
        }
    }
}