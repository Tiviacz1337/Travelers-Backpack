package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags
{
    public static final TagKey<Item> BLACKLISTED_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items"));
    public static final TagKey<Item> ACCEPTABLE_TOOLS = TagKey.create(Registries.ITEM, new ResourceLocation(TravelersBackpack.MODID, "acceptable_tools"));
    public static final TagKey<Item> CUSTOM_TRAVELERS_BACKPACK = TagKey.create(Registries.ITEM, new ResourceLocation(TravelersBackpack.MODID, "custom_travelers_backpack"));
    public static final TagKey<Item> SLEEPING_BAGS = TagKey.create(Registries.ITEM, new ResourceLocation(TravelersBackpack.MODID, "sleeping_bags"));
    public static final TagKey<Item> BACKPACK_UPGRADES = TagKey.create(Registries.ITEM, new ResourceLocation(TravelersBackpack.MODID, "backpack_upgrades"));
}