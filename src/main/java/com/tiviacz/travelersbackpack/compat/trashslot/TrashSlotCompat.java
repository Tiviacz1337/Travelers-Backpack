package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import net.blay09.mods.trashslot.api.event.RegisterTrashSlotContainerLayoutsEvent;

public class TrashSlotCompat {
    public static void register() {
        RegisterTrashSlotContainerLayoutsEvent.EVENT.register((event) -> {
            event.registerLayout(ModScreenHandlerTypes.BACKPACK_MENU, new BackpackLayout());
            event.registerLayout(ModScreenHandlerTypes.BACKPACK_BLOCK_MENU, new BackpackLayout());
        });
    }
}