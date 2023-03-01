package com.tiviacz.travelersbackpack.component.entity;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.item.ItemStack;

public interface IEntityTravelersBackpackComponent extends Component, AutoSyncedComponent
{
    boolean hasWearable();

    ItemStack getWearable();

    void setWearable(ItemStack stack);

    void removeWearable();

    void sync();
}