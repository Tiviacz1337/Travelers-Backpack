package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TileEntityTransferInfo implements IRecipeTransferInfo<TravelersBackpackTileContainer>
{
    @Override
    public Class<TravelersBackpackTileContainer> getContainerClass()
    {
        return TravelersBackpackTileContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid()
    {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackTileContainer travelersBackpackItemContainer)
    {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackTileContainer travelersBackpackItemContainer)
    {
        List<Slot> list = new ArrayList<>();
        int firstCraftSlot = (travelersBackpackItemContainer.inventory.getTier().getStorageSlotsWithCrafting() - Tiers.LEATHER.getStorageSlotsWithCrafting()) + 6;

        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                list.add(travelersBackpackItemContainer.getSlot(firstCraftSlot + j + (i * 8)));
            }
        }

        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackTileContainer travelersBackpackItemContainer)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = travelersBackpackItemContainer.inventory.getTier();

        //Backpack Inv
        for(int i = 1; i < tier.getStorageSlotsWithCrafting() + 1; i++)
        {
            if(travelersBackpackItemContainer.getSlot(i).container instanceof CraftingInventoryImproved)
            {
                continue;
            }
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        //Player Inv
        for(int i = (tier.getAllSlots() + 10); i < (tier.getAllSlots() + 10) + 36; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        return list;
    }
}