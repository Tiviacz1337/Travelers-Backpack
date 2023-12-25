package com.tiviacz.travelersbackpack.datagen.loot;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class LootItemHasSleepingBagColorCondition implements LootItemCondition
{
    public static final LootItemHasSleepingBagColorCondition INSTANCE = new LootItemHasSleepingBagColorCondition();
    public static final Codec<LootItemHasSleepingBagColorCondition> hasColorSleepingBagConditionCodec = Codec.unit(INSTANCE);

    @Override
    public LootItemConditionType getType()
    {
        return ModLootConditions.HAS_SLEEPING_BAG_COLOR.get();
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TravelersBackpackBlockEntity blockEntity && blockEntity.hasSleepingBagColor();
    }

    public static Builder hasSleepingBagColor()
    {
        return () -> INSTANCE;
    }
}