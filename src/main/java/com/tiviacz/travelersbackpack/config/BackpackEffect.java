package com.tiviacz.travelersbackpack.config;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

public record BackpackEffect(Holder<MobEffect> effect, int minDuration, int maxDuration, int amplifier) {
}
