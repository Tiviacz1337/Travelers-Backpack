package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBackpackCapabilityClient
{
    private final CompoundTag compound;
    private final int entityID;

    public SyncBackpackCapabilityClient(CompoundTag compound, int entityID)
    {
        this.compound = compound;
        this.entityID = entityID;
    }

    public static SyncBackpackCapabilityClient decode(final FriendlyByteBuf buffer)
    {
        final CompoundTag compound = buffer.readAnySizeNbt();
        final int entityID = buffer.readInt();

        return new SyncBackpackCapabilityClient(compound, entityID);
    }

    public static void encode(final SyncBackpackCapabilityClient message, final FriendlyByteBuf buffer)
    {
        buffer.writeNbt(message.compound);
        buffer.writeInt(message.entityID);
    }

    public static void handle(final SyncBackpackCapabilityClient message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

            final Player playerEntity = (Player)Minecraft.getInstance().player.level.getEntity(message.entityID);
            ITravelersBackpack cap = CapabilityUtils.getCapability(playerEntity).orElse(null);

            if(cap != null)
            {
                cap.setWearable(ItemStack.of(message.compound));
            }
        }));

        ctx.get().setPacketHandled(true);
    }
}