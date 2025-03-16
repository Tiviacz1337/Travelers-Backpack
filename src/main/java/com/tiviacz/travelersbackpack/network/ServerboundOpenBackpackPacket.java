package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundOpenBackpackPacket(int index, boolean fromSlot) implements CustomPacketPayload {
    public static final Type<ServerboundOpenBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "open_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundOpenBackpackPacket::index,
            ByteBufCodecs.BOOL, ServerboundOpenBackpackPacket::fromSlot,
            ServerboundOpenBackpackPacket::new
    );

    public static void handle(final ServerboundOpenBackpackPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer) {
                int index = message.index;
                if(index >= 0 && index < serverPlayer.getInventory().items.size()) {
                    ItemStack backpackStack = serverPlayer.getInventory().items.get(index);
                    if(backpackStack.getItem() instanceof TravelersBackpackItem) {
                        if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get() && TravelersBackpackConfig.SERVER.backpackSettings.allowOpeningFromSlot.get()) {
                            BackpackContainer.openBackpack(serverPlayer, backpackStack, Reference.ITEM_SCREEN_ID, message.index);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
