package com.tiviacz.travelersbackpack.component.entity;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import dev.onyxstudios.cca.internal.entity.CardinalComponentsEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class EntityTravelersBackpackComponent implements IEntityTravelersBackpackComponent
{
    private ItemStack wearable = null;
    private final LivingEntity livingEntity;

    public EntityTravelersBackpackComponent(LivingEntity livingEntity)
    {
        this.livingEntity = livingEntity;
    }

    @Override
    public boolean hasWearable()
    {
        return this.wearable != null;
    }

    @Override
    public ItemStack getWearable()
    {
        return this.wearable;
    }

    @Override
    public void setWearable(ItemStack stack)
    {
        this.wearable = stack;
    }

    @Override
    public void removeWearable()
    {
        this.wearable = null;
    }

    @Override
    public void sync()
    {
        syncToTracking();
    }

    public void syncToTracking()
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(this.livingEntity.getId());
        buf.writeIdentifier(ComponentUtils.ENTITY_WEARABLE.getId());

        for(ServerPlayerEntity serverPlayer : PlayerLookup.tracking(this.livingEntity))
        {
            this.writeSyncPacket(buf, serverPlayer);
            ServerPlayNetworking.send(serverPlayer, CardinalComponentsEntity.PACKET_ID, buf);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        ItemStack wearable = ItemStack.fromNbt(tag);

        if(wearable.isEmpty())
        {
            setWearable(null);
        }
        else
        {
            setWearable(wearable);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag)
    {
        if(hasWearable())
        {
            ItemStack wearable = getWearable();
            wearable.writeNbt(tag);
        }
        if(!hasWearable())
        {
            ItemStack wearable = ItemStack.EMPTY;
            wearable.writeNbt(tag);
        }
    }
}