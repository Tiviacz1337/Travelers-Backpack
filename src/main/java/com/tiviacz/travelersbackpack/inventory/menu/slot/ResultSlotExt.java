package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public class ResultSlotExt extends ResultSlot
{
    protected final ResultContainer inv;
    private final byte screenID;

    public ResultSlotExt(Player player, CraftingContainerImproved matrix, ResultContainer inv, byte screenID, int slotIndex, int xPosition, int yPosition)
    {
        super(player, matrix, inv, slotIndex, xPosition, yPosition);
        this.inv = inv;
        this.screenID = screenID;
    }

    @Override
    public ItemStack remove(int amount)
    {
        if(this.hasItem())
        {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return this.getItem().copy();
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted)
    {
        super.onSwapCraft(numItemsCrafted);
        this.inv.setItem(0, this.getItem().copy()); // https://github.com/Shadows-of-Fire/FastWorkbench/issues/62 - Vanilla's SWAP action will leak this stack here.
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack)
    {
        if(this.removeCount > 0)
        {
            stack.onCraftedBy(this.player.level, this.player, this.removeCount);
            ForgeEventFactory.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }
        this.removeCount = 0;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void onTake(Player player, ItemStack stack)
    {
        this.checkTakeAchievements(stack);
        ForgeHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> list;
        Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>) this.inv.getRecipeUsed();
        if(recipe != null && recipe.matches(this.craftSlots, player.level)) list = recipe.getRemainingItems(this.craftSlots);
        else list = ((CraftingContainerImproved)this.craftSlots).getStackList();
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
                else if (ItemStack.isSame(itemstack, itemstack1) && ItemStack.tagMatches(itemstack, itemstack1))
                {
                    itemstack1.grow(itemstack.getCount());
                    this.craftSlots.setItem(i, itemstack1);
                }
                else if (!this.player.getInventory().add(itemstack1))
                {
                    this.player.drop(itemstack1, false);
                }
            }
        }
    }
}