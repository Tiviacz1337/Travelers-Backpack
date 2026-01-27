package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Shadow
    public Input input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    private void afterInputTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.screen instanceof ToolsScreen) {
            Options settings = Minecraft.getInstance().options;
            input.up = KeybindHandler.isKeyDown(settings.keyUp);
            input.down = KeybindHandler.isKeyDown(settings.keyDown);
            input.left = KeybindHandler.isKeyDown(settings.keyLeft);
            input.right = KeybindHandler.isKeyDown(settings.keyRight);

            input.forwardImpulse = input.up == input.down ? 0.0F : (input.up ? 1.0F : -1.0F);
            input.leftImpulse = input.left == input.right ? 0.0F : (input.left ? 1.0F : -1.0F);
            input.jumping = KeybindHandler.isKeyDown(settings.keyJump);
            input.shiftKeyDown = KeybindHandler.isKeyDown(settings.keyShift);
            if(Minecraft.getInstance().player.isMovingSlowly()) {
                input.leftImpulse = (float)((double)input.leftImpulse * 0.3D);
                input.forwardImpulse = (float)((double)input.forwardImpulse * 0.3D);
            }
        }
    }
}