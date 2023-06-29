package com.tiviacz.travelersbackpack.datagen.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.JsonSerializer;

public class LootItemHasSleepingBagColorCondition implements LootCondition
{
    public static final LootItemHasSleepingBagColorCondition INSTANCE = new LootItemHasSleepingBagColorCondition();

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

    public static class ConditionSerializer implements JsonSerializer<LootItemHasSleepingBagColorCondition>
    {
        @Override
        public void toJson(JsonObject json, LootItemHasSleepingBagColorCondition object, JsonSerializationContext context) {}

        @Override
        public LootItemHasSleepingBagColorCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return LootItemHasSleepingBagColorCondition.INSTANCE;
        }
    }
}