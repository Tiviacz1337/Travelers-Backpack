package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.impl.AccessoriesEventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AccessoriesEventHandler.class)
public class AccessoriesEventHandlerMixin
{
    @Inject(method = "attemptEquipFromUse", at = @At("HEAD"), cancellable = true)
    private static void attemptEquipFromUse(PlayerEntity player, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof TravelersBackpackItem) cir.setReturnValue(TypedActionResult.pass(stack));
    }
}