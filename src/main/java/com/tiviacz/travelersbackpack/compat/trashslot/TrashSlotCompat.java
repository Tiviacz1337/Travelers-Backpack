package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import net.blay09.mods.trashslot.api.event.RegisterTrashSlotContainerLayoutsEvent;

public class TrashSlotCompat {
    public static void register() {
        RegisterTrashSlotContainerLayoutsEvent.EVENT.register((event) -> {
            event.registerLayout(ModMenuTypes.BACKPACK_MENU.get(), new BackpackLayout());
            event.registerLayout(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), new BackpackLayout());
        });
    }
}