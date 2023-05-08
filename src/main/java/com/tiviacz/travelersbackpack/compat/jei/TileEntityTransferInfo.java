package com.tiviacz.travelersbackpack.compat.jei;

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
        for(int i = 1; i < 10; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackTileContainer travelersBackpackItemContainer)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = travelersBackpackItemContainer.inventory.getTier();

        for(int i = 10; i < tier.getStorageSlots() + 11 - 7; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        for(int i = tier.getStorageSlots() + 10; i < tier.getStorageSlots() + 11 + 36 - 1; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        return list;
    }
}