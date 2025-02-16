package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundRetrieveBackpackPacket(ItemStack backpackHolder) implements CustomPacketPayload {
    public static final Type<ServerboundRetrieveBackpackPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "retrieve_backpack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRetrieveBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ServerboundRetrieveBackpackPacket::backpackHolder,
            ServerboundRetrieveBackpackPacket::new
    );

    public static void handle(ServerboundRetrieveBackpackPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if(ctx.player() instanceof ServerPlayer serverPlayer && ctx.player().containerMenu instanceof InventoryMenu menu && menu.getCarried().isEmpty()) {
                if(AttachmentUtils.getAttachment(ctx.player()).get().hasBackpack()) {
                    ItemStack backpack = AttachmentUtils.getAttachment(ctx.player()).get().getBackpack().copy();
                    AttachmentUtils.getAttachment(ctx.player()).ifPresent(attachment -> {
                        BackpackManager.addBackpack(serverPlayer, backpack);
                        attachment.equipBackpack(new ItemStack(Items.AIR, 0));
                        attachment.synchronise();
                    });
                    menu.setCarried(backpack);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}