package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemTransferInfo implements IRecipeTransferInfo<TravelersBackpackItemMenu, RecipeHolder<CraftingRecipe>>
{
    @Override
    public Class<? extends TravelersBackpackItemMenu> getContainerClass()
    {
        return TravelersBackpackItemMenu.class;
    }

    @Override
    public Optional<MenuType<TravelersBackpackItemMenu>> getMenuType()
    {
        return Optional.of(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackItemMenu container, RecipeHolder<CraftingRecipe> recipe)
    {
        return container.container.getSettingsManager().hasCraftingGrid();
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemMenu container, RecipeHolder<CraftingRecipe> recipe)
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
    public List<Slot> getInventorySlots(TravelersBackpackItemMenu container, RecipeHolder<CraftingRecipe> recipe)
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