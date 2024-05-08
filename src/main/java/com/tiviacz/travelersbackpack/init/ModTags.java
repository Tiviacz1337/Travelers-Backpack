package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags
{
    public static final TagKey<Item> BLACKLISTED_ITEMS = TagKey.of(Registries.ITEM.getKey(), new Identifier(TravelersBackpack.MODID, "blacklisted_items"));
    public static final TagKey<Item> ACCEPTABLE_TOOLS = TagKey.of(Registries.ITEM.getKey(), new Identifier(TravelersBackpack.MODID, "acceptable_tools"));
    public static final TagKey<Item> CUSTOM_TRAVELERS_BACKPACK = TagKey.of(Registries.ITEM.getKey(), new Identifier(TravelersBackpack.MODID, "custom_travelers_backpack"));
    public static final TagKey<Item> SLEEPING_BAGS = TagKey.of(Registries.ITEM.getKey(), new Identifier(TravelersBackpack.MODID, "sleeping_bags"));
    public static final TagKey<Item> BACKPACK_UPGRADES = TagKey.of(Registries.ITEM.getKey(), new Identifier(TravelersBackpack.MODID, "backpack_upgrades"));
}