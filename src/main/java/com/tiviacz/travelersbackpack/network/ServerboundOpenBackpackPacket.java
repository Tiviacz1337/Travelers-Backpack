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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

public class ServerboundOpenBackpackPacket implements IPacket<ServerboundOpenBackpackPacket> {
    private final int slotIndex;

    public ServerboundOpenBackpackPacket(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public static ServerboundOpenBackpackPacket decode(final FriendlyByteBuf buffer) {
        final int slotIndex = buffer.readInt();

        return new ServerboundOpenBackpackPacket(slotIndex);
    }

    public void encode(final ServerboundOpenBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slotIndex);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.OPEN_BACKPACK_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundOpenBackpackPacket message = decode(buf);
        server.execute(() -> {
            Slot slot = player.containerMenu.getSlot(message.slotIndex);
            if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(player) && slot.container instanceof Inventory) {
                if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOnlyEquippedBackpack) {
                    BackpackContainer.openBackpack(player, slot.getItem(), Reference.ITEM_SCREEN_ID);
                }
            }
        });
    }
}