package com.tiviacz.travelersbackpack.mixin;

import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldSavePath.class)
public interface WorldSavePathMixin
{
    @Invoker("<init>")
    static WorldSavePath invokeInit(String path) {
        throw new AssertionError();
    }
}
