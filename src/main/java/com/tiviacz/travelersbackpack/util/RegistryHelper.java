package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class RegistryHelper {
    public static Optional<RegistryAccess> getRegistryAccess() {
        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return ClientRegistryHelper.getRegistryAccess();
        }

        MinecraftServer currentServer = TravelersBackpack.getCurrentServer();
        if(currentServer == null) {
            return Optional.empty();
        }

        return Optional.of(currentServer.registryAccess());
    }
}