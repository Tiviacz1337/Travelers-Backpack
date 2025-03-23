package com.tiviacz.travelersbackpack.network;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackSettingsMenu;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerboundSlotPacket {
    private final int selectType;
    private final List<?> slotsData;

    public ServerboundSlotPacket(int selectType, List<?> slotsData) {
        this.selectType = selectType;
        this.slotsData = slotsData;
    }

    public static ServerboundSlotPacket decode(final FriendlyByteBuf buffer) {
        final int selectType = buffer.readInt();
        List<?> slotsData = new ArrayList<>();
        if(selectType == UNSORTABLES) {
            slotsData = buffer.readIntIdList().intStream().boxed().collect(Collectors.toList());
        }
        if(selectType == MEMORY) {
            slotsData = NbtHelper.deserializeMemorySlotsPacket(buffer.readNbt());
        }
        return new ServerboundSlotPacket(selectType, slotsData);
    }

    public static void encode(final ServerboundSlotPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.selectType);
        List<?> slotsData = message.slotsData;
        if(message.selectType == UNSORTABLES) {
            List<Integer> unsortables = (List<Integer>)slotsData;
            buffer.writeIntIdList(new IntArrayList(unsortables.stream().mapToInt(Integer::intValue).toArray()));
        }
        if(message.selectType == MEMORY) {
            buffer.writeNbt(NbtHelper.serializeMemorySlotsPacket((List<Pair<Integer, Boolean>>)slotsData));
        }
    }

    public static final int UNSORTABLES = 0;
    public static final int MEMORY = 1;

    public static void handle(final ServerboundSlotPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackSettingsMenu menu) {
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
                    syncKey = ModDataHelper.MEMORY_SLOTS;
                }

                //Update backpack data on clients
                if(syncKey != null) {
                    menu.getWrapper().sendDataToClients(syncKey);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}