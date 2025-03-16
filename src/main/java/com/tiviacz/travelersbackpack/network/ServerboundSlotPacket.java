package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record ServerboundSlotPacket(int selectType, List<Integer> unsortables,
                                    List<Pair<Integer, Boolean>> memorizedSlots) implements CustomPacketPayload {
    public static final Type<ServerboundSlotPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSlotPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundSlotPacket::selectType,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), ServerboundSlotPacket::unsortables,
            ByteBufCodecs.fromCodec(Codec.mapPair(Codec.INT.fieldOf("index"), Codec.BOOL.fieldOf("matchComponents")).codec()).apply(ByteBufCodecs.list()), ServerboundSlotPacket::memorizedSlots,
            ServerboundSlotPacket::new
    );

    public static final int UNSORTABLES = 0;
    public static final int MEMORY = 1;

    public static void handle(final ServerboundSlotPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackSettingsMenu menu) {
                if(message.selectType() == UNSORTABLES) {
                    menu.getWrapper().setUnsortableSlots(message.unsortables());
                }
                if(message.selectType() == MEMORY) {
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> oldMemoryStacks = menu.getWrapper().getMemorySlots();
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> memoryStacks = new ArrayList<>();
                    for(Pair<Integer, Boolean> memorizedSlot : message.memorizedSlots()) {
                        ItemStack retrievedStack = memorizedSlot.getSecond() ? menu.getSlot(memorizedSlot.getFirst()).getItem() : menu.getSlot(memorizedSlot.getFirst()).getItem().getItem().getDefaultInstance();
                        if(retrievedStack.isEmpty()) {
                            for(Pair<Integer, Pair<ItemStack, Boolean>> oldMemorizedSlot : oldMemoryStacks) {
                                if(oldMemorizedSlot.getFirst().equals(memorizedSlot.getFirst())) {
                                    retrievedStack = oldMemorizedSlot.getSecond().getFirst();
                                    break;
                                }
                            }
                        }
                        if(retrievedStack.isEmpty()) {
                            continue; //not allowed in codec
                        }
                        memoryStacks.add(Pair.of(memorizedSlot.getFirst(), Pair.of(retrievedStack, memorizedSlot.getSecond())));
                    }
                    menu.getWrapper().setMemorySlots(memoryStacks);
                }

                //Update backpack data on clients
                menu.getWrapper().sendDataToClients(ModDataComponents.SLOTS.get());
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
