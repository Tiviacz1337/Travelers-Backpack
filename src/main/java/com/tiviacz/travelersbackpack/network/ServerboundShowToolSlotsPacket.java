package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerboundShowToolSlotsPacket {
    private final boolean show;

    public ServerboundShowToolSlotsPacket(boolean show) {
        this.show = show;
    }

    public static ServerboundShowToolSlotsPacket decode(final FriendlyByteBuf buffer) {
        final boolean show = buffer.readBoolean();

        return new ServerboundShowToolSlotsPacket(show);
    }

    public static void encode(final ServerboundShowToolSlotsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeBoolean(message.show);
    }

    public static void handle(final ServerboundShowToolSlotsPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                menu.getWrapper().setShowToolSlots(message.show);
                menu.getWrapper().requestMenuUpdate(false);

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataComponents.SHOW_TOOL_SLOTS.get());
            }
        });

        ctx.setPacketHandled(true);
    }
}