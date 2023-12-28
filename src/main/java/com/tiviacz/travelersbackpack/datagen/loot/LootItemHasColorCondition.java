package com.tiviacz.travelersbackpack.datagen.loot;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;

public class LootItemHasColorCondition implements LootCondition
{
    public static final LootItemHasColorCondition INSTANCE = new LootItemHasColorCondition();
    public static final Codec<LootItemHasColorCondition> hasColorConditionCodec = Codec.unit(INSTANCE);

    @Override
    public LootConditionType getType()
    {
        return ModLootConditions.HAS_COLOR;
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return lootContext.get(LootContextParameters.BLOCK_ENTITY) instanceof TravelersBackpackBlockEntity blockEntity && blockEntity.hasColor();
    }

    public static Builder hasColor()
    {
        return () -> INSTANCE;
    }
}