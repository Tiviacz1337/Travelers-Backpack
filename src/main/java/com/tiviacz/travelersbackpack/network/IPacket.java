package com.tiviacz.travelersbackpack.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPacket<T> {
    public ResourceLocation getPacketId();

    public void encode(final T message, final FriendlyByteBuf buffer);
}
