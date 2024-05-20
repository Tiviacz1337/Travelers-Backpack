package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackBlockEntityScreenHandler;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityTransferInfo implements IRecipeTransferInfo<TravelersBackpackBlockEntityScreenHandler, CraftingRecipe>
{
    @Override
    public Class<TravelersBackpackBlockEntityScreenHandler> getContainerClass()
    {
        return TravelersBackpackBlockEntityScreenHandler.class;
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

    @Override
    public Class<CraftingRecipe> getRecipeClass()
    {
        return CraftingRecipe.class;
    }

    @Override
    public Identifier getRecipeCategoryUid()
    {
        return VanillaRecipeCategoryUid.CRAFTING;
    }
}