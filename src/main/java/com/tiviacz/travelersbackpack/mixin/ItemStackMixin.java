package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.ae2.AE2Compat;
import com.tiviacz.travelersbackpack.inventory.menu.AbstractBackpackMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "overrideStackedOnOther", at = @At(value = "HEAD"), cancellable = true)
    private void afterOverrideStackedOnOther(Slot slot, ClickAction action, Player player, CallbackInfoReturnable<Boolean> cir) {
        if(!TravelersBackpack.ae2Loaded) {
            return;
        }
        if(player.containerMenu instanceof AbstractBackpackMenu && !(slot.container instanceof Inventory)) { //Only for backpack, vanilla inventory slots work fine so do not include them
            if((Object)this instanceof ItemStack clicked && AE2Compat.isPortableCellItem(clicked)) {
                cir.setReturnValue(false);
            }
        }
    }
}