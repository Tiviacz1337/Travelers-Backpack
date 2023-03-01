package com.tiviacz.travelersbackpack.util;

import java.util.Random;

public class TimeUtils
{
    public static int randomTime(Random rand, int maxSeconds)
    {
        return rand.nextInt(seconds(maxSeconds));
    }

    public static int randomTime(Random rand, int minSeconds, int maxSeconds)
    {
        return rand.nextInt((seconds(maxSeconds) - seconds(minSeconds)) + 1) + seconds(minSeconds);
    }

    public static int seconds(int seconds)
    {
        return seconds * 20;
    }

    public static int randomInBetweenInclusive(Random rand, int min, int max)
    {
        return rand.nextInt(max - min + 1) + min;
    }
}