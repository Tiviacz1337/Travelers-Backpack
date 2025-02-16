package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ServerboundRetrieveBackpackPacket(ItemStack backpackHolder) {

    public static ServerboundRetrieveBackpackPacket decode(final RegistryFriendlyByteBuf buffer) {
        final ItemStack backpackHolder = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);

        return new ServerboundRetrieveBackpackPacket(backpackHolder);
    }

    public static void encode(final ServerboundRetrieveBackpackPacket message, final RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, message.backpackHolder);
    }

    public static void handle(final ServerboundRetrieveBackpackPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof InventoryMenu menu && menu.getCarried().isEmpty()) {
                if(AttachmentUtils.getCapability(serverPlayer).resolve().get().hasBackpack()) {
                    ItemStack backpack = AttachmentUtils.getCapability(serverPlayer).resolve().get().getBackpack().copy();
                    AttachmentUtils.getCapability(player).ifPresent(attachment -> {
                        attachment.equipBackpack(new ItemStack(Items.AIR, 0));
                        attachment.synchronise();
                    });

                    menu.setCarried(backpack);
                }
            }
        });

        ctx.setPacketHandled(true);
    }
}
