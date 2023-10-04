package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
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

public class BlockEntityTransferInfo implements IRecipeTransferInfo<TravelersBackpackBlockEntityMenu, CraftingRecipe>
{
    @Override
    public Class<? extends TravelersBackpackBlockEntityMenu> getContainerClass()
    {
        return TravelersBackpackBlockEntityMenu.class;
    }

    @Override
    public Optional<MenuType<TravelersBackpackBlockEntityMenu>> getMenuType()
    {
        return Optional.of(ModMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY.get());
    }

    @Override
    public RecipeType<CraftingRecipe> getRecipeType()
    {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = (container.container.getTier().getStorageSlots() - Tiers.LEATHER.getStorageSlots()) + 7;

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                list.add(container.getSlot(firstCraftSlot + j + (i * 9)));
            }
        }

        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = container.container.getTier();

        //Backpack Inv
        for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
        {
            if(container.getSlot(i).container instanceof CraftingContainerImproved)
            {
                continue;
            }
            list.add(container.getSlot(i));
        }

        //Player Inv
        for(int i = (tier.getAllSlots() + 14); i < (tier.getAllSlots() + 14) + Inventory.INVENTORY_SIZE; i++)
        {
            list.add(container.getSlot(i));
        }

        return list;
    }
}