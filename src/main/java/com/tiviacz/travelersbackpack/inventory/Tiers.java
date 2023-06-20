package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class Tiers
{
    public static final Tier LEATHER = new Tier("leather", 21, Reference.BUCKET * 2, 153, 0);
    public static final Tier IRON = new Tier("iron", 29, Reference.BUCKET * 3, 171, 18);
    public static final Tier GOLD = new Tier("gold", 37, Reference.BUCKET * 4, 189, 36);
    public static final Tier DIAMOND = new Tier("diamond", 45, Reference.BUCKET * 5, 207, 54);
    public static final Tier NETHERITE = new Tier("netherite", 58, Reference.BUCKET * 6, 225, 72);

    public static final String TIER = "Tier";

    public static class Tier
    {
        public final String name;
        public final int storageSlots;
        public final int tankCapacity;
        public final int imageHeight;
        public final int menuSlotPlacementFactor;

        public Tier(String name, int storageSlots, int tankCapacity, int imageHeight, int menuSlotPlacementFactor)
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

        public int getTankCapacity()
        {
            return TravelersBackpackConfig.tanksCapacity.get(getOrdinal());
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
                case TOOL_UPPER -> 15 + (this.getAllSlots() - LEATHER.getAllSlots());
                case TOOL_LOWER -> 16 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_IN_LEFT -> 17 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_LEFT -> 18 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_IN_RIGHT -> 19 + (this.getAllSlots() - LEATHER.getAllSlots());
                case BUCKET_OUT_RIGHT -> 20 + (this.getAllSlots() - LEATHER.getAllSlots());
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
            if(this == LEATHER) return ModItems.IRON_TIER_UPGRADE.get();
            if(this == IRON) return ModItems.GOLD_TIER_UPGRADE.get();
            if(this == GOLD) return ModItems.DIAMOND_TIER_UPGRADE.get();
            if(this == DIAMOND) return ModItems.NETHERITE_TIER_UPGRADE.get();
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
        TOOL_UPPER, TOOL_LOWER,
        BUCKET_IN_LEFT, BUCKET_OUT_LEFT,
        BUCKET_IN_RIGHT, BUCKET_OUT_RIGHT
    }
}