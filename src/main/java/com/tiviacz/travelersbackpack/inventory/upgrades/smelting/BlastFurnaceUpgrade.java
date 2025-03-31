package com.tiviacz.travelersbackpack.inventory.upgrades.smelting;

import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class BlastFurnaceUpgrade extends AbstractSmeltingUpgrade<BlastFurnaceUpgrade> {
    public BlastFurnaceUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> furnaceContents) {
        super(manager, dataHolderSlot, furnaceContents, RecipeType.BLASTING, "blast_furnace_upgrade");
    }
}