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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundOpenBackpackPacket(int slotIndex) implements CustomPacketPayload {
    public static final Type<ServerboundOpenBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "open_backpack"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundOpenBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundOpenBackpackPacket::slotIndex,
            ServerboundOpenBackpackPacket::new
    );

    public static void handle(final ServerboundOpenBackpackPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Slot slot = player.containerMenu.getSlot(message.slotIndex());
            if(player.containerMenu instanceof InventoryMenu menu) {

            }
            if(slot != null && slot.getItem().getItem() instanceof TravelersBackpackItem && slot.allowModification(player) && slot.container instanceof Inventory) {
                if(!TravelersBackpackConfig.SERVER.backpackSettings.allowOnlyEquippedBackpack.get()) {
                    BackpackContainer.openBackpack((ServerPlayer)player, slot.getItem(), Reference.ITEM_SCREEN_ID);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
