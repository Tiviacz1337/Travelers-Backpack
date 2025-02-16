package com.tiviacz.travelersbackpack.client.screens;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

public interface IBackpackScreen {
    public Player getScreenPlayer();

    public BackpackWrapper getWrapper();

    public Font getFont();

    public void playUIClickSound();

    public default void sendDataToServer() {

    }

    //Fabric only
    public Slot getHoveredSlot();

    public int getRows();

    public void setScrollAmount(int scrollAmount);

    public void updateBackpackSlotsPosition();
}
