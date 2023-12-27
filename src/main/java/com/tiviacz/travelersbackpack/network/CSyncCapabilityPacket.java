package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncCapabilityPacket
{
    private final int entityID;
    private final boolean isPlayer;
    private final CompoundNBT compound;

    public CSyncCapabilityPacket(int entityID, boolean isPlayer, CompoundNBT compound)
    {
        this.entityID = entityID;
        this.isPlayer = isPlayer;
        this.compound = compound;
    }

    public static CSyncCapabilityPacket decode(final PacketBuffer buffer)
    {
        final int entityID = buffer.readInt();
        final boolean isPlayer = buffer.readBoolean();
        final CompoundNBT compound = buffer.readNbt();

        return new CSyncCapabilityPacket(entityID, isPlayer, compound);
    }

    public static void encode(final CSyncCapabilityPacket message, final PacketBuffer buffer)
    {
        buffer.writeInt(message.entityID);
        buffer.writeBoolean(message.isPlayer);
        buffer.writeNbt(message.compound);
    }

    public static void handle(final CSyncCapabilityPacket message, final Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Minecraft minecraft = Minecraft.getInstance();

            if(message.isPlayer)
            {
                PlayerEntity playerEntity = (PlayerEntity)minecraft.level.getEntity(message.entityID);

                CapabilityUtils.getCapability(playerEntity).ifPresent(cap ->
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