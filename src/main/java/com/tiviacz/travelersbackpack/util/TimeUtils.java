package com.tiviacz.travelersbackpack.util;

import net.minecraft.util.math.random.Random;

public class TimeUtils
{
    public static int randomTime(Random rand, int maxSeconds)
    {
        return rand.nextInt(seconds(maxSeconds));
    }

    public static int randomTime(Random rand, int minSeconds, int maxSeconds)
    {
        return rand.nextBetween(seconds(minSeconds), seconds(maxSeconds));
    }

    public static int seconds(int seconds)
    {
        return seconds * 20;
    }
}