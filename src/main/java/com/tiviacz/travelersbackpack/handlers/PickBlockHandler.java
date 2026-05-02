package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.network.ServerboundActionTagPacket;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class PickBlockHandler {
    public static void registerClientListeners() {
        ClientPickBlockApplyCallback.EVENT.register((player, hitResult, target) -> {
            if(ComponentUtils.isWearingBackpack(Minecraft.getInstance().player)) {
                int i = player.getInventory().findSlotMatchingItem(target);
                if(i == -1 && !player.getAbilities().instabuild) { //Can't find in normal inventory, backpack equipped and no creative mode
                    ServerboundActionTagPacket.create(ServerboundActionTagPacket.PICK_ITEM, target);
                    return ItemStack.EMPTY;
                }
            }
            return target;
        });
    }
}