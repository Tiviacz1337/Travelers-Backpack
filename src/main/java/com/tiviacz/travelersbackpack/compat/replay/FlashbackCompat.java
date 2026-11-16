package com.tiviacz.travelersbackpack.compat.replay;

import com.moulberry.flashback.Flashback;

public final class FlashbackCompat {
    private FlashbackCompat() {
    }

    public static boolean isInReplay() {
        return Flashback.isInReplay();
    }
}