package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.handlers.ScreenRenderHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {
    @Inject(at = @At(value = "TAIL"), method = "renderContents")
    private void renderContents(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci) {
        if((Object)this instanceof AbstractContainerScreen<?> screen) {
            ScreenRenderHandler.renderAboveContents(screen, guiGraphics, i, j);
        }
    }
}