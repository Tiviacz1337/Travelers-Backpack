package com.tiviacz.travelersbackpack.compat;

import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.server.ContainerInfoHandler;

public class ServerContainerPlugin implements Runnable
{
    @Override
    public void run()
    {
        ContainerInfoHandler.registerContainerInfo(DefaultPlugin.CRAFTING, ReiCompat.BackpackContainerInfo.create());
    }
}
