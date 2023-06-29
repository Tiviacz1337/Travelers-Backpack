package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screen.OverlayHandledScreen;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(at = @At(value = "TAIL"), method = "renderHotbar")
    private void renderOverlay(float tickDelta, DrawContext context, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.enableOverlay)
        {
            if(ComponentUtils.isWearingBackpack(MinecraftClient.getInstance().player))
            {
                OverlayHandledScreen gui = new OverlayHandledScreen();
                gui.renderOverlay(context);
            }
        }
    }
}