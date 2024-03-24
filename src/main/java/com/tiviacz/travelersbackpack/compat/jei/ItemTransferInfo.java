package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
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

public class ItemTransferInfo implements IRecipeTransferInfo<TravelersBackpackItemMenu, CraftingRecipe>
{
    @Override
    public Class<TravelersBackpackItemMenu> getContainerClass()
    {
        return TravelersBackpackItemMenu.class;
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
    public boolean canHandle(TravelersBackpackItemMenu container, CraftingRecipe recipe)
    {
        return container.container.getSettingsManager().hasCraftingGrid();
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
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
    public List<Slot> getInventorySlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();

        //Backpack Inv
        for(int i = 1; i <= container.container.getHandler().getSlots(); i++)
        {
            list.add(container.getSlot(i));
        }

        //Player Inv
        for(int i = container.container.getCombinedHandler().getSlots() + 1; i < container.container.getCombinedHandler().getSlots() + 1 + Inventory.INVENTORY_SIZE; i++)
        {
            if(container.container.getScreenID() == Reference.ITEM_SCREEN_ID && container.getSlot(i) instanceof DisabledSlot) continue;

            list.add(container.getSlot(i));
        }

        return list;
    }
}