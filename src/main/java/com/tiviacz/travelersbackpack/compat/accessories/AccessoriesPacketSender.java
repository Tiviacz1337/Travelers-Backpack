package com.tiviacz.travelersbackpack.compat.accessories;

import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.impl.core.AccessoriesHolderImpl;
import io.wispforest.accessories.networking.AccessoriesNetworking;
import io.wispforest.accessories.networking.client.SyncContainerData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;

public class AccessoriesPacketSender {
    public static void sendSyncingPacketForBackpack(ServerPlayer player) {
        if(player.level().isClientSide()) return;

        var capability = AccessoriesCapability.get(player);

        if(capability != null) {
            var dirtyStacks = new HashMap<String, ItemStack>();
            var dirtyCosmeticStacks = new HashMap<String, ItemStack>();

            for(var containerEntry : AccessoriesHolderImpl.getHolder(capability).getAllSlotContainers().entrySet()) {
                var container = containerEntry.getValue();

                var accessories = container.getAccessories();
                var cosmetics = container.getCosmeticAccessories();

                for(int i = 0; i < accessories.getContainerSize(); i++) {
                    var slotId = container.getSlotName() + "/" + i;
                    var currentStack = accessories.getItem(i);
                    if(currentStack.getItem() instanceof TravelersBackpackItem) {
                        dirtyStacks.put(slotId, currentStack.copy());
                    }
                    var currentCosmeticStack = cosmetics.getItem(i);
                    if(currentCosmeticStack.getItem() instanceof TravelersBackpackItem) {
                        dirtyCosmeticStacks.put(slotId, currentCosmeticStack.copy());
                    }
                }
            }

            if(!dirtyStacks.isEmpty() || !dirtyCosmeticStacks.isEmpty()) {
                var packet = SyncContainerData.of(player, capability.getContainers().values(), dirtyStacks, dirtyCosmeticStacks);

                AccessoriesNetworking.sendToTrackingAndSelf(player, packet);
            }
        }
    }
}