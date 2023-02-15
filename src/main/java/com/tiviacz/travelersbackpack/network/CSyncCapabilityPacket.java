package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CSyncCapabilityPacket
{
    private final CompoundNBT compound;
    private final int entityID;
    private final boolean isPlayer;

    public CSyncCapabilityPacket(CompoundNBT compound, int entityID, boolean isPlayer)
    {
        this.compound = compound;
        this.entityID = entityID;
        this.isPlayer = isPlayer;
    }

    public static CSyncCapabilityPacket decode(final PacketBuffer buffer)
    {
        final CompoundNBT compound = buffer.readNbt();
        final int entityID = buffer.readInt();
        final boolean isPlayer = buffer.readBoolean();

        return new CSyncCapabilityPacket(compound, entityID, isPlayer);
    }

    public static void encode(final CSyncCapabilityPacket message, final PacketBuffer buffer)
    {
        buffer.writeNbt(message.compound);
        buffer.writeInt(message.entityID);
        buffer.writeBoolean(message.isPlayer);
    }

    public static void handle(final CSyncCapabilityPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {

            if(message.isPlayer)
            {
                final PlayerEntity playerEntity = (PlayerEntity)Minecraft.getInstance().player.level.getEntity(message.entityID);
                ITravelersBackpack cap = CapabilityUtils.getCapability(playerEntity).orElseThrow(() -> new RuntimeException("No player capability found!"));
                if(cap != null) {
                    cap.setWearable(ItemStack.of(message.compound));
                    cap.setContents(ItemStack.of(message.compound));
                }
            }
            else
            {
                final LivingEntity livingEntity = (LivingEntity)Minecraft.getInstance().player.level.getEntity(message.entityID);
                IEntityTravelersBackpack cap = CapabilityUtils.getEntityCapability(livingEntity).orElseThrow(() -> new RuntimeException("No entity capability found!"));

                if(cap != null)
                {
                    cap.setWearable(ItemStack.of(message.compound));
                }
            }
        }));

        ctx.get().setPacketHandled(true);
    }
}