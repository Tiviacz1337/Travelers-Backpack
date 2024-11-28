package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public static void handle(final ServerboundOpenBackpackPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            Slot slot = player.containerMenu.getSlot(message.slotIndex);
            if(player.containerMenu instanceof InventoryMenu menu) {

            }
            if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(player) && slot.container instanceof Inventory) {
                if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
                    BackpackContainer.openBackpack((ServerPlayer)player, slot.getItem(), Reference.ITEM_SCREEN_ID);
                }
            }
        });

        ctx.setPacketHandled(true);
    }
}