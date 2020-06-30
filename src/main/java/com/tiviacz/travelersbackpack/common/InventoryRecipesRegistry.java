package com.tiviacz.travelersbackpack.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.api.inventory.InventoryRecipe;
import com.tiviacz.travelersbackpack.gui.inventory.IInventoryTanks;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.Map;

public class InventoryRecipesRegistry
{
    public static BiMap<String, InventoryRecipe> RECIPE_REGISTRY = HashBiMap.create();

    private static int recipeIDCounter = 0;

    public static void initRecipes()
    {
        RECIPE_REGISTRY.clear();
    }

    public static int registerRecipe(InventoryRecipe recipe)
    {
        String className = recipe.getClass().getName();

        if(!RECIPE_REGISTRY.containsKey(className) && recipe.isRecipeNonNull())
        {
            RECIPE_REGISTRY.put(className, recipe);
            recipe.setRecipeID(recipeIDCounter);
            LogHelper.info("Registered the class " + className + " as a InventoryRecipe for " + recipe.getStackIn().getDisplayName() + " with the ID " + recipeIDCounter);
            recipeIDCounter++;
            return recipeIDCounter;
        }
        return -1;
    }

    public static Map<String, InventoryRecipe> getRegisteredInventoryRecipes()
    {
        return ImmutableMap.copyOf(RECIPE_REGISTRY);
    }

    public static boolean hasStackInInventoryRecipeAndCanProcess(EntityPlayer player, IInventoryTanks inv, ItemStack stackIn, FluidTank tank, int slotIn)
    {
        for(InventoryRecipe recipe : getRegisteredInventoryRecipes().values())
        {
            if(stackIn.isItemEqual(recipe.getStackIn()))
            {
                if(recipe.canProcess(tank, inv.getStackInSlot(slotIn + 1)))
                {
                    return recipe.processRecipe(player, inv, tank, slotIn, slotIn + 1);
                }
            }
        }
        return false;
    }

    public static InventoryRecipe getInventoryRecipe(ItemStack stackIn, FluidStack fluidstack, int amount, ItemStack stackOut)
    {
        for(InventoryRecipe recipe : getRegisteredInventoryRecipes().values())
        {
            if(stackIn == recipe.getStackIn() && fluidstack == recipe.getFluidStack() && amount == recipe.getAmount() && stackOut == recipe.getStackOut())
            {
                return recipe;
            }
        }
        return null;
    }
}
