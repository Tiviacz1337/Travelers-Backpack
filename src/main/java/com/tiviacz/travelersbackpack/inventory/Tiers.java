package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Tiers {
    public static final Tier LEATHER = new Tier("leather", (9 * 3), 2, 2, 1000);
    public static final Tier IRON = new Tier("iron", (9 * 5), 3, 3, 1000);
    public static final Tier GOLD = new Tier("gold", (9 * 7), 4, 4, 1000);
    public static final Tier DIAMOND = new Tier("diamond", (9 * 9), 5, 5, 1000);
    public static final Tier NETHERITE = new Tier("netherite", (9 * 11), 6, 6, 1000);

    public static class Tier {
        public final String name;
        public int toolSlots;
        public final int storageSlots;
        public final int upgradeSlots;
        public final int tankCapacityPerRow;

        public Tier(String name, int storageSlots, int upgradeSlots, int toolSlots, int tankCapacityPerRow) {
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
            if(this == LEATHER) return TravelersBackpackConfig.SERVER.backpackSettings.leather.inventorySlotCount.get();
            if(this == IRON) return TravelersBackpackConfig.SERVER.backpackSettings.iron.inventorySlotCount.get();
            if(this == GOLD) return TravelersBackpackConfig.SERVER.backpackSettings.gold.inventorySlotCount.get();
            if(this == DIAMOND) return TravelersBackpackConfig.SERVER.backpackSettings.diamond.inventorySlotCount.get();
            if(this == NETHERITE)
                return TravelersBackpackConfig.SERVER.backpackSettings.netherite.inventorySlotCount.get();
            return this.storageSlots;
        }

        public int getUpgradeSlots() {
            if(this == LEATHER) return TravelersBackpackConfig.SERVER.backpackSettings.leather.upgradeSlotCount.get();
            if(this == IRON) return TravelersBackpackConfig.SERVER.backpackSettings.iron.upgradeSlotCount.get();
            if(this == GOLD) return TravelersBackpackConfig.SERVER.backpackSettings.gold.upgradeSlotCount.get();
            if(this == DIAMOND) return TravelersBackpackConfig.SERVER.backpackSettings.diamond.upgradeSlotCount.get();
            if(this == NETHERITE)
                return TravelersBackpackConfig.SERVER.backpackSettings.netherite.upgradeSlotCount.get();
            return this.upgradeSlots;
        }

        public int getToolSlots() {
            if(this == LEATHER) return TravelersBackpackConfig.SERVER.backpackSettings.leather.toolSlotCount.get();
            if(this == IRON) return TravelersBackpackConfig.SERVER.backpackSettings.iron.toolSlotCount.get();
            if(this == GOLD) return TravelersBackpackConfig.SERVER.backpackSettings.gold.toolSlotCount.get();
            if(this == DIAMOND) return TravelersBackpackConfig.SERVER.backpackSettings.diamond.toolSlotCount.get();
            if(this == NETHERITE) return TravelersBackpackConfig.SERVER.backpackSettings.netherite.toolSlotCount.get();
            return this.toolSlots;
        }

        public int getTankCapacityPerRow() {
            if(this == LEATHER) return TravelersBackpackConfig.SERVER.backpackSettings.leather.tankCapacityPerRow.get();
            if(this == IRON) return TravelersBackpackConfig.SERVER.backpackSettings.iron.tankCapacityPerRow.get();
            if(this == GOLD) return TravelersBackpackConfig.SERVER.backpackSettings.gold.tankCapacityPerRow.get();
            if(this == DIAMOND) return TravelersBackpackConfig.SERVER.backpackSettings.diamond.tankCapacityPerRow.get();
            if(this == NETHERITE)
                return TravelersBackpackConfig.SERVER.backpackSettings.netherite.tankCapacityPerRow.get();
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
            if(this == LEATHER) return ModItems.IRON_TIER_UPGRADE.get();
            if(this == IRON) return ModItems.GOLD_TIER_UPGRADE.get();
            if(this == GOLD) return ModItems.DIAMOND_TIER_UPGRADE.get();
            if(this == DIAMOND) return ModItems.NETHERITE_TIER_UPGRADE.get();
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