package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record ServerboundRetrieveBackpackPacket(ItemStack backpackHolder) implements CustomPacketPayload {
    public static final Type<ServerboundRetrieveBackpackPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "retrieve_backpack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundRetrieveBackpackPacket> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, ServerboundRetrieveBackpackPacket::backpackHolder,
            ServerboundRetrieveBackpackPacket::new
    );

    public static void handle(ServerboundRetrieveBackpackPacket message, ServerPlayNetworking.Context ctx) {
        ctx.server().execute(() -> {
            if(ctx.player().containerMenu instanceof InventoryMenu menu && menu.getCarried().isEmpty()) {
                if(ComponentUtils.getComponent(ctx.player()).get().hasBackpack()) {
                    ItemStack backpack = ComponentUtils.getComponent(ctx.player()).get().getBackpack().copy();
                    ComponentUtils.getComponent(ctx.player()).ifPresent(attachment -> {
                        BackpackManager.addBackpack(ctx.player(), backpack);
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