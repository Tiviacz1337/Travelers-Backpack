package com.tiviacz.travelersbackpack.compat.trinkets;

import io.wispforest.accessories.client.AccessoriesMenu;
import net.minecraft.entity.player.PlayerEntity;

public class AccessoriesPatch {
    public static boolean isAccessoriesMenuOpened(PlayerEntity player) {
        return player.currentScreenHandler instanceof AccessoriesMenu;
    }
}