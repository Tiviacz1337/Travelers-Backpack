package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class ModLootConditions
{
 /*   public static LootItemConditionType HAS_COLOR;
    public static LootItemConditionType HAS_SLEEPING_BAG_COLOR;

    public static void registerLootConditions()
    {
        HAS_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, "has_color"), new LootItemConditionType(new LootItemHasColorCondition.ConditionSerializer()));
        HAS_SLEEPING_BAG_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, "has_sleeping_bag_color"), new LootItemConditionType(new LootItemHasSleepingBagColorCondition.ConditionSerializer()));
    } */

    public static final LootItemConditionType HAS_COLOR = register("has_color", new LootItemHasColorCondition.ConditionSerializer());
    public static final LootItemConditionType HAS_SLEEPING_BAG_COLOR = register("has_sleeping_bag_color", new LootItemHasSleepingBagColorCondition.ConditionSerializer());

    public static void register() {
        // No-op method to ensure that this class is loaded and its static initialisers are run
    }

    private static LootItemConditionType register(final String name, final Serializer<? extends LootItemCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, name), new LootItemConditionType(serializer));
    }
}
