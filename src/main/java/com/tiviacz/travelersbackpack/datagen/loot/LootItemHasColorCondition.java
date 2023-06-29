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

public class LootItemHasColorCondition implements LootItemCondition
{
    public static final LootItemHasColorCondition INSTANCE = new LootItemHasColorCondition();

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

    public static LootItemCondition.Builder hasColor() {
        return () -> {
            return INSTANCE;
        };
    }

    public static class ConditionSerializer implements Serializer<LootItemHasColorCondition>
    {
        @Override
        public void serialize(JsonObject json, LootItemHasColorCondition loot, JsonSerializationContext context) { }

        @Override
        public LootItemHasColorCondition deserialize(JsonObject loot, JsonDeserializationContext context) {
            return LootItemHasColorCondition.INSTANCE;
        }
    }
}