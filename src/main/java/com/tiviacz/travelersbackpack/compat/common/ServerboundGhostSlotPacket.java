package com.tiviacz.travelersbackpack.compat.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.network.IPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public record ServerboundGhostSlotPacket(ItemStack stack,
                                         int slotNumber) implements IPacket<ServerboundGhostSlotPacket> {
    public static final ResourceLocation GHOST_SLOT_ID = new ResourceLocation(TravelersBackpack.MODID, "set_ghost_slot");

    public static ServerboundGhostSlotPacket decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        int slotNumber = buffer.readInt();
        return new ServerboundGhostSlotPacket(stack, slotNumber);
    }

    @Override
    public void encode(ServerboundGhostSlotPacket message, FriendlyByteBuf buffer) {
        buffer.writeItem(message.stack);
        buffer.writeInt(message.slotNumber);
    }

    @Override
    public ResourceLocation getPacketId() {
        return GHOST_SLOT_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundGhostSlotPacket message = decode(buf);
        if(!(player.containerMenu instanceof BackpackBaseMenu)) {
            return;
        }
        player.containerMenu.getSlot(message.slotNumber).set(message.stack);
    }
}