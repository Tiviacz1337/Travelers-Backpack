package com.tiviacz.travelersbackpack.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tiviacz.travelersbackpack.client.screens.AbstractBackpackScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Shadow
    @Nullable
    protected abstract Slot findSlot(double mouseX, double mouseY);

    @ModifyExpressionValue(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;hasClickedOutside(DDIII)Z"))
    private boolean mouseClickedInject(boolean outside, @Local(argsOnly = true, ordinal = 0) double mouseX, @Local(argsOnly = true, ordinal = 1) double mouseY) {
        if((Object)this instanceof AbstractBackpackScreen<?>) {
            Slot slot = this.findSlot(mouseX, mouseY);
            return outside && slot == null;
        }
        return outside;
    }
}