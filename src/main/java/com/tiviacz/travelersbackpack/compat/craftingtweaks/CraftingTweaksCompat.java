package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import net.blay09.mods.craftingtweaks.api.CraftingTweaksAPI;

public class CraftingTweaksCompat {
    public static void registerCraftingTweaksAddition() {
        CraftingTweaksAPI.registerCraftingGridProvider(new BackpackCraftingGridProvider());
    }

    public static void registerCraftingTweaksAdditionClient() {
        BackpackCraftingGridAddition.registerCraftingTweaksAddition();
    }
}