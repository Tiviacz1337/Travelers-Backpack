package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ClientboundSyncCapabilityPacket
{
    private final int entityID;
    private final boolean isPlayer;
    private final ItemStack stack;

    public ClientboundSyncCapabilityPacket(int entityID, boolean isPlayer, ItemStack stack)
    {
        this.entityID = entityID;
        this.isPlayer = isPlayer;
        this.stack = stack;
    }

    public static ClientboundSyncCapabilityPacket decode(final RegistryFriendlyByteBuf buffer)
    {
        final int entityID = buffer.readInt();
        final boolean isPlayer = buffer.readBoolean();
        final ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer); //buffer.readNbt();

        return new ClientboundSyncCapabilityPacket(entityID, isPlayer, stack);
    }

    public static void encode(final ClientboundSyncCapabilityPacket message, final RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityID);
        buffer.writeBoolean(message.isPlayer);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, message.stack);
        //buffer.writeNbt(message.compound);
    }

    public static void handle(final ClientboundSyncCapabilityPacket message, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() ->
        {
            Minecraft minecraft = Minecraft.getInstance();

            if(message.isPlayer)
            {
                Player player = (Player)minecraft.level.getEntity(message.entityID);

                CapabilityUtils.getCapability(player).ifPresent(cap ->
                {
                    cap.setWearable(message.stack);
                    cap.setContents(message.stack);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}