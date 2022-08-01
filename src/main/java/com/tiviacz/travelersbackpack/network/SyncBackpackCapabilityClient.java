package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBackpackCapabilityClient
{
    private final CompoundNBT compound;
    private final int entityID;

    public SyncBackpackCapabilityClient(CompoundNBT compound, int entityID)
    {
        this.compound = compound;
        this.entityID = entityID;
    }

    public static SyncBackpackCapabilityClient decode(final PacketBuffer buffer)
    {
        final CompoundNBT compound = buffer.readNbt();
        final int entityID = buffer.readInt();

        return new SyncBackpackCapabilityClient(compound, entityID);
    }

    public static void encode(final SyncBackpackCapabilityClient message, final PacketBuffer buffer)
    {
        buffer.writeNbt(message.compound);
        buffer.writeInt(message.entityID);
    }

    public static void handle(final SyncBackpackCapabilityClient message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

            final PlayerEntity playerEntity = (PlayerEntity)Minecraft.getInstance().player.level.getEntity(message.entityID);
            ITravelersBackpack cap = CapabilityUtils.getCapability(playerEntity).orElse(null);
            if(cap != null) {
                cap.setWearable(ItemStack.of(message.compound));
                cap.setContents(ItemStack.of(message.compound));
            }
        }));

        ctx.get().setPacketHandled(true);
    }
}