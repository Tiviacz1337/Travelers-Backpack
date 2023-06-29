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

public class LootItemHasColorCondition implements LootCondition
{
    public static final LootItemHasColorCondition INSTANCE = new LootItemHasColorCondition();

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

    public static class ConditionSerializer implements JsonSerializer<LootItemHasColorCondition>
    {
        @Override
        public void toJson(JsonObject json, LootItemHasColorCondition object, JsonSerializationContext context) {

        }

        @Override
        public LootItemHasColorCondition fromJson(JsonObject json, JsonDeserializationContext context) {
            return LootItemHasColorCondition.INSTANCE;
        }
    }
}