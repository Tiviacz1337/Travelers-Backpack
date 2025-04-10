package com.tiviacz.travelersbackpack.inventory.upgrades.smelting;

import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class SmokerUpgrade extends AbstractSmeltingUpgrade<SmokerUpgrade> {
    public SmokerUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> furnaceContents) {
        super(manager, dataHolderSlot, furnaceContents, RecipeType.SMOKING, "smoker_upgrade");
    }
}