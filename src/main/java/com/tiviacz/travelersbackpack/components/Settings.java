package com.tiviacz.travelersbackpack.components;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Settings(List<List<Byte>> settings) {
    public static final Codec<List<List<Byte>>> CODEC = Codec.list(Codec.list(Codec.BYTE));
    public static final StreamCodec<ByteBuf, List<List<Byte>>> STREAM_CODEC = ByteBufCodecs.BYTE.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list());

    public static List<List<Byte>> createSettings(List<Byte>... lists) {
        List<List<Byte>> list = new ArrayList<>(Arrays.asList(lists));
        return list;
    }

    public static List<Byte> createDefaultToolSettings() {
        return Arrays.asList((byte)0);
    }
}