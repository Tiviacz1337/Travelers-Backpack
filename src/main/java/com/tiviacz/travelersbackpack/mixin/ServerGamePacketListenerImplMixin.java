package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.network.protocol.game.ClientboundSetHeldSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicReference;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @Shadow
    protected abstract void tryPickItem(ItemStack itemStack);

    @Inject(method = "tryPickItem", at = @At(value = "HEAD"))
    public void onTryPickItem(ItemStack itemStack, CallbackInfo ci) {
        if(AttachmentUtils.isWearingBackpack(player)) {
            ServerLevel level = this.player.level();
            if(itemStack.isItemEnabled(level.enabledFeatures())) {
                Inventory inventory = player.getInventory();
                //If found in inventory, do vanilla pick item
                if(inventory.findSlotMatchingItem(itemStack) != -1 || player.hasInfiniteMaterials()) {
                    return;
                }
                BackpackWrapper wrapper = AttachmentUtils.getBackpackWrapper(player, AttachmentUtils.STORAGE_ONLY.get());

                AtomicReference<ItemStack> atomicStack = new AtomicReference<>(null);
                StorageAccessWrapper storage = wrapper.getStorageForInputOutput();

                InventoryHelper.iterate(storage, (slot, stack) -> {
                    //Continue if found required stack
                    if(ItemStack.isSameItemSameComponents(stack, itemStack)) {
                        inventory.setSelectedSlot(inventory.getSuitableHotbarSlot());
                        ItemStack pickResult = inventory.getSelectedItem();
                        inventory.setItem(inventory.getSelectedSlot(), stack.copy());

                        StacksHandlerUtils.setStackInSlot(wrapper.getStorage(), slot, pickResult); //storage.setStackInSlot(slot, pickResult);

                        player.connection.send(new ClientboundSetHeldSlotPacket(inventory.getSelectedSlot()));
                        player.inventoryMenu.broadcastChanges();
                        atomicStack.set(stack);
                        return true;
                    }
                    return false;
                });
                if(atomicStack.get() != null) {
                    tryPickItem(atomicStack.get());
                }
            }
        }
    }
}