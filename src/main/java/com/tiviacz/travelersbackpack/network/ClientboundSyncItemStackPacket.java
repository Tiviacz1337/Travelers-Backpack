package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.items.HoseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

public class ClientboundSyncItemStackPacket {
    private final int entityID;
    private final int slot;
    private final ItemStack itemStackInstance;
    private final DataComponentMap map;

    public ClientboundSyncItemStackPacket(int entityId, int slot, ItemStack itemStackInstance, DataComponentMap map) {
        this.entityID = entityId;
        this.slot = slot;
        this.itemStackInstance = itemStackInstance;
        this.map = map;
    }

    public static ClientboundSyncItemStackPacket decode(final RegistryFriendlyByteBuf buffer) {
        int entityID = buffer.readInt();
        int slot = buffer.readInt();
        ItemStack itemStackInstance = ByteBufCodecs.fromCodec(ItemStack.SIMPLE_ITEM_CODEC).decode(buffer);
        DataComponentMap map = ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).decode(buffer);

        return new ClientboundSyncItemStackPacket(entityID, slot, itemStackInstance, map);
    }

    public static void encode(final ClientboundSyncItemStackPacket message, final RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeInt(message.slot);
        ByteBufCodecs.fromCodec(ItemStack.SIMPLE_ITEM_CODEC).encode(buffer, message.itemStackInstance);
        ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).encode(buffer, message.map);
    }

    public static void handle(final ClientboundSyncItemStackPacket message, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() ->
        {
            Player player = (Player)Minecraft.getInstance().player.level().getEntity(message.entityID);

            //Sync clientside wrapper if integration enabled (Wrapper created on the fly)
            if(player != null && message.slot == -1) {
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    ItemStack oldStack = menu.getWrapper().getBackpackStack().copy();
                    oldStack.applyComponents(message.map);
                    menu.getWrapper().setBackpackStack(oldStack);
                    return;
                }
                return;
            }

            if(player != null && player.getInventory().items.get(message.slot).is(message.itemStackInstance.getItem())) {
                ItemStack oldStack = player.getInventory().items.get(message.slot).copy();
                //Sync component changes on client
                player.getInventory().items.get(message.slot).applyComponents(message.map);
                ItemStack newStack = player.getInventory().items.get(message.slot).copy();

                //Update Item Backpack
                if(player.containerMenu instanceof BackpackBaseMenu menu) {
                    menu.getWrapper().setBackpackStack(player.getInventory().items.get(message.slot));
                }

                //Display hose mode if changed
                if(message.map.has(ModDataComponents.HOSE_MODES.get())) {
                    int changedMode = getChangedMode(oldStack, newStack);
                    if(changedMode != -1) {
                        player.displayClientMessage(getNextModeMessage(changedMode, message.map.get(ModDataComponents.HOSE_MODES.get()).get(changedMode)), true);
                    }
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    public static int getChangedMode(ItemStack oldStack, ItemStack newStack) {
        if(oldStack.getOrDefault(ModDataComponents.HOSE_MODES.get(), List.of(0, 0)).get(0) != newStack.getOrDefault(ModDataComponents.HOSE_MODES.get(), List.of(0, 0)).get(0)) {
            return 0;
        }
        if(oldStack.getOrDefault(ModDataComponents.HOSE_MODES.get(), List.of(0, 0)).get(1) != newStack.getOrDefault(ModDataComponents.HOSE_MODES.get(), List.of(0, 0)).get(1)) {
            return 1;
        }
        return -1;
    }

    public static Component getNextModeMessage(int changedMode, int data) {
        if(changedMode == 0) {
            if(data == HoseItem.SPILL_MODE) {
                return Component.translatable("item.travelersbackpack.hose.spill");
            } else if(data == HoseItem.DRINK_MODE) {
                return Component.translatable("item.travelersbackpack.hose.drink");
            }
            return Component.translatable("item.travelersbackpack.hose.suck");
        } else {
            if(data == 1) {
                return Component.translatable("hose.travelersbackpack.tank_left");
            } else {
                return Component.translatable("hose.travelersbackpack.tank_right");
            }
        }
    }
}
