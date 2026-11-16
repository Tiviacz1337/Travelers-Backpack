package com.tiviacz.travelersbackpack.compat.replay;

import com.replaymod.replay.ReplayModReplay;

public final class ReplayModCompat {
    private ReplayModCompat() {
    }

    public static boolean isInReplay() {
        ReplayModReplay replay = ReplayModReplay.instance;
        return replay != null && replay.getReplayHandler() != null;
    }
}