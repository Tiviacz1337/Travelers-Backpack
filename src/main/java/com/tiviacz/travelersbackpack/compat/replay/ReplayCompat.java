package com.tiviacz.travelersbackpack.compat.replay;

import net.fabricmc.loader.api.FabricLoader;

public final class ReplayCompat {
    private static boolean REPLAYMOD_LOADED;
    private static boolean FLASHBACK_LOADED;

    private ReplayCompat() {
    }

    public static void init() {
        REPLAYMOD_LOADED = FabricLoader.getInstance().isModLoaded("replaymod");
        FLASHBACK_LOADED = FabricLoader.getInstance().isModLoaded("flashback");
    }

    public static boolean isInReplay() {
        return (REPLAYMOD_LOADED && ReplayModCompat.isInReplay()) || (FLASHBACK_LOADED && FlashbackCompat.isInReplay());
    }
}