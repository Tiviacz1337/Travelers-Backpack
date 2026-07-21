package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.inventory.menu.AbstractBackpackMenu;
import net.minecraft.world.entity.SlotAccess;
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
    //Fix for bundle not saving when contents change in backpack (Potentially any resource handler)
    @Inject(method = "overrideOtherStackedOnMe", at = @At(value = "TAIL"))
    private void afterOverrideOtherStackedOnMe(ItemStack stack, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
        boolean result = cir.getReturnValue();
        if(player.containerMenu instanceof AbstractBackpackMenu && !(slot.container instanceof Inventory)) { //Only for backpack, vanilla inventory slots work fine so do not include them
            if(result && (Object)this instanceof ItemStack clicked) {
                slot.set(clicked.copy());
            }
        }
    }
}