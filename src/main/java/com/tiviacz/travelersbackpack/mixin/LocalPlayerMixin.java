package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.client.screens.ToolsScreen;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Shadow
    public ClientInput input;

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;tick()V", shift = At.Shift.AFTER))
    private void afterInputTick(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if(mc.gui.screen() instanceof ToolsScreen) {
            Options settings = Minecraft.getInstance().options;
            input.keyPresses = new Input(KeybindHandler.isKeyDown(settings.keyUp), KeybindHandler.isKeyDown(settings.keyDown), KeybindHandler.isKeyDown(settings.keyLeft), KeybindHandler.isKeyDown(settings.keyRight), KeybindHandler.isKeyDown(settings.keyJump), KeybindHandler.isKeyDown(settings.keyShift), KeybindHandler.isKeyDown(settings.keySprint));
            input.moveVector = new Vec2(input.keyPresses.left() == input.keyPresses.right() ? 0.0F : (input.keyPresses.left() ? 1.0F : -1.0F), input.keyPresses.forward() == input.keyPresses.backward() ? 0.0F : (input.keyPresses.forward() ? 1.0F : -1.0F));
            if(Minecraft.getInstance().player.isMovingSlowly()) {
                input.moveVector = new Vec2((float)((double)input.moveVector.x * 0.3), (float)((double)input.moveVector.y * 0.3));
            }
        }
    }
}