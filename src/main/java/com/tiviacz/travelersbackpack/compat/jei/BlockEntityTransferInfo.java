package com.tiviacz.travelersbackpack.compat.jei;

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
        for(int i = 1; i < 10; i++)
        {
            list.add(container.getSlot(i));
        }
        return list;
    }

    @Override
    public List<Slot> getInventorySlots(TravelersBackpackBlockEntityMenu container, CraftingRecipe recipe)
    {
        List<Slot> list = new ArrayList<>();
        Tiers.Tier tier = container.container.getTier();

        for(int i = 10; i < tier.getStorageSlots() + 11 - 7; i++)
        {
            list.add(container.getSlot(i));
        }

        for(int i = tier.getStorageSlots() + 10; i < tier.getStorageSlots() + 11 + Inventory.INVENTORY_SIZE - 1; i++)
        {
            list.add(container.getSlot(i));
        }

        return list;
    }
}
