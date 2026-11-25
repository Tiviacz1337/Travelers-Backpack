package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientboundVersionCheckPacket(String version) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "version_check");
    public static final Type<ClientboundVersionCheckPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundVersionCheckPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientboundVersionCheckPacket::version,
            ClientboundVersionCheckPacket::new
    );

    public static int versionCheckTimeout = -1;

    public static void handle(ClientboundVersionCheckPacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            versionCheckTimeout = -1;
            if(!message.version().equals(TravelersBackpack.getModVersion())) {
                sendMismatchMessage("The server has version " + message.version() + ", but you have " + TravelersBackpack.getModVersion() + ".");
            }
        });
    }

    public static void tickVersionCheck() {
        if(versionCheckTimeout > 0 && --versionCheckTimeout == 0) {
            versionCheckTimeout = -1;
            sendMismatchMessage("The server has an older version than yours (you have " + TravelersBackpack.getModVersion() + ").");
        }
    }

    public static void sendMismatchMessage(String message) {
        LogHelper.warn("Version mismatch! " + message + " Errors may occur.");
        if(Minecraft.getInstance().player != null) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("[Traveler's Backpack] " + message + " Errors may occur. Use the same version as the server to avoid them.").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}