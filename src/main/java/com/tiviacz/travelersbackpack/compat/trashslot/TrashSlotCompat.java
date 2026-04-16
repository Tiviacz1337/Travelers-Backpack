package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import net.blay09.mods.trashslot.api.TrashSlotAPI;

public class TrashSlotCompat {
    public static void register() {
        TrashSlotAPI.registerLayout(ModMenuTypes.BACKPACK_MENU.get(), new BackpackLayout());
        TrashSlotAPI.registerLayout(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), new BackpackLayout());
    }
}