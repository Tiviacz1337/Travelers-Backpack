package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ModLootConditions
{
 /*   public static LootItemConditionType HAS_COLOR;
    public static LootItemConditionType HAS_SLEEPING_BAG_COLOR;

    public static void registerLootConditions()
    {
        HAS_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, "has_color"), new LootItemConditionType(new LootItemHasColorCondition.ConditionSerializer()));
        HAS_SLEEPING_BAG_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, "has_sleeping_bag_color"), new LootItemConditionType(new LootItemHasSleepingBagColorCondition.ConditionSerializer()));
    } */

    public static final LootConditionType HAS_COLOR = register("has_color", new LootItemHasColorCondition.ConditionSerializer());
    public static final LootConditionType HAS_SLEEPING_BAG_COLOR = register("has_sleeping_bag_color", new LootItemHasSleepingBagColorCondition.ConditionSerializer());

    public static void register() {
        // No-op method to ensure that this class is loaded and its static initialisers are run
    }

    private static LootConditionType register(final String name, final ILootSerializer<? extends ILootCondition> serializer) {
        return Registry.register(Registry.LOOT_CONDITION_TYPE, new ResourceLocation(TravelersBackpack.MODID, name), new LootConditionType(serializer));
    }
}
