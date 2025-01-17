package com.tiviacz.travelersbackpack.util;

import net.minecraft.util.RandomSource;

public class CooldownHelper {
    public static int createCooldown(int minSeconds, int maxSeconds) {
        RandomSource random = RandomSource.create(1337L);
        return random.nextIntBetweenInclusive(seconds(minSeconds), seconds(maxSeconds));
    }

    public static int seconds(int seconds) {
        return seconds * 20;
    }
}