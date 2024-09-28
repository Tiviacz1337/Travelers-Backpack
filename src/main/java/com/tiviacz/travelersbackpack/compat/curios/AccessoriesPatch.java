package com.tiviacz.travelersbackpack.compat.curios;

import io.wispforest.accessories.client.AccessoriesMenu;
import net.minecraft.world.entity.player.Player;

public class AccessoriesPatch {
    public static boolean isAccessoriesMenuOpened(Player player) {
        return player.containerMenu instanceof AccessoriesMenu;
    }
}
