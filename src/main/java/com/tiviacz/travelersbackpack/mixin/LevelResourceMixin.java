package com.tiviacz.travelersbackpack.mixin;

import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelResource.class)
public interface LevelResourceMixin
{
    @Invoker("<init>")
    static LevelResource invokeInit(String path) {
        throw new AssertionError();
    }
}