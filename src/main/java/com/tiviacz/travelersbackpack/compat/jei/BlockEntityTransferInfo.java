package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockEntityTransferInfo implements IRecipeTransferInfo<TravelersBackpackBlockEntityScreenHandler, CraftingRecipe>
{
    @Override
    public Class<? extends TravelersBackpackBlockEntityScreenHandler> getContainerClass()
    {
        return TravelersBackpackBlockEntityScreenHandler.class;
    }

    @Override
    public Optional<ScreenHandlerType<TravelersBackpackBlockEntityScreenHandler>> getMenuType()
    {
        return Optional.of(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY);
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackBlockEntityScreenHandler container, CraftingRecipe recipe)
    {
        return container.inventory.getSettingsManager().hasCraftingGrid();
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackBlockEntityScreenHandler container, CraftingRecipe recipe)
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
    public List<Slot> getInventorySlots(TravelersBackpackBlockEntityScreenHandler container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();

        //Backpack Inv
        for(int i = 1; i <= container.inventory.getInventory().size(); i++)
        {
            list.add(container.getSlot(i));
        }

        //Player Inv
        for(int i = container.inventory.getCombinedInventory().size(); i < container.inventory.getCombinedInventory().size() + PlayerInventory.MAIN_SIZE; i++)
        {
            list.add(container.getSlot(i));
        }

        return list;
    }
}