package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class ServerboundFilterSettingsPacket implements IPacket<ServerboundFilterSettingsPacket> {
    private final int slot;
    private final List<Integer> settings;

    public ServerboundFilterSettingsPacket(int slot, List<Integer> settings) {
        this.slot = slot;
        this.settings = settings;
    }

    public static ServerboundFilterSettingsPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();
        final List<Integer> settings = buffer.readIntIdList().intStream().boxed().collect(Collectors.toList());
        //final List<Integer> settings = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buffer);

        return new ServerboundFilterSettingsPacket(slot, settings);
    }

    public void encode(final ServerboundFilterSettingsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeIntIdList(new IntArrayList(message.settings.stream().mapToInt(Integer::intValue).toArray()));
        // buffer.writeIntIdList();
        //ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buffer, message.settings);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.FILTER_SETTINGS_ID;
    }

    public static void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ServerboundFilterSettingsPacket message = decode(buf);

        server.execute(() -> {
            if(player.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    NbtHelper.set(upgradeStack, ModDataHelper.FILTER_SETTINGS, message.settings);
                    wrapper.getUpgrades().setStackInSlot(message.slot, upgradeStack);

                    if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).isPresent()) {
                        if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).get() instanceof IFilter filter) {
                            filter.updateSettings();
                        }
                    }
                    menu.getWrapper().saveHandler.run();
                }
            }
        });
    }
}