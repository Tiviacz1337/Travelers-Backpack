package com.tiviacz.travelersbackpack.datagen.loot;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class LootItemHasColorCondition implements LootItemCondition
{
    public static final LootItemHasColorCondition INSTANCE = new LootItemHasColorCondition();
    public static final Codec<LootItemHasColorCondition> hasColorConditionCodec = Codec.unit(INSTANCE);

    @Override
    public LootItemConditionType getType()
    {
        return ModLootConditions.HAS_COLOR.get();
    }

    @Override
    public boolean test(LootContext lootContext)
    {
        return lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY) instanceof TravelersBackpackBlockEntity blockEntity && blockEntity.hasColor();
    }

    public static Builder hasColor()
    {
        return () -> INSTANCE;
    }
}