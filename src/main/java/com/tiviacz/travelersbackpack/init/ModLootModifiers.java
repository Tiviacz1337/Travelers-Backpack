package com.tiviacz.travelersbackpack.init;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.loot.AddBackpackModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers
{
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TravelersBackpack.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_BACKPACK = LOOT_MODIFIER_SERIALIZERS.register("add_backpack", AddBackpackModifier.CODEC);
    //public static final RegistryObject<Codec<? extends IGlobalLootModifier>> IRON_GOLEM = LOOT_MODIFIER_SERIALIZERS.register("iron_golem", AddBackpackModifier.CODEC);
}
