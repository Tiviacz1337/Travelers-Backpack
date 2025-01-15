package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class ServerboundSlotPacket {
    private final byte selectType;
    private final List<Integer> unsortables;
    private final List<Pair<Integer, Boolean>> memorizedSlots;

    public ServerboundSlotPacket(byte selectType, List<Integer> unsortables,
                                 List<Pair<Integer, Boolean>> memorizedSlots) {
        this.selectType = selectType;
        this.unsortables = unsortables;
        this.memorizedSlots = memorizedSlots;
    }

    public static ServerboundSlotPacket decode(final RegistryFriendlyByteBuf buffer) {
        final byte selectType = buffer.readByte();
        final List<Integer> unsortables = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buffer);
        final List<Pair<Integer, Boolean>> memorizedSlots = ByteBufCodecs.fromCodec(Codec.mapPair(Codec.INT.fieldOf("index"), Codec.BOOL.fieldOf("matchComponents")).codec()).apply(ByteBufCodecs.list()).decode(buffer);

        return new ServerboundSlotPacket(selectType, unsortables, memorizedSlots);
    }

    public static void encode(final ServerboundSlotPacket message, final RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(message.selectType);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buffer, message.unsortables);
        ByteBufCodecs.fromCodec(Codec.mapPair(Codec.INT.fieldOf("index"), Codec.BOOL.fieldOf("matchComponents")).codec()).apply(ByteBufCodecs.list()).encode(buffer, message.memorizedSlots);
    }

    public static final byte UNSORTABLES = (byte)0;
    public static final byte MEMORY = (byte)1;

    public static void handle(final ServerboundSlotPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackSettingsMenu menu) {
                if(message.selectType == UNSORTABLES) {
                    menu.getWrapper().setUnsortableSlots(message.unsortables);
                }
                if(message.selectType == MEMORY) {
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> oldMemoryStacks = menu.getWrapper().getMemorySlots();
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> memoryStacks = new ArrayList<>();
                    for(Pair<Integer, Boolean> memorizedSlot : message.memorizedSlots) {
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

        ctx.setPacketHandled(true);
    }
}