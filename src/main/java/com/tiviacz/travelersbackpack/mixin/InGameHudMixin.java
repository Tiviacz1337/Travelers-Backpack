package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screen.OverlayHandledScreen;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
    @Inject(at = @At(value = "TAIL"), method = "renderHotbar(FLnet/minecraft/client/util/math/MatrixStack;)V")
    private void renderOverlay(float delta, MatrixStack matrices, CallbackInfo info)
    {
        if(TravelersBackpackConfig.enableOverlay)
        {
            if(ComponentUtils.isWearingBackpack(MinecraftClient.getInstance().player))
            {
                OverlayHandledScreen gui = new OverlayHandledScreen();
                gui.renderOverlay(matrices);
            }
        }
    }
}
