package com.tiviacz.travelersbackpack.client.gui;

import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackBaseContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import vazkii.quark.api.IQuarkButtonIgnored;

public class TravelersBackpackQuarkScreen extends TravelersBackpackScreen implements IQuarkButtonIgnored
{
    public TravelersBackpackQuarkScreen(TravelersBackpackBaseContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
    {
        super(screenContainer, inv, titleIn);
    }
}
