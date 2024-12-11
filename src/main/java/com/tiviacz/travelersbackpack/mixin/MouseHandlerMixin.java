package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MouseHandler.class, priority = 500)
public class MouseHandlerMixin
{
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V"), cancellable = true)
    private void mouseWheelDetect(long window, double horizontal, double vertical, CallbackInfo ci)
    {
        boolean bl = this.minecraft.options.discreteMouseScroll().get();
        double d = this.minecraft.options.mouseWheelSensitivity().get();
        double e = (bl ? Math.signum(horizontal) : horizontal) * d;
        double f = (bl ? Math.signum(vertical) : vertical) * d;

        if(KeybindHandler.mouseWheelDetect(e, f)) ci.cancel();
    }
}