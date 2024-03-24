package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;

import java.util.ArrayList;
import java.util.List;

public class BlockEntityTransferInfo implements IRecipeTransferInfo<TravelersBackpackBlockEntityMenu, CraftingRecipe>
{
    @Override
    public Class<TravelersBackpackBlockEntityMenu> getContainerClass()
    {
        return TravelersBackpackBlockEntityMenu.class;
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public Class<CraftingRecipe> getRecipeClass()
    {
        return CraftingRecipe.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid()
    {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        return container.container.getSettingsManager().hasCraftingGrid();
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = container.container.getCombinedHandler().getSlots() - 8;

        for(int i = 0; i < 9; i++)
        {
            list.add(container.getSlot(firstCraftSlot + i));
        }

        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();

        //Backpack Inv
        for(int i = 1; i <= container.container.getHandler().getSlots(); i++)
        {
            list.add(container.getSlot(i));
        }

        //Player Inv
        for(int i = container.container.getCombinedHandler().getSlots(); i < container.container.getCombinedHandler().getSlots() + Inventory.INVENTORY_SIZE; i++)
        {
            list.add(container.getSlot(i));
        }

        return list;
    }
}