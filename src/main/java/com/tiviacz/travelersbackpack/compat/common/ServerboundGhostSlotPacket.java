package com.tiviacz.travelersbackpack.compat.common;

import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundGhostSlotPacket {
    private final ItemStack stack;
    private final int slotNumber;

    public ServerboundGhostSlotPacket(ItemStack stack, int slotNumber) {
        this.stack = stack;
        this.slotNumber = slotNumber;
    }

    public static ServerboundGhostSlotPacket decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        int slotNumber = buffer.readInt();
        return new ServerboundGhostSlotPacket(stack, slotNumber);
    }

    public static void encode(ServerboundGhostSlotPacket message, FriendlyByteBuf buffer) {
        buffer.writeItem(message.stack);
        buffer.writeInt(message.slotNumber);
    }

    public static void handle(final ServerboundGhostSlotPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(!(player.containerMenu instanceof BackpackBaseMenu)) {
                return;
            }
            player.containerMenu.getSlot(message.slotNumber).set(message.stack);
        });
        ctx.get().setPacketHandled(true);
    }
}