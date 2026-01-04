package com.tiviacz.travelersbackpack.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.forge.REIPluginCommon;

@REIPluginCommon
public class ReiCompat implements REIClientPlugin {
    @Override
    public double getPriority() {
        return 0D;
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new ReiTransferHandler());
    }
}