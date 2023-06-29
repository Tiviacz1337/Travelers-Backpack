package com.tiviacz.travelersbackpack.datagen.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class LootItemHasSleepingBagColorCondition implements LootItemCondition
{
    public static final LootItemHasSleepingBagColorCondition INSTANCE = new LootItemHasSleepingBagColorCondition();

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

    public static LootItemCondition.Builder hasSleepingBagColor() {
        return () -> {
            return INSTANCE;
        };
    }

    public static class ConditionSerializer implements Serializer<LootItemHasSleepingBagColorCondition>
    {
        @Override
        public void serialize(JsonObject json, LootItemHasSleepingBagColorCondition loot, JsonSerializationContext context) { }

        @Override
        public LootItemHasSleepingBagColorCondition deserialize(JsonObject loot, JsonDeserializationContext context) {
            return LootItemHasSleepingBagColorCondition.INSTANCE;
        }
    }
}