package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screens.RadialToolsOverlay;
import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void onExtractCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(Minecraft.getInstance().gui.screen() instanceof ToolsScreen && !RadialToolsOverlay.drawCrosshair) {
            ci.cancel();
        }
    }
}
