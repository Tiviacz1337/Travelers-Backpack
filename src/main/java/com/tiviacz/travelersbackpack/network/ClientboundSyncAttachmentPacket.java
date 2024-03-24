package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.capability.entity.IEntityTravelersBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ClientboundSyncAttachmentPacket(int entityID, boolean isPlayer, CompoundTag compound) implements CustomPacketPayload
{
    public static final ResourceLocation ID = new ResourceLocation(TravelersBackpack.MODID, "sync_attachment");

    public ClientboundSyncAttachmentPacket(FriendlyByteBuf friendlyByteBuf)
    {
        this(friendlyByteBuf.readInt(), friendlyByteBuf.readBoolean(), friendlyByteBuf.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf friendlyByteBuf)
    {
        friendlyByteBuf.writeInt(entityID);
        friendlyByteBuf.writeBoolean(isPlayer);
        friendlyByteBuf.writeNbt(compound);
    }

    @Override
    public ResourceLocation id()
    {
        return ID;
    }

    public static void handle(final ClientboundSyncAttachmentPacket message, PlayPayloadContext ctx)
    {
        ctx.workHandler().submitAsync(() ->
        {
            if(message.isPlayer)
            {
                final Player playerEntity = (Player) Minecraft.getInstance().player.level().getEntity(message.entityID);
                ITravelersBackpack data = AttachmentUtils.getAttachment(playerEntity).orElseThrow(() -> new RuntimeException("No player attachment data found!"));

                if(data != null)
                {
                    data.setWearable(ItemStack.of(message.compound));
                    data.setContents(ItemStack.of(message.compound));
                }
            }
            else
            {
                final LivingEntity livingEntity = (LivingEntity)Minecraft.getInstance().player.level().getEntity(message.entityID);
                IEntityTravelersBackpack data = AttachmentUtils.getEntityAttachment(livingEntity).orElseThrow(() -> new RuntimeException("No entity attachment data found!"));

                if(data != null)
                {
                    data.setWearable(ItemStack.of(message.compound));
                }
            }
        });
    }
}