package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends TagsProvider<Item> {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, Registries.ITEM, provider, TravelersBackpack.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.BACKPACK_UPGRADES).add(ModItems.IRON_TIER_UPGRADE.getKey(), ModItems.GOLD_TIER_UPGRADE.getKey(), ModItems.DIAMOND_TIER_UPGRADE.getKey(), ModItems.NETHERITE_TIER_UPGRADE.getKey());
    }
}
