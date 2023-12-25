package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.loot.AddItemModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers
{
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TravelersBackpack.MODID);

    public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<AddItemModifier>> ADD_ITEM = LOOT_MODIFIER_SERIALIZERS.register("add_item", AddItemModifier.CODEC);
    //public static final RegistryObject<Codec<? extends IGlobalLootModifier>> IRON_GOLEM = LOOT_MODIFIER_SERIALIZERS.register("iron_golem", AddBackpackModifier.CODEC);
}
