package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ItemTransferInfo implements IRecipeTransferInfo<TravelersBackpackItemContainer>
{
    @Override
    public Class<TravelersBackpackItemContainer> getContainerClass()
    {
        return TravelersBackpackItemContainer.class;
    }

    @Override
    public ResourceLocation getRecipeCategoryUid()
    {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Override
    public boolean canHandle(TravelersBackpackItemContainer travelersBackpackItemContainer)
    {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemContainer travelersBackpackItemContainer)
    {
        List<Slot> list = new ArrayList<>();
        for(int i = 1; i < 10; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackItemContainer travelersBackpackItemContainer)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = travelersBackpackItemContainer.inventory.getTier();

        for(int i = 10; i < tier.getStorageSlots() + 11 - 7; i++)
        {
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        for(int i = tier.getStorageSlots() + 10; i < tier.getStorageSlots() + 11 + 36 - 1; i++)
        {
            if(travelersBackpackItemContainer.inventory.getScreenID() == Reference.ITEM_SCREEN_ID && travelersBackpackItemContainer.getSlot(i) instanceof DisabledSlot)
            {
                continue;
            }
            list.add(travelersBackpackItemContainer.getSlot(i));
        }

        return list;
    }
}