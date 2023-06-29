package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasColorCondition;
import com.tiviacz.travelersbackpack.datagen.loot.LootItemHasSleepingBagColorCondition;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModLootConditions
{
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = DeferredRegister.create(Registry.LOOT_ITEM_REGISTRY, TravelersBackpack.MODID);

    public static final RegistryObject<LootItemConditionType> HAS_COLOR = LOOT_CONDITIONS.register("has_color", () -> new LootItemConditionType(new LootItemHasColorCondition.ConditionSerializer()));
    public static final RegistryObject<LootItemConditionType> HAS_SLEEPING_BAG_COLOR = LOOT_CONDITIONS.register("has_sleeping_bag_color", () -> new LootItemConditionType(new LootItemHasSleepingBagColorCondition.ConditionSerializer()));

}