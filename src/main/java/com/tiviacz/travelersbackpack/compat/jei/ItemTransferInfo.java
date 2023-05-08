package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.Tiers;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemTransferInfo implements IRecipeTransferInfo<TravelersBackpackItemMenu, CraftingRecipe>
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
    public RecipeType<CraftingRecipe> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackItemMenu container, CraftingRecipe recipe)
    {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        for(int i = 1; i < 10; i++)
        {
            list.add(container.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = container.container.getTier();

        for(int i = 10; i < tier.getStorageSlots() + 11 - 7; i++)
        {
            list.add(container.getSlot(i));
        }

        for(int i = tier.getStorageSlots() + 10; i < tier.getStorageSlots() + 11 + Inventory.INVENTORY_SIZE - 1; i++)
        {
            if(container.container.getScreenID() == Reference.ITEM_SCREEN_ID && container.getSlot(i) instanceof DisabledSlot)
            {
                continue;
            }
            list.add(container.getSlot(i));
        }

        return list;
    }
}