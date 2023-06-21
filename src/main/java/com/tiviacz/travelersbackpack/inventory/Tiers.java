package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Tiers
{
    public static final Tier LEATHER = new Tier("leather", 21, TravelersBackpackConfig.tanksCapacity[0], 153, 0);
    public static final Tier IRON = new Tier("iron", 29, TravelersBackpackConfig.tanksCapacity[1], 171, 18);
    public static final Tier GOLD = new Tier("gold", 37, TravelersBackpackConfig.tanksCapacity[2], 189, 36);
    public static final Tier DIAMOND = new Tier("diamond", 45, TravelersBackpackConfig.tanksCapacity[3], 207, 54);
    public static final Tier NETHERITE = new Tier("netherite", 58, TravelersBackpackConfig.tanksCapacity[4], 225, 72);

    public static final String TIER = "Tier";

    public static class Tier
    {
        public final String name;
        public final int storageSlots;
        public final long tankCapacity;
        public final int imageHeight;
        public final int menuSlotPlacementFactor;

        public Tier(String name, int storageSlots, long tankCapacity, int imageHeight, int menuSlotPlacementFactor)
        {
            this.name = name;
            this.storageSlots = storageSlots;
            this.tankCapacity = tankCapacity;
            this.imageHeight = imageHeight;
            this.menuSlotPlacementFactor = menuSlotPlacementFactor;
        }

        public String getName()
        {
            return this.name;
        }

        public int getAllSlots()
        {
            return this.storageSlots;
        }

        public int getStorageSlots()
        {
            return this.storageSlots - 6;
        }
        public int getStorageSlotsWithCrafting()
        {
            return this.getStorageSlots() + 9;
        }

        public long getTankCapacity()
        {
            return this.tankCapacity;
        }

        public int getImageHeight()
        {
            return this.imageHeight;
        }

        public int getMenuSlotPlacementFactor()
        {
            return this.menuSlotPlacementFactor;
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

        public int getSlotIndex(SlotType slotType)
        {
            switch(slotType)
            {
                case TOOL_UPPER: return 15 + (this.getAllSlots() - LEATHER.getAllSlots());
                case TOOL_LOWER: return 16 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_IN_LEFT: return 17 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_LEFT: return 18 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_IN_RIGHT: return 19 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_RIGHT: return 20 + (this.getAllSlots() - LEATHER.getAllSlots());
                default: return 0;
            }
        }

        public int getTankRenderPos()
        {
            if(this == LEATHER) return 46;
            if(this == IRON) return 64;
            if(this == GOLD) return 82;
            if(this == DIAMOND) return 100;
            if(this == NETHERITE) return 118;
            return -1;
        }

        public int getAbilitySliderRenderPos()
        {
            if(this == LEATHER || this == IRON) return 26;
            else return 56;
        }

        public Item getTierUpgradeIngredient()
        {
            if(this == LEATHER) return ModItems.IRON_TIER_UPGRADE;
            if(this == IRON) return ModItems.GOLD_TIER_UPGRADE;
            if(this == GOLD) return ModItems.DIAMOND_TIER_UPGRADE;
            if(this == DIAMOND) return ModItems.NETHERITE_TIER_UPGRADE;
            return Items.AIR;
        }

        public int[] getSortOrder(boolean isCraftingLocked)
        {
            int[] slots = new int[this.getStorageSlotsWithCrafting()];
            int slot = 0;
            for(int i = 0; i <= this.getStorageSlots() - Tiers.LEATHER.getStorageSlots(); i++)
            {
                slots[i] = i;
                slot = i;
            }
            if(!isCraftingLocked)
            {
                for(int i = slot; i < this.getStorageSlotsWithCrafting(); i++)
                {
                    slots[i] = i;
                }
            }
            else
            {
                int counter = 0;
                int craftingSlot = this.getStorageSlots();
                boolean isFirstRow = true;
                for(int i = slot, j = slot; i < slots.length; i++)
                {
                    if(counter < (this == NETHERITE && isFirstRow ? 6 : 5))
                    {
                        slots[i] = j;
                        j++;
                        counter++;
                    }
                    else
                    {
                        slots[i] = craftingSlot;
                        craftingSlot++;
                        counter++;
                        if(counter == (this == NETHERITE && isFirstRow ? 9 : 8))
                        {
                            counter = 0;
                            isFirstRow = false;
                        }
                    }
                }
            }
            return slots;
        }
    }

    public static Tier of(String name)
    {
        switch(name)
        {
            case "leather": return Tiers.LEATHER;
            case "iron": return Tiers.IRON;
            case "gold": return Tiers.GOLD;
            case "diamond": return Tiers.DIAMOND;
            case "netherite": return Tiers.NETHERITE;
            default: return Tiers.LEATHER;
        }
    }

    public static Tier of(int ordinal)
    {
        switch(ordinal)
        {
            case 0: return Tiers.LEATHER;
            case 1: return Tiers.IRON;
            case 2: return Tiers.GOLD;
            case 3: return Tiers.DIAMOND;
            case 4: return Tiers.NETHERITE;
            default: return Tiers.LEATHER;
        }
    }

    public enum SlotType
    {
        TOOL_UPPER, TOOL_LOWER,
        BUCKET_IN_LEFT, BUCKET_OUT_LEFT,
        BUCKET_IN_RIGHT, BUCKET_OUT_RIGHT
    }
}