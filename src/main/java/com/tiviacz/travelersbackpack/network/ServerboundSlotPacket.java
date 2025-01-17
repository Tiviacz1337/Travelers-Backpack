package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServerboundSlotPacket implements IPacket<ServerboundSlotPacket> {
    private final byte selectType;
    private final List<?> slotsData;

    public ServerboundSlotPacket(byte selectType, List<?> slotsData) {
        this.selectType = selectType;
        this.slotsData = slotsData;
    }

    public static ServerboundSlotPacket decode(final FriendlyByteBuf buffer) {
        final byte selectType = buffer.readByte();
        List<?> slotsData = new ArrayList<>();
        if(selectType == UNSORTABLES) {
            slotsData = buffer.readIntIdList().intStream().boxed().collect(Collectors.toList());
        }
        if(selectType == MEMORY) {
            slotsData = NbtHelper.deserializeMemorySlotsPacket(buffer.readNbt());
        }
        return new ServerboundSlotPacket(selectType, slotsData);
    }

    public void encode(final ServerboundSlotPacket message, final FriendlyByteBuf buffer) {
        buffer.writeByte(message.selectType);
        List<?> slotsData = message.slotsData;
        if(message.selectType == UNSORTABLES) {
            List<Integer> unsortables = (List<Integer>)slotsData;
            buffer.writeIntIdList(new IntArrayList(unsortables.stream().mapToInt(Integer::intValue).toArray()));
        }
        if(message.selectType == MEMORY) {
            buffer.writeNbt(NbtHelper.serializeMemorySlotsPacket((List<Pair<Integer, Boolean>>)slotsData));
        }
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.SLOTS_ID;
    }

    public static final byte UNSORTABLES = (byte)0;
    public static final byte MEMORY = (byte)1;

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundSlotPacket message = decode(buf);
        server.execute(() -> {
            if(player.containerMenu instanceof BackpackSettingsMenu menu) {
                String syncKey = null;

                if(message.selectType == UNSORTABLES) {
                    menu.getWrapper().setUnsortableSlots((List<Integer>)message.slotsData);
                    syncKey = ModDataHelper.UNSORTABLE_SLOTS;
                }
                if(message.selectType == MEMORY) {
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> oldMemoryStacks = menu.getWrapper().getMemorySlots();
                    List<Pair<Integer, Pair<ItemStack, Boolean>>> memoryStacks = new ArrayList<>();
                    for(Pair<Integer, Boolean> memorizedSlot : (List<Pair<Integer, Boolean>>)message.slotsData) {
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
                    //menu.getWrapper().setMemorySlots((List<Pair<Integer, Pair<ItemStack, Boolean>>>)message.slotsData);
                    syncKey = ModDataHelper.MEMORY_SLOTS;
                }

                //Update backpack data on clients
                if(syncKey != null) {
                    menu.getWrapper().sendDataToClients(syncKey);
                }
            }
        });
    }
}