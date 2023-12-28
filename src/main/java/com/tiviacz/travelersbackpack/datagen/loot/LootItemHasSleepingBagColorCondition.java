package com.tiviacz.travelersbackpack.datagen.loot;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public class LootItemHasSleepingBagColorCondition implements LootCondition
{
    public static final LootItemHasSleepingBagColorCondition INSTANCE = new LootItemHasSleepingBagColorCondition();
    public static final Codec<LootItemHasSleepingBagColorCondition> hasColorSleepingBagConditionCodec = Codec.unit(INSTANCE);

    @Override
    public LootConditionType getType()
    {
        return ModLootConditions.HAS_SLEEPING_BAG_COLOR;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return lootContext.get(LootContextParameters.BLOCK_ENTITY) instanceof TravelersBackpackBlockEntity blockEntity && blockEntity.hasSleepingBagColor();
    }

    public static Builder hasSleepingBagColor()
    {
        return () -> INSTANCE;
    }
}