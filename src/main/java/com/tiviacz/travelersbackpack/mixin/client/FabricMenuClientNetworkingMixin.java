package com.tiviacz.travelersbackpack.mixin.client;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.replay.ReplayCompat;
import net.fabricmc.fabric.impl.menu.Networking;
import net.fabricmc.fabric.impl.menu.client.ClientNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientNetworking.class)
public abstract class FabricMenuClientNetworkingMixin {
    @Inject(method = "openScreen", at = @At("HEAD"), cancellable = true, require = 0)
    private void travelersbackpack$skipBackpackMenusInReplay(Networking.OpenScreenPayload<?> payload, CallbackInfo ci) {
        if(payload.identifier().getNamespace().equals(TravelersBackpack.MODID) && ReplayCompat.isInReplay()) {
            ci.cancel();
        }
    }
}