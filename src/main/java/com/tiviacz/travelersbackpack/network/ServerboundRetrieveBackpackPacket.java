package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record ServerboundRetrieveBackpackPacket(
        ItemStack backpackHolder) implements IPacket<ServerboundRetrieveBackpackPacket> {
    public static ServerboundRetrieveBackpackPacket decode(final FriendlyByteBuf buffer) {
        ItemStack backpackHolder = buffer.readItem();
        return new ServerboundRetrieveBackpackPacket(backpackHolder);
    }

    public void encode(final ServerboundRetrieveBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeItem(message.backpackHolder);
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        server.execute(() -> {
            if(player.containerMenu instanceof InventoryMenu menu && menu.getCarried().isEmpty()) {
                if(ComponentUtils.getComponentOptional(player).get().hasBackpack()) {
                    ItemStack backpack = ComponentUtils.getComponentOptional(player).get().getBackpack().copy();
                    ComponentUtils.getComponentOptional(player).ifPresent(attachment -> {
                        BackpackManager.addBackpack(player, backpack);
                        attachment.equipBackpack(new ItemStack(Items.AIR, 0));
                        attachment.synchronise();
                    });
                    menu.setCarried(backpack);
                }
            }
        });
    }

    @Override
    public ResourceLocation getPacketId() {
        return ModNetwork.RETRIEVE_BACKPACK_ID;
    }
}