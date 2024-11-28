package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> BLACKLISTED_ITEMS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "blacklisted_items"));
    public static final TagKey<Item> ACCEPTABLE_TOOLS = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "acceptable_tools"));
    public static final TagKey<Item> CUSTOM_TRAVELERS_BACKPACK = ItemTags.create(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "custom_travelers_backpack"));
    public static final TagKey<Item> SLEEPING_BAGS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sleeping_bags"));
    public static final TagKey<Item> BACKPACK_UPGRADES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_upgrades"));
}