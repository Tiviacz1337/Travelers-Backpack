package com.tiviacz.travelersbackpack.inventory.menu.slot;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingContainerImproved;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Collections;

public class ResultSlotExt extends ResultSlot {
    protected final ResultContainer inv;
    protected final BackpackWrapper wrapper;

    public ResultSlotExt(BackpackWrapper wrapper, Player player, CraftingContainerImproved matrix, ResultContainer inv, int slotIndex, int xPosition, int yPosition) {
        super(player, matrix, inv, slotIndex, xPosition, yPosition);
        this.inv = inv;
        this.wrapper = wrapper;
    }

    @Override
    public boolean mayPickup(Player player) {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ItemStack remove(int amount) {
        if(this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return this.getItem().copy();
    }

    @Override
    protected void onSwapCraft(int numItemsCrafted) {
        super.onSwapCraft(numItemsCrafted);
        this.inv.setItem(0, this.getItem().copy()); // https://github.com/Shadows-of-Fire/FastWorkbench/issues/62 - Vanilla's SWAP action will leak this stack here.
    }

    @Override
    public void set(ItemStack stack) {
    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if(this.removeCount > 0) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            EventHooks.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }
        this.removeCount = 0;

        // Have to copy this code because vanilla nulls out the recipe, which shouldn't be done.
        RecipeHolder<?> recipe = this.inv.getRecipeUsed();
        if(recipe != null) {
            this.player.triggerRecipeCrafted(this.inv.getRecipeUsed(), this.craftSlots.getItems());
            if(!recipe.value().isSpecial()) {
                this.player.awardRecipes(Collections.singleton(recipe));
            }
        }
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);
        CraftingInput.Positioned pos = this.craftSlots.asPositionedCraftInput();
        CraftingInput input = pos.input();
        int left = pos.left();
        int top = pos.top();
        RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>)this.inv.getRecipeUsed();
        CommonHooks.setCraftingPlayer(player);
        if(recipe != null && recipe.value().matches(input, player.level())) {
            NonNullList<ItemStack> remaining = recipe.value().getRemainingItems(input);

            for(int x = 0; x < input.width(); x++) {
                for(int y = 0; y < input.height(); y++) {
                    int realIdx = x + left + (y + top) * this.craftSlots.getWidth();
                    ItemStack current = this.craftSlots.getItem(realIdx);
                    ItemStack remainder = remaining.get(x + y * input.width());
                    if(!current.isEmpty()) {
                        //Replaced method here #TODO find fix
                        ((CraftingContainerImproved)this.craftSlots).removeItemShiftClick(realIdx, 1);
                        current = this.craftSlots.getItem(realIdx);
                    }
                    if(!remainder.isEmpty()) {
                        if(current.isEmpty()) {
                            this.craftSlots.setItem(realIdx, remainder);
                        } else if(ItemStack.isSameItemSameComponents(current, remainder)) {
                            remainder.grow(current.getCount());
                            this.craftSlots.setItem(realIdx, remainder);
                        } else if(!this.player.getInventory().add(remainder)) {
                            this.player.drop(remainder, false);
                        }
                    }
                }
            }
        }
        CommonHooks.setCraftingPlayer(null);
    }

    @Override
    public ItemStack getItem() {
        if(player.level().isClientSide) return super.getItem();
        // Crafting Tweaks fakes 64x right click operations to right-click craft a stack to the "held" item, so we need to verify the recipe here.
        RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>)this.inv.getRecipeUsed();
        if(recipe != null && recipe.value().matches(this.craftSlots.asCraftInput(), player.level())) {
            return super.getItem();
        }
        return ItemStack.EMPTY;
    }
}