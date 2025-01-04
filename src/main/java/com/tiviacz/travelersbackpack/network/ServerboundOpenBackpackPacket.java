package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundOpenBackpackPacket {
    private final int slotIndex;

    public ServerboundOpenBackpackPacket(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public static ServerboundOpenBackpackPacket decode(final FriendlyByteBuf buffer) {
        final int slotIndex = buffer.readInt();

        return new ServerboundOpenBackpackPacket(slotIndex);
    }

    public static void encode(final ServerboundOpenBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slotIndex);
    }

    public static void handle(final ServerboundOpenBackpackPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            Slot slot = player.containerMenu.getSlot(message.slotIndex);
            if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(player) && slot.container instanceof Inventory) {
                if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
                    BackpackContainer.openBackpack((ServerPlayer)player, slot.getItem(), Reference.ITEM_SCREEN_ID);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}