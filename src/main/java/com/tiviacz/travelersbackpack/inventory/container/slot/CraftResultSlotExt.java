package com.tiviacz.travelersbackpack.inventory.container.slot;

import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class CraftResultSlotExt extends CraftingResultSlot
{
    protected final CraftResultInventory inv;
    protected final ITravelersBackpackInventory inventory;

    public CraftResultSlotExt(ITravelersBackpackInventory inventory, PlayerEntity player, CraftingInventoryImproved matrix, CraftResultInventory inv, int slotIndex, int xPosition, int yPosition)
    {
        super(player, matrix, inv, slotIndex, xPosition, yPosition);
        this.inv = inv;
        this.inventory = inventory;
    }

    @Override
    public boolean isActive()
    {
        if(this.inventory.getTier().getOrdinal() <= 0)
        {
            return this.inventory.getInventory().getStackInSlot(this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() && this.inventory.getInventory().getStackInSlot(this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();
        }
        return true;
    }

    @Override
    public ItemStack remove(int amount)
    {
        if(this.hasItem())
        {
            this.removeCount += Math.min(amount, getItem().getCount());
        }
        return getItem().copy();
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted)
    {
        this.removeCount += numItemsCrafted;
        inv.setItem(0, getItem().copy()); // https://github.com/Shadows-of-Fire/FastWorkbench/issues/62 - Vanilla's SWAP action will leak this stack here.
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack)
    {
        if(this.removeCount > 0)
        {
            stack.onCraftedBy(this.player.level, this.player, this.removeCount);
            BasicEventHooks.firePlayerCraftingEvent(this.player, stack, craftSlots);
        }
        this.removeCount = 0;
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack)
    {
        this.checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> list;
        IRecipe<CraftingInventory> recipe = (IRecipe<CraftingInventory>)inv.getRecipeUsed();

        if(recipe != null && recipe.matches(craftSlots, player.level))
        {
            list = recipe.getRemainingItems(craftSlots);
        }
        else
        {
            list = ((CraftingInventoryImproved)craftSlots).getStackList();
        }
        ForgeHooks.setCraftingPlayer(null);

        for(int i = 0; i < list.size(); ++i)
        {
            ItemStack itemstack = this.craftSlots.getItem(i);
            ItemStack itemstack1 = list.get(i);

            if(!itemstack.isEmpty())
            {
                this.craftSlots.removeItem(i, 1);
                itemstack = this.craftSlots.getItem(i);
            }

            if(!itemstack1.isEmpty())
            {
                if(itemstack.isEmpty())
                {
                    this.craftSlots.setItem(i, itemstack1);
                }
                else if(ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1))
                {
                    itemstack1.grow(itemstack.getCount());
                    this.craftSlots.setItem(i, itemstack1);
                }
                else if(!this.player.inventory.add(itemstack1))
                {
                    this.player.drop(itemstack1, false);
                }
            }
        }
        return stack;
    }
}