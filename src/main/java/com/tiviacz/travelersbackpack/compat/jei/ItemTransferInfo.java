package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemTransferInfo implements IRecipeTransferInfo<TravelersBackpackItemScreenHandler, RecipeEntry<CraftingRecipe>>
{
    @Override
    public Class<? extends TravelersBackpackItemScreenHandler> getContainerClass()
    {
        return TravelersBackpackItemScreenHandler.class;
    }

    @Override
    public Optional<ScreenHandlerType<TravelersBackpackItemScreenHandler>> getMenuType()
    {
        return Optional.of(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM);
    }

    @Override
    public RecipeType<RecipeEntry<CraftingRecipe>> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackItemScreenHandler container, RecipeEntry<CraftingRecipe> recipe)
    {
        return container.inventory.getSettingsManager().hasCraftingGrid();
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemScreenHandler container, RecipeEntry<CraftingRecipe> recipe)
    {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = container.inventory.getCombinedInventory().size() - 8;

        for(int i = 0; i < 9; i++)
        {
            list.add(container.getSlot(firstCraftSlot + i));
        }

        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackItemScreenHandler container, RecipeEntry<CraftingRecipe> recipe)
    {
        List<Slot> list = new ArrayList<>();

        //Backpack Inv
        for(int i = 1; i <= container.inventory.getInventory().size(); i++)
        {
            list.add(container.getSlot(i));
        }

        //Player Inv
        for(int i = container.inventory.getCombinedInventory().size() + 1; i < container.inventory.getCombinedInventory().size() + 1 + PlayerInventory.MAIN_SIZE; i++)
        {
            if(container.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && container.getSlot(i) instanceof DisabledSlot) continue;

            list.add(container.getSlot(i));
        }

        return list;
    }
}