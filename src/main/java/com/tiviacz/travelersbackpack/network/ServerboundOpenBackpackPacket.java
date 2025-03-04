package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundOpenBackpackPacket {
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

    public static void encode(final ServerboundOpenBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.index);
        buffer.writeBoolean(message.fromSlot);
    }

    public static void handle(final ServerboundOpenBackpackPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer) {
                int index = message.index;
                if(index >= 0 && index < serverPlayer.getInventory().items.size()) {
                    ItemStack backpackStack = serverPlayer.getInventory().items.get(index);
                    if(backpackStack.getItem() instanceof TravelersBackpackItem) {
                        if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
                            if(!message.fromSlot || TravelersBackpackConfig.SERVER.backpackSettings.allowOpeningFromSlot.get()) {
                                BackpackContainer.openBackpack(serverPlayer, backpackStack, Reference.ITEM_SCREEN_ID, message.index);
                            }
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}