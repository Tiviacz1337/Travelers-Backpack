package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Tiers {
    public static final Tier LEATHER = new Tier("leather", (9 * 3), 2, 2, 81000);
    public static final Tier IRON = new Tier("iron", (9 * 5), 3, 3, 81000);
    public static final Tier GOLD = new Tier("gold", (9 * 7), 4, 4, 81000);
    public static final Tier DIAMOND = new Tier("diamond", (9 * 9), 5, 5, 81000);
    public static final Tier NETHERITE = new Tier("netherite", (9 * 11), 6, 6, 81000);

    public static class Tier {
        public final String name;
        public int toolSlots;
        public final int storageSlots;
        public final int upgradeSlots;
        public final long tankCapacityPerRow;

        public Tier(String name, int storageSlots, int upgradeSlots, int toolSlots, long tankCapacityPerRow) {
            this.name = name;
            this.storageSlots = storageSlots;
            this.upgradeSlots = upgradeSlots;
            this.toolSlots = toolSlots;
            this.tankCapacityPerRow = tankCapacityPerRow;
        }

        public String getName() {
            return this.name;
        }

        public Component getLocalizedName() {
            return Component.translatable("tier.travelersbackpack." + this.name);
        }

        public int getStorageSlots() {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.inventorySlotCount;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.inventorySlotCount;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.inventorySlotCount;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.inventorySlotCount;
            if(this == NETHERITE)
                return TravelersBackpackConfig.getConfig().backpackSettings.netherite.inventorySlotCount;
            return this.storageSlots;
        }

        public int getUpgradeSlots() {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.upgradeSlotCount;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.upgradeSlotCount;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.upgradeSlotCount;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.upgradeSlotCount;
            if(this == NETHERITE)
                return TravelersBackpackConfig.getConfig().backpackSettings.netherite.upgradeSlotCount;
            return this.upgradeSlots;
        }

        public int getToolSlots() {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.toolSlotCount;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.toolSlotCount;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.toolSlotCount;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.toolSlotCount;
            if(this == NETHERITE) return TravelersBackpackConfig.getConfig().backpackSettings.netherite.toolSlotCount;
            return this.toolSlots;
        }

        public long getTankCapacityPerRow() {
            if(this == LEATHER) return TravelersBackpackConfig.getConfig().backpackSettings.leather.tankCapacityPerRow;
            if(this == IRON) return TravelersBackpackConfig.getConfig().backpackSettings.iron.tankCapacityPerRow;
            if(this == GOLD) return TravelersBackpackConfig.getConfig().backpackSettings.gold.tankCapacityPerRow;
            if(this == DIAMOND) return TravelersBackpackConfig.getConfig().backpackSettings.diamond.tankCapacityPerRow;
            if(this == NETHERITE)
                return TravelersBackpackConfig.getConfig().backpackSettings.netherite.tankCapacityPerRow;
            return this.tankCapacityPerRow;
        }

        public Tier getNextTier() {
            if(this == LEATHER) return IRON;
            if(this == IRON) return GOLD;
            if(this == GOLD) return DIAMOND;
            if(this == DIAMOND) return NETHERITE;
            return LEATHER;
        }

        public int getOrdinal() {
            if(this == LEATHER) return 0;
            if(this == IRON) return 1;
            if(this == GOLD) return 2;
            if(this == DIAMOND) return 3;
            if(this == NETHERITE) return 4;
            return -1;
        }

        public Item getTierUpgradeIngredient() {
            if(this == LEATHER) return ModItems.IRON_TIER_UPGRADE;
            if(this == IRON) return ModItems.GOLD_TIER_UPGRADE;
            if(this == GOLD) return ModItems.DIAMOND_TIER_UPGRADE;
            if(this == DIAMOND) return ModItems.NETHERITE_TIER_UPGRADE;
            return Items.AIR;
        }
    }

    public static Tier of(String name) {
        return switch(name) {
            case "leather" -> Tiers.LEATHER;
            case "iron" -> Tiers.IRON;
            case "gold" -> Tiers.GOLD;
            case "diamond" -> Tiers.DIAMOND;
            case "netherite" -> Tiers.NETHERITE;
            default -> Tiers.LEATHER;
        };
    }

    public static Tier of(int ordinal) {
        return switch(ordinal) {
            case 0 -> Tiers.LEATHER;
            case 1 -> Tiers.IRON;
            case 2 -> Tiers.GOLD;
            case 3 -> Tiers.DIAMOND;
            case 4 -> Tiers.NETHERITE;
            default -> Tiers.LEATHER;
        };
    }
}