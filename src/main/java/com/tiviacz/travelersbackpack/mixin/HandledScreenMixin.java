package com.tiviacz.travelersbackpack.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin
{
    @Shadow @Nullable protected abstract Slot getSlotAt(double x, double y);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;isClickOutsideBounds(DDIII)Z", shift = At.Shift.BY, by = 2), method = "mouseClicked")
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) LocalBooleanRef localRef)
    {
        Slot slot = this.getSlotAt(mouseX, mouseY);

        if(slot != null)
        {
            localRef.set(false);
        }
    }
}