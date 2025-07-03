package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientboundSyncItemStackPacket implements IPacket<ClientboundSyncItemStackPacket> {
    private final int entityID;
    private final int slot;
    private final ItemStack itemStackInstance;
    private final CompoundTag map;

    public ClientboundSyncItemStackPacket(int entityId, int slot, ItemStack itemStackInstance, CompoundTag map) {
        this.entityID = entityId;
        this.slot = slot;
        ItemStack backpackCopy = itemStackInstance.copy();
        backpackCopy.setTag(null); //Need only Item
        this.itemStackInstance = backpackCopy;
        this.map = map;
    }

    public static ClientboundSyncItemStackPacket decode(final FriendlyByteBuf buffer) {
        int entityID = buffer.readInt();
        int slot = buffer.readInt();
        ItemStack itemStackInstance = buffer.readItem();
        CompoundTag map = buffer.readNbt();

        return new ClientboundSyncItemStackPacket(entityID, slot, itemStackInstance, map);
    }

    public void encode(final ClientboundSyncItemStackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeInt(message.slot);
        buffer.writeItem(message.itemStackInstance);
        buffer.writeNbt(message.map);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SYNC_ITEMSTACK_ID;
    }

    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientboundSyncItemStackPacket message = decode(buf);
        client.execute(() -> {
            Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);

            //Sync clientside wrapper if integration enabled (Wrapper created on the fly)
            if(player != null && message.slot == -1) {
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    ItemStack oldStack = menu.getWrapper().getBackpackStack().copy();
                    for(String key : message.map.getAllKeys()) {
                        oldStack.getOrCreateTag().put(key, message.map.get(key));
                    }
                    menu.getWrapper().setBackpackStack(oldStack);
                    return;
                }
                return;
            }

            if(player != null && player.getInventory().items.get(message.slot).is(message.itemStackInstance.getItem())) {
                ItemStack oldStack = player.getInventory().items.get(message.slot).copy();
                //Sync component changes on client
                for(String key : message.map.getAllKeys()) {
                    player.getInventory().items.get(message.slot).getOrCreateTag().put(key, message.map.get(key));
                }
                ItemStack newStack = player.getInventory().items.get(message.slot).copy();

                //Update Item Backpack
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    menu.getWrapper().setBackpackStack(player.getInventory().items.get(message.slot));
                }

                if(message.map.contains(ModDataHelper.HOSE_MODES)) {
                    int changedMode = getChangedMode(oldStack, newStack);
                    if(changedMode != -1) {
                        player.displayClientMessage(getNextModeMessage(changedMode, NbtHelper.deserializeIntList(message.map, ModDataHelper.HOSE_MODES).get(changedMode)), true);
                    }
                }
            }
        });
    }

    public static int getChangedMode(ItemStack oldStack, ItemStack newStack) {
        if((int)NbtHelper.getOrDefault(oldStack, ModDataHelper.HOSE_MODES, List.of(0, 0)).get(0) != (int)NbtHelper.getOrDefault(newStack, ModDataHelper.HOSE_MODES, List.of(0, 0)).get(0)) {
            return 0;
        }
        if((int)NbtHelper.getOrDefault(oldStack, ModDataHelper.HOSE_MODES, List.of(0, 0)).get(1) != (int)NbtHelper.getOrDefault(newStack, ModDataHelper.HOSE_MODES, List.of(0, 0)).get(1)) {
            return 1;
        }
        return -1;
    }

    public static Component getNextModeMessage(int changedMode, int data) {
        if(changedMode == 0) {
            if(data == HoseItem.SPILL_MODE) {
                return Component.translatable("item.travelersbackpack.hose.spill");
            } else if(data == HoseItem.DRINK_MODE) {
                return Component.translatable("item.travelersbackpack.hose.drink");
            }
            return Component.translatable("item.travelersbackpack.hose.suck");
        } else {
            if(data == 1) {
                return Component.translatable("item.travelersbackpack.hose.tank_left");
            } else {
                return Component.translatable("item.travelersbackpack.hose.tank_right");
            }
        }
    }
}
