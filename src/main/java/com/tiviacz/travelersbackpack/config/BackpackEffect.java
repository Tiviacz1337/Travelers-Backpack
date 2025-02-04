package com.tiviacz.travelersbackpack.config;

import net.minecraft.world.effect.MobEffect;

public record BackpackEffect(MobEffect effect, int minDuration, int maxDuration, int amplifier) {
}
