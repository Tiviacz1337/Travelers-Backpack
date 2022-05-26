package com.tiviacz.travelersbackpack.component;

import dev.onyxstudios.cca.internal.entity.CardinalComponentsEntity;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class TravelersBackpackComponent implements ITravelersBackpackComponent
{
    private ItemStack wearable = ItemStack.EMPTY;
    private final PlayerEntity player;

    public TravelersBackpackComponent(PlayerEntity player)
    {
        this.player = player;
    }

    @Override
    public boolean hasWearable()
    {
        return !this.wearable.isEmpty();
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
        this.wearable = ItemStack.EMPTY;
    }

    @Override
    public void sync()
    {
        syncWith((ServerPlayerEntity)this.player);
    }

    public void syncToTracking(ServerPlayerEntity player)
    {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getId());
        buf.writeIdentifier(ComponentUtils.WEARABLE.getId());
        this.writeSyncPacket(buf, player);
        for(ServerPlayerEntity serverPlayer : PlayerLookup.tracking(player))
        {
            ServerPlayNetworking.send(serverPlayer, CardinalComponentsEntity.PACKET_ID, buf);
        }
    }

    public void syncWith(ServerPlayerEntity player) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(player.getId());
        buf.writeIdentifier(ComponentUtils.WEARABLE.getId());
        this.writeSyncPacket(buf, player);
        ServerPlayNetworking.send(player, CardinalComponentsEntity.PACKET_ID, buf);
    }

    @Override
    public void readFromNbt(NbtCompound tag)
    {
        ItemStack wearable = ItemStack.fromNbt(tag);
        setWearable(wearable);
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