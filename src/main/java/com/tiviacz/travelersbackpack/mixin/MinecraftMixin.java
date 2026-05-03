package com.tiviacz.travelersbackpack.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(
            method = "pickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"
            ),
            cancellable = true
    )
    public void onPickBlock(CallbackInfo ci, @Local boolean flag, @Local ItemStack itemstack, @Local Inventory inventory) {
        if(AttachmentUtils.isWearingBackpack(Minecraft.getInstance().player)) {
            int i = inventory.findSlotMatchingItem(itemstack);
            if (i == -1 && !flag) { // Can't find in normal inventory, backpack equipped and no creative mode
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.PICK_ITEM, itemstack);
                ci.cancel();
            }
        }
    }
}