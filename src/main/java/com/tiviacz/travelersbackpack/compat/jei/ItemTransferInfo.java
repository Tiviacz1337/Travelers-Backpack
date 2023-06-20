package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.DisabledSlot;
import com.tiviacz.travelersbackpack.util.Reference;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
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
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
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
    public List<Slot> getInventorySlots(TravelersBackpackItemMenu container, CraftingRecipe recipe)
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
            if(container.container.getScreenID() == Reference.ITEM_SCREEN_ID && container.getSlot(i) instanceof DisabledSlot) continue;

            list.add(container.getSlot(i));
        }

        return list;
    }
}