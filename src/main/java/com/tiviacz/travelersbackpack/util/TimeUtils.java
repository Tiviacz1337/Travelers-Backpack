package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.level.levelgen.RandomSource;

import java.util.Random;

public class TimeUtils
{
    public static int randomTime(Random rand, int maxSeconds)
    {
        return rand.nextInt(seconds(maxSeconds));
    }

    public static int randomTime(Random rand, int minSeconds, int maxSeconds)
    {
        return rand.nextInt((maxSeconds - minSeconds) + 1) + minSeconds;
    }

    public static int seconds(int seconds)
    {
        return seconds * 20;
    }
}