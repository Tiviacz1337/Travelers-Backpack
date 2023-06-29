package com.tiviacz.travelersbackpack.datagen.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tiviacz.travelersbackpack.init.ModLootConditions;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

public class LootItemHasColorCondition implements ILootCondition
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
        return lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY) instanceof TravelersBackpackTileEntity && ((TravelersBackpackTileEntity)lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY)).hasColor();
    }

    public static ILootCondition.IBuilder hasColor()
    {
        return () -> INSTANCE;
    }

    public static class ConditionSerializer implements ILootSerializer<LootItemHasColorCondition>
    {
        @Override
        public void serialize(JsonObject json, LootItemHasColorCondition loot, JsonSerializationContext context) { }

        @Override
        public LootItemHasColorCondition deserialize(JsonObject loot, JsonDeserializationContext context) {
            return LootItemHasColorCondition.INSTANCE;
        }
    }
}