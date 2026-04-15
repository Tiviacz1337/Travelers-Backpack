package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import net.blay09.mods.trashslot.api.TrashSlotAPI;
import net.minecraft.resources.Identifier;

public class TrashSlotCompat {
    public static void register() {
        TrashSlotAPI.registerLayout(ModScreenHandlerTypes.BACKPACK_MENU, new BackpackLayout());
        TrashSlotAPI.registerLayout(ModScreenHandlerTypes.BACKPACK_BLOCK_MENU, new BackpackLayout());
    }
}