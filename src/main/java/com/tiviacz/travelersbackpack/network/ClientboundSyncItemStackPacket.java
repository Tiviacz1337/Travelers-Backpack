package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackItemMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundSyncItemStackPacket(int entityId, int slot, ItemStack itemStackInstance,
                                             DataComponentMap map) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_itemstack");
    public static final Type<ClientboundSyncItemStackPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncItemStackPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncItemStackPacket::entityId,
            ByteBufCodecs.INT, ClientboundSyncItemStackPacket::slot,
            ByteBufCodecs.fromCodec(ItemStack.SIMPLE_ITEM_CODEC), ClientboundSyncItemStackPacket::itemStackInstance,
            ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC), ClientboundSyncItemStackPacket::map,
            ClientboundSyncItemStackPacket::new
    );

    public static void handle(final ClientboundSyncItemStackPacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityId());

                //Sync clientside wrapper if integration enabled (Wrapper created on the fly)
                if(player != null && message.slot() == -1) {
                    if(player.containerMenu instanceof BackpackItemMenu menu) {
                        ItemStack oldStack = menu.getWrapper().getBackpackStack().copy();
                        oldStack.applyComponents(message.map());
                        menu.getWrapper().setBackpackStack(oldStack);
                        return;
                    }
                    return;
                }

                if(player != null && player.getInventory().items.get(message.slot()).is(message.itemStackInstance().getItem())) {
                    //Sync component changes on client
                    player.getInventory().items.get(message.slot()).applyComponents(message.map());

                    //Update Item Backpack
                    if(player.containerMenu instanceof BackpackBaseMenu menu) {
                        menu.getWrapper().setBackpackStack(player.getInventory().items.get(message.slot()));
                    }
                }
            });
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}