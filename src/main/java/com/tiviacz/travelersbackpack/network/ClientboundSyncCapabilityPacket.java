package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncCapabilityPacket {
    private final int entityID;
    private final ItemStack backpack;
    private final boolean removeData;

    public ClientboundSyncCapabilityPacket(int entityID, ItemStack serverBackpack) {
        this(entityID, serverBackpack, false);
    }

    public ClientboundSyncCapabilityPacket(int entityID, ItemStack backpack, boolean removeData) {
        this.entityID = entityID;
        this.backpack = backpack;
        this.removeData = removeData;
    }

    public static ClientboundSyncCapabilityPacket decode(final FriendlyByteBuf buffer) {
        final int entityID = buffer.readInt();
        final ItemStack backpack = buffer.readItem(); //ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
        final boolean removeData = buffer.readBoolean(); //buffer.readNbt();
        return new ClientboundSyncCapabilityPacket(entityID, backpack, removeData);
    }

    public static void encode(final ClientboundSyncCapabilityPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        //ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, message.backpack);
        buffer.writeItem(message.backpack);
        buffer.writeBoolean(message.removeData);
    }

    public static void handle(final ClientboundSyncCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player playerEntity = (Player)Minecraft.getInstance().level.getEntity(message.entityID);
            LazyOptional<ITravelersBackpack> data = CapabilityUtils.getCapability(playerEntity); //.orElseThrow(() -> new RuntimeException("No player attachment data found!"));
            if(data.isPresent()) {
                if(message.removeData) {
                    data.resolve().get().remove();
                } else {
                    data.resolve().get().updateBackpack(message.backpack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}