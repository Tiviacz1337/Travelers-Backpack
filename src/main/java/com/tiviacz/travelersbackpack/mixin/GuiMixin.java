package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screens.RadialToolsOverlay;
import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void onRenderCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {
        if(Minecraft.getInstance().screen instanceof ToolsScreen && !RadialToolsOverlay.drawCrosshair) {
            ci.cancel();
        }
    }
}