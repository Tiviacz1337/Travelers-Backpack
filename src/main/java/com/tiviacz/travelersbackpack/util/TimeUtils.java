package com.tiviacz.travelersbackpack.util;

import net.minecraft.util.RandomSource;

public class TimeUtils
{
    public static int randomTime(RandomSource rand, int maxSeconds)
    {
        return rand.nextInt(seconds(maxSeconds));
    }

    public static int randomTime(RandomSource rand, int minSeconds, int maxSeconds)
    {
        return rand.nextIntBetweenInclusive(seconds(minSeconds), seconds(maxSeconds));
    }

    public static int seconds(int seconds)
    {
        return seconds * 20;
    }
}