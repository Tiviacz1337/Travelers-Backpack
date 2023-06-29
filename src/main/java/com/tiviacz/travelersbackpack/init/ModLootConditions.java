package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModLootConditions
{
    public static LootConditionType HAS_COLOR;
    public static LootConditionType HAS_SLEEPING_BAG_COLOR;

    public static void registerLootConditions()
    {
        HAS_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(TravelersBackpack.MODID, "has_color"), new LootConditionType(new LootItemHasColorCondition.ConditionSerializer()));
        HAS_SLEEPING_BAG_COLOR = Registry.register(Registry.LOOT_CONDITION_TYPE, new Identifier(TravelersBackpack.MODID, "has_sleeping_bag_color"), new LootConditionType(new LootItemHasSleepingBagColorCondition.ConditionSerializer()));
    }
}
