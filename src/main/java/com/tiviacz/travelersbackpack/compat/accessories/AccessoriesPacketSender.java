package com.tiviacz.travelersbackpack.compat.accessories;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import io.wispforest.accessories.AccessoriesInternals;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.networking.base.BaseNetworkHandler;
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

            for(var containerEntry : capability.getContainers().entrySet()) {
                var container = containerEntry.getValue();

                var accessories = container.getAccessories();
                var cosmetics = container.getCosmeticAccessories();

                for(int i = 0; i < accessories.getContainerSize(); i++) {
                    var slotId = container.getSlotName() + "/" + i;
                    var currentStack = accessories.getItem(i);
                    if(currentStack.getItem() instanceof TravelersBackpackItem) {
                        ItemStack copy = currentStack.copy();
                        System.out.println("Taki Tag wyslano" + copy.getTag().toString());
                        //copy = ItemStackUtils.reduceSize(copy);
                        dirtyStacks.put(slotId, copy);
                    }
                    var currentCosmeticStack = cosmetics.getItem(i);
                    if(currentCosmeticStack.getItem() instanceof TravelersBackpackItem) {
                        ItemStack copy = currentCosmeticStack.copy();
                        //copy = ItemStackUtils.reduceSize(copy);
                        dirtyCosmeticStacks.put(slotId, copy);
                    }
                }
            }

            if(!dirtyStacks.isEmpty() || !dirtyCosmeticStacks.isEmpty()) {
                var packet = SyncContainerData.of(player, capability.getContainers().values(), dirtyStacks, dirtyCosmeticStacks);

                BaseNetworkHandler networkHandler = AccessoriesInternals.getNetworkHandler();
                networkHandler.sendToTrackingAndSelf(player, packet);
                System.out.println("Wysłano pakiet z accessories");
            }
        }
    }
}