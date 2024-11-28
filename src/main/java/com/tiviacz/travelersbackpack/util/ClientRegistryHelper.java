package com.tiviacz.travelersbackpack.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;

import java.util.Optional;

public class ClientRegistryHelper {
    public static Optional<RegistryAccess> getRegistryAccess() {
        ClientLevel level = Minecraft.getInstance().level;

        if(level == null) {
            return Optional.empty();
        }

        return Optional.of(level.registryAccess());
    }
}
