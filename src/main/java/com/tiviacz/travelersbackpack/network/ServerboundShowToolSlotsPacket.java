package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class ServerboundShowToolSlotsPacket implements IPacket<ServerboundShowToolSlotsPacket> {
    private final boolean show;

    public ServerboundShowToolSlotsPacket(boolean show) {
        this.show = show;
    }

    public static ServerboundShowToolSlotsPacket decode(final FriendlyByteBuf buffer) {
        final boolean show = buffer.readBoolean();

        return new ServerboundShowToolSlotsPacket(show);
    }

    public void encode(final ServerboundShowToolSlotsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.show);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SHOW_TOOL_SLOTS_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundShowToolSlotsPacket message = decode(buf);
        server.execute(() -> {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                menu.getWrapper().setShowToolSlots(message.show);
                menu.getWrapper().requestMenuUpdate(false);

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataHelper.SHOW_TOOL_SLOTS);
            }
        });
    }
}