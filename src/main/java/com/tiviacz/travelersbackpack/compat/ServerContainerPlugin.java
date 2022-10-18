package com.tiviacz.travelersbackpack.compat;

import me.shedaniel.rei.api.BuiltinPlugin;
import me.shedaniel.rei.server.ContainerInfoHandler;

public class ServerContainerPlugin implements Runnable
{
    @Override
    public void run()
    {
        ContainerInfoHandler.registerContainerInfo(BuiltinPlugin.CRAFTING, ReiCompat.BackpackContainerInfo.create());
    }
}
