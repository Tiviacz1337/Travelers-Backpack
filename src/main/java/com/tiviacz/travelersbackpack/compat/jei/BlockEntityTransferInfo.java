package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
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
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = (container.container.getTier().getStorageSlotsWithCrafting() - Tiers.LEATHER.getStorageSlotsWithCrafting()) + 6;

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                list.add(container.getSlot(firstCraftSlot + j + (i * 8)));
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
        for(int i = (tier.getAllSlots() + 10); i < (tier.getAllSlots() + 10) + Inventory.INVENTORY_SIZE; i++)
        {
            list.add(container.getSlot(i));
        }

        return list;
    }
}
