package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;

public interface IBackpackScreen {
    Player getScreenPlayer();

    BackpackWrapper getWrapper();

    Font getFont();

    void playUIClickSound();

    default void sendDataToServer() {

    }

    int getRows();

    void setScrollAmount(int scrollAmount);

    void updateBackpackSlotsPosition();
}