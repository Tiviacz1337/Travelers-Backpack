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

public class LootItemHasSleepingBagColorCondition implements ILootCondition
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
        return lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY) instanceof TravelersBackpackTileEntity && ((TravelersBackpackTileEntity)lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY)).hasSleepingBagColor();
    }

    public static ILootCondition.IBuilder hasSleepingBagColor()
    {
        return () -> INSTANCE;
    }

    public static class ConditionSerializer implements ILootSerializer<LootItemHasSleepingBagColorCondition>
    {
        @Override
        public void serialize(JsonObject json, LootItemHasSleepingBagColorCondition loot, JsonSerializationContext context) { }

        @Override
        public LootItemHasSleepingBagColorCondition deserialize(JsonObject loot, JsonDeserializationContext context) {
            return LootItemHasSleepingBagColorCondition.INSTANCE;
        }
    }
}