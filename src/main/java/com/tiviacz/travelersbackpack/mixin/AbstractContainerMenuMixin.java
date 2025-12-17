package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.inventory.menu.AbstractBackpackMenu;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    //Fix for EasyShulkerBoxes Duplication bug - load only if said mod is installed
    @Redirect(method = "tryItemClickBehaviourOverride", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;overrideOtherStackedOnMe(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/inventory/Slot;Lnet/minecraft/world/inventory/ClickAction;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/SlotAccess;)Z"))
    private boolean redirectOverride(ItemStack clickedItem, ItemStack carriedItem, Slot slot, ClickAction action, Player player, SlotAccess slotAccess) {
        boolean result = clickedItem.overrideOtherStackedOnMe(carriedItem, slot, action, player, slotAccess);

        if(ModList.get().isLoaded("easyshulkerboxes") && result) {
            if(player.containerMenu instanceof AbstractBackpackMenu && !(slot.container instanceof Inventory)) { //Only for backpack, vanilla inventory slots work fine so do not include them
                slot.set(clickedItem.copy());
            }
        }
        return result;
    }
}