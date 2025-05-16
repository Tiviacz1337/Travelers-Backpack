package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@Deprecated(forRemoval = true, since = "1.22")
public record StarterUpgrades(List<ItemStack> upgrades) {
    public static final Codec<StarterUpgrades> CODEC = ItemStack.CODEC.listOf().xmap(StarterUpgrades::new, StarterUpgrades::upgrades);
    public static final StreamCodec<RegistryFriendlyByteBuf, StarterUpgrades> STREAM_CODEC =
            ItemStack.LIST_STREAM_CODEC.map(StarterUpgrades::new, StarterUpgrades::upgrades);

    @Override
    public boolean equals(Object pOther) {
        if(this == pOther) {
            return true;
        } else {
            if(pOther instanceof StarterUpgrades contents && ItemStack.listMatches(this.upgrades, contents.upgrades)) {
                return true;
            }
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.hashStackList(upgrades);
    }
}