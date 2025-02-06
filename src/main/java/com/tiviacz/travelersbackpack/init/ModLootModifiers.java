package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.loot.AddItemModifier;
import com.tiviacz.travelersbackpack.loot.HayBackpackLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TravelersBackpack.MODID);

    public static final RegistryObject<MapCodec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIER_SERIALIZERS.register("add_item", AddItemModifier.CODEC);
    public static final RegistryObject<MapCodec<? extends IGlobalLootModifier>> HAY_BACKPACK_MODIFIER = LOOT_MODIFIER_SERIALIZERS.register("hay_backpack_modifier", HayBackpackLootModifier.CODEC);
}
