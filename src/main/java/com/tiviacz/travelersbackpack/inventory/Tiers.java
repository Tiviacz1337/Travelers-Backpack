package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Tiers
{
    public static final Tier LEATHER = new Tier("leather", (9 * 3), 2, FluidConstants.BUCKET * 3);
    public static final Tier IRON = new Tier("iron", (9 * 4), 3, FluidConstants.BUCKET * 4);
    public static final Tier GOLD = new Tier("gold", (9 * 5), 4, FluidConstants.BUCKET * 5);
    public static final Tier DIAMOND = new Tier("diamond", (9 * 6), 5, FluidConstants.BUCKET * 6);
    public static final Tier NETHERITE = new Tier("netherite", (9 * 7), 6, FluidConstants.BUCKET * 7);

    public static class Tier
    {
        public final String name;
        public final int storageSlots;
        public final int toolSlots;
        public final long tankCapacity;

        public Tier(String name, int storageSlots, int toolSlots, long tankCapacity)
        {
            this.name = name;
            this.storageSlots = storageSlots;
            this.toolSlots = toolSlots;
            this.tankCapacity = tankCapacity;
        }

        public String getName()
        {
            return this.name;
        }

        public int getStorageSlots()
        {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.inventorySlotCount;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.inventorySlotCount;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.inventorySlotCount;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.inventorySlotCount;
            if(this == NETHERITE) return TravelersBackpackConfig.getConfig().backpackSettings.netherite.inventorySlotCount;
            return this.storageSlots;
        }

        public int getToolSlots()
        {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.toolSlotCount;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.toolSlotCount;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.toolSlotCount;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.toolSlotCount;
            if(this == NETHERITE) return TravelersBackpackConfig.getConfig().backpackSettings.netherite.toolSlotCount;
            return this.toolSlots;
        }

        public long getTankCapacity()
        {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.tankCapacity;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.tankCapacity;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.tankCapacity;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.tankCapacity;
            if(this == NETHERITE) return TravelersBackpackConfig.getConfig().backpackSettings.netherite.tankCapacity;
            return this.tankCapacity;
        }

        public Tier getNextTier()
        {
            if(this == LEATHER) return IRON;
            if(this == IRON) return GOLD;
            if(this == GOLD) return DIAMOND;
            if(this == DIAMOND) return NETHERITE;
            return LEATHER;
        }

        public int getOrdinal()
        {
            if(this == LEATHER) return 0;
            if(this == IRON) return 1;
            if(this == GOLD) return 2;
            if(this == DIAMOND) return 3;
            if(this == NETHERITE) return 4;
            return -1;
        }

        public Item getTierUpgradeIngredient()
        {
            if(this == LEATHER) return ModItems.IRON_TIER_UPGRADE;
            if(this == IRON) return ModItems.GOLD_TIER_UPGRADE;
            if(this == GOLD) return ModItems.DIAMOND_TIER_UPGRADE;
            if(this == DIAMOND) return ModItems.NETHERITE_TIER_UPGRADE;
            return Items.AIR;
        }
    }

    public static Tier of(String name)
    {
        return switch(name)
        {
            case "leather" -> Tiers.LEATHER;
            case "iron" -> Tiers.IRON;
            case "gold" -> Tiers.GOLD;
            case "diamond" -> Tiers.DIAMOND;
            case "netherite" -> Tiers.NETHERITE;
            default -> Tiers.LEATHER;
        };
    }

    public static Tier of(int ordinal)
    {
        return switch(ordinal)
        {
            case 0 -> Tiers.LEATHER;
            case 1 -> Tiers.IRON;
            case 2 -> Tiers.GOLD;
            case 3 -> Tiers.DIAMOND;
            case 4 -> Tiers.NETHERITE;
            default -> Tiers.LEATHER;
        };
    }
}