package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class Tiers
{
    public static final Tier LEATHER = new Tier("leather", 20, TravelersBackpackConfig.tanksCapacity[0], 153, 0);
    public static final Tier IRON = new Tier("iron", 30, TravelersBackpackConfig.tanksCapacity[1], 171, 18);
    public static final Tier GOLD = new Tier("gold", 40, TravelersBackpackConfig.tanksCapacity[2], 189, 36);
    public static final Tier DIAMOND = new Tier("diamond", 50, TravelersBackpackConfig.tanksCapacity[3], 207, 54);
    public static final Tier NETHERITE = new Tier("netherite", 60, TravelersBackpackConfig.tanksCapacity[4], 225, 72);

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
            return this.storageSlots - getToolSlots();
        }

        public int getStorageSlotsWithCrafting()
        {
            return this.getStorageSlots() + 9;
        }

        public int getToolSlots()
        {
            if(this == LEATHER) return 2;
            if(this == IRON) return 3;
            if(this == GOLD) return 4;
            if(this == DIAMOND) return 5;
            if(this == NETHERITE) return 6;
            return getAllSlots() - getStorageSlots();
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
            return switch(slotType)
            {
                case TOOL_FIRST -> 18 + (this.getStorageSlots() - LEATHER.getStorageSlots());
                case TOOL_SECOND -> 19 + (this.getStorageSlots() - LEATHER.getStorageSlots());
                case BUCKET_IN_LEFT -> 0;//17 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_LEFT -> 1; //18 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_IN_RIGHT -> 2; //19 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_RIGHT -> 3; //20 + (this.getAllSlots() - LEATHER.getAllSlots());
            };
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

                for(int i = slot, j = slot; i < slots.length; i++)
                {
                    if(counter < 6)
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

                        if(counter == 9)
                        {
                            counter = 0;
                        }
                    }
                }
            }
            return slots;
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

    public enum SlotType
    {
        TOOL_FIRST, TOOL_SECOND,
        BUCKET_IN_LEFT, BUCKET_OUT_LEFT,
        BUCKET_IN_RIGHT, BUCKET_OUT_RIGHT
    }
}