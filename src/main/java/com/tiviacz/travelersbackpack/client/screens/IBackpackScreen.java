package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;

public interface IBackpackScreen {
    public Player getScreenPlayer();

    public BackpackWrapper getWrapper();

    public Font getFont();

    public void playUIClickSound();

    public default void sendDataToServer() {

    }
}
