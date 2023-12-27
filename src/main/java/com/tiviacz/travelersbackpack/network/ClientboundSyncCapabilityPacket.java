package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncCapabilityPacket
{
    private final int entityID;
    private final boolean isPlayer;
    private final CompoundTag compound;

    public ClientboundSyncCapabilityPacket(int entityID, boolean isPlayer, CompoundTag compound)
    {
        this.entityID = entityID;
        this.isPlayer = isPlayer;
        this.compound = compound;
    }

    public static ClientboundSyncCapabilityPacket decode(final FriendlyByteBuf buffer)
    {
        final int entityID = buffer.readInt();
        final boolean isPlayer = buffer.readBoolean();
        final CompoundTag compound = buffer.readAnySizeNbt();

        return new ClientboundSyncCapabilityPacket(entityID, isPlayer, compound);
    }

    public static void encode(final ClientboundSyncCapabilityPacket message, final FriendlyByteBuf buffer)
    {
        buffer.writeInt(message.entityID);
        buffer.writeBoolean(message.isPlayer);
        buffer.writeNbt(message.compound);
    }

    public static void handle(final ClientboundSyncCapabilityPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Minecraft minecraft = Minecraft.getInstance();

            if(message.isPlayer)
            {
                Player player = (Player)minecraft.level.getEntity(message.entityID);

                CapabilityUtils.getCapability(player).ifPresent(cap ->
                {
                    cap.setWearable(ItemStack.of(message.compound));
                    cap.setContents(ItemStack.of(message.compound));
                });
            }
            else
            {
                LivingEntity livingEntity = (LivingEntity)minecraft.level.getEntity(message.entityID);

                CapabilityUtils.getEntityCapability(livingEntity).ifPresent(cap ->
                {
                    cap.setWearable(ItemStack.of(message.compound));
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}