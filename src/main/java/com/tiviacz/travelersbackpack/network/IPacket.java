package com.tiviacz.travelersbackpack.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket<T> {
    ResourceLocation getPacketId();

    void encode(T message, FriendlyByteBuf buffer);
}