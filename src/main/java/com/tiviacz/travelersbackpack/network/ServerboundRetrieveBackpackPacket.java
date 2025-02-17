package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundRetrieveBackpackPacket(ItemStack backpackHolder) {

    public static ServerboundRetrieveBackpackPacket decode(final FriendlyByteBuf buffer) {
        ItemStack backpackHolder = buffer.readItem();

        return new ServerboundRetrieveBackpackPacket(backpackHolder);
    }

    public static void encode(final ServerboundRetrieveBackpackPacket message, final FriendlyByteBuf buffer) {
        buffer.writeItem(message.backpackHolder);
    }

    public static void handle(final ServerboundRetrieveBackpackPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof InventoryMenu menu && menu.getCarried().isEmpty()) {
                if(CapabilityUtils.getCapability(serverPlayer).resolve().get().hasBackpack()) {
                    ItemStack backpack = CapabilityUtils.getCapability(serverPlayer).resolve().get().getBackpack().copy();
                    CapabilityUtils.getCapability(player).ifPresent(attachment -> {
                        BackpackManager.addBackpack(serverPlayer, backpack);
                        attachment.equipBackpack(new ItemStack(Items.AIR, 0));
                        attachment.synchronise();
                    });

                    menu.setCarried(backpack);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}