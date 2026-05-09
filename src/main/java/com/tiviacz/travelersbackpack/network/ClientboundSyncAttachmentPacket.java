package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.component.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack,
                                              boolean removeData) implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_attachment");
    public static final Type<ClientboundSyncAttachmentPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncAttachmentPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientboundSyncAttachmentPacket::entityID,
            ItemStack.OPTIONAL_STREAM_CODEC, ClientboundSyncAttachmentPacket::backpack,
            ByteBufCodecs.BOOL, ClientboundSyncAttachmentPacket::removeData,
            ClientboundSyncAttachmentPacket::new
    );

    public ClientboundSyncAttachmentPacket(int entityID, ItemStack serverBackpack) {
        this(entityID, serverBackpack, false);
    }

    public ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack, boolean removeData) {
        this.entityID = entityID;
        //Remove heavy data that is not needed anyways
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
        }
        //Client needs only visual representation, no need to send the whole data
        if(backpackCopy.has(ModDataComponents.SLOTS)) {
            Slots slots = backpackCopy.get(ModDataComponents.SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedStacksHeavy = slots.memory();
            List<Pair<Integer, Pair<ItemStack, Boolean>>> reduced = new ArrayList<>();

            for(Pair<Integer, Pair<ItemStack, Boolean>> outerPair : memorizedStacksHeavy) {
                int index = outerPair.getFirst();
                ItemStack innerStack = outerPair.getSecond().getFirst().copy();
                boolean matchComponents = outerPair.getSecond().getSecond();
                if(matchComponents) {
                    innerStack = new ItemStack(innerStack.getItem(), innerStack.getCount());
                }
                if(innerStack.isEmpty()) {
                    continue;
                }
                reduced.add(Pair.of(index, Pair.of(innerStack, matchComponents)));
            }
            backpackCopy.set(ModDataComponents.SLOTS, new Slots(slots.unsortables(), reduced));
        }
        this.backpack = backpackCopy;
        this.removeData = removeData;
    }

    public static void handle(ClientboundSyncAttachmentPacket message, ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            Player player = (Player)Minecraft.getInstance().level.getEntity(message.entityID());
            AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                if(message.removeData()) {
                    attachment.remove(player);
                } else {
                    attachment.updateBackpack(message.backpack, player);
                }
            });
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}