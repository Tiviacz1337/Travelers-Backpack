package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ClientboundSyncAttachmentPacket(int entityID, ItemStack backpack,
                                              boolean removeData) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "sync_attachment");
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
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER.get())) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER.get());
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

    public static void handle(final ClientboundSyncAttachmentPacket message, IPayloadContext ctx) {
        if(ctx.flow().isClientbound()) {
            ctx.enqueueWork(() -> {
                Player playerEntity = (Player)Minecraft.getInstance().level.getEntity(message.entityID());
                Optional<ITravelersBackpack> data = AttachmentUtils.getAttachment(playerEntity); //.orElseThrow(() -> new RuntimeException("No player attachment data found!"));
                if(data.isPresent()) {
                    if(message.removeData()) {
                        data.get().remove();
                    } else {
                        data.get().updateBackpack(message.backpack());
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