package com.tiviacz.travelersbackpack.compat.trashslot;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import net.blay09.mods.trashslot.api.TrashSlotAPI;

public class TrashSlotCompat {
    public static void register() {
        TrashSlotAPI.registerLayout(BackpackScreen.class, new BackpackLayout());
    }
}