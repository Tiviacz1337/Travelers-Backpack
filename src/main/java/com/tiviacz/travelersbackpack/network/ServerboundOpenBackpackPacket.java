package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

public class ServerboundOpenBackpackPacket implements IPacket<ServerboundOpenBackpackPacket> {
    private final int index;
    private final boolean fromSlot;

    public ServerboundOpenBackpackPacket(int index, boolean fromSlot) {
        this.index = index;
        this.fromSlot = fromSlot;
    }

    public static ServerboundOpenBackpackPacket decode(final FriendlyByteBuf buffer) {
        final int index = buffer.readInt();
        final boolean fromSlot = buffer.readBoolean();

        return new ServerboundOpenBackpackPacket(index, fromSlot);
    }

    public void encode(final ServerboundOpenBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.index);
        buffer.writeBoolean(message.fromSlot);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.OPEN_BACKPACK_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundOpenBackpackPacket message = decode(buf);
        server.execute(() -> {
            int index = message.index;
            if(index >= 0 && index < player.getInventory().items.size()) {
                ItemStack backpackStack = player.getInventory().items.get(index);
                if(backpackStack.getItem() instanceof TravelersBackpackItem) {
                    if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOnlyEquippedBackpack) {
                        if(!message.fromSlot || TravelersBackpackConfig.getConfig().backpackSettings.allowOpeningFromSlot) {
                            BackpackContainer.openBackpack(player, backpackStack, Reference.ITEM_SCREEN_ID, message.index);
                        }
                    }
                }
            }
        });
    }
}