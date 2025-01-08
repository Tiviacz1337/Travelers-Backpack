package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSyncCapabilityPacket {
    private final int entityID;
    private final ItemStack backpack;
    private final boolean removeData;

    public ClientboundSyncCapabilityPacket(int entityID, ItemStack serverBackpack) {
        this(entityID, serverBackpack, false);
    }

    public ClientboundSyncCapabilityPacket(int entityID, ItemStack backpack, boolean removeData) {
        this.entityID = entityID;
        //Remove heavy data
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.hasTag()) {
            backpackCopy.getTag().remove(ModDataHelper.BACKPACK_CONTAINER);
            //backpackCopy.getTag().remove(ModDataHelper.TOOLS_CONTAINER); //Keep for synchronizing tool slots render
            backpackCopy.getTag().remove(ModDataHelper.UPGRADES);
        }
        this.backpack = backpackCopy;
        this.removeData = removeData;
    }

    public static ClientboundSyncCapabilityPacket decode(final FriendlyByteBuf buffer) {
        final int entityID = buffer.readInt();
        final ItemStack backpack = buffer.readItem();
        final boolean removeData = buffer.readBoolean();
        return new ClientboundSyncCapabilityPacket(entityID, backpack, removeData);
    }

    public static void encode(final ClientboundSyncCapabilityPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.entityID);
        buffer.writeItem(message.backpack);
        buffer.writeBoolean(message.removeData);
    }

    public static void handle(final ClientboundSyncCapabilityPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player playerEntity = (Player)Minecraft.getInstance().level.getEntity(message.entityID);
            LazyOptional<ITravelersBackpack> data = CapabilityUtils.getCapability(playerEntity);
            if(data.isPresent()) {
                if(message.removeData) {
                    data.resolve().get().remove();
                } else {
                    data.resolve().get().updateBackpack(message.backpack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}