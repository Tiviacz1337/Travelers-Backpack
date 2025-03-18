package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.BackpackContainer;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

public record ServerboundOpenBackpackPacket(int index, boolean fromSlot) implements CustomPacketPayload {
    public static final Type<ServerboundOpenBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "open_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundOpenBackpackPacket::index,
            ByteBufCodecs.BOOL, ServerboundOpenBackpackPacket::fromSlot,
            ServerboundOpenBackpackPacket::new
    );

    public static void handle(final ServerboundOpenBackpackPacket message, ServerPlayNetworking.Context ctx) {
        ctx.player().getServer().execute(() -> {
            int index = message.index;
            if(index >= 0 && index < ctx.player().getInventory().items.size()) {
                ItemStack backpackStack = ctx.player().getInventory().items.get(index);
                if(backpackStack.getItem() instanceof TravelersBackpackItem) {
                    if(!TravelersBackpackConfig.getConfig().backpackSettings.allowOnlyEquippedBackpack) {
                        if(!message.fromSlot || TravelersBackpackConfig.getConfig().backpackSettings.allowOpeningFromSlot) {
                            BackpackContainer.openBackpack(ctx.player(), backpackStack, Reference.ITEM_SCREEN_ID, message.index);
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
