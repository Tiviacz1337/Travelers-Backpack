package com.tiviacz.travelersbackpackold.component;

import com.tiviacz.travelersbackpackold.inventory.TravelersBackpackInventory;
import net.minecraft.item.ItemStack;
import org.ladysnake.cca.api.v3.component.ComponentV3;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

public interface ITravelersBackpackComponent extends ComponentV3, AutoSyncedComponent, RespawnableComponent
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    TravelersBackpackInventory getInventory();

    void setContents(ItemStack stack);

    void sync();
}