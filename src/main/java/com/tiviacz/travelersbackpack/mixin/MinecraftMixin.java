package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
            method = "pickBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    public void onPickBlock(CallbackInfo ci, boolean flag, BlockEntity blockentity, HitResult.Type hitresult$type, ItemStack itemstack, Inventory inventory) {
        if(CapabilityUtils.isWearingBackpack(Minecraft.getInstance().player)) {
            int i = inventory.findSlotMatchingItem(itemstack);
            if(i == -1 && !flag) { //Can't find in normal inventory, backpack equipped and no creative mode
                ServerboundActionTagPacket.create(ServerboundActionTagPacket.PICK_ITEM, itemstack);
                ci.cancel();
            }
        }
    }
}