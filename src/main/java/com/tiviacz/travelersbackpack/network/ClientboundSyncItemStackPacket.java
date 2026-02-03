package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncItemStackPacket {
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

    public static void encode(final ClientboundSyncItemStackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeInt(message.slot);
        buffer.writeItem(message.itemStackInstance);
        buffer.writeNbt(message.map);
    }

    public static void handle(final ClientboundSyncItemStackPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
        {
            Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);

            //Sync clientside wrapper if integration enabled (Wrapper created on the fly)
            if(player != null && message.slot == -1) {
                if(player.containerMenu instanceof BackpackItemMenu menu) {
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
                //Sync component changes on client
                for(String key : message.map.getAllKeys()) {
                    player.getInventory().items.get(message.slot).getOrCreateTag().put(key, message.map.get(key));
                }

                //Update Item Backpack
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    menu.getWrapper().setBackpackStack(player.getInventory().items.get(message.slot));
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
