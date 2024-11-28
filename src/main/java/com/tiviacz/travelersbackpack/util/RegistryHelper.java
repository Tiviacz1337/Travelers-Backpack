package com.tiviacz.travelersbackpack.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.SidedThreadGroups;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Optional;

public class RegistryHelper {
    public static Optional<RegistryAccess> getRegistryAccess() {
        if(Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER && FMLEnvironment.dist.isClient()) {
            return ClientRegistryHelper.getRegistryAccess();
        }

        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if(currentServer == null) {
            return Optional.empty();
        }

        return Optional.of(currentServer.registryAccess());
    }
}
