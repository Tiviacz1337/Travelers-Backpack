package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ServerboundFilterSettingsPacket {
    private final int slot;
    private final List<Integer> settings;

    public ServerboundFilterSettingsPacket(int slot, List<Integer> settings) {
        this.slot = slot;
        this.settings = settings;
    }

    public static ServerboundFilterSettingsPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();
        final List<Integer> settings = buffer.readIntIdList().intStream().boxed().collect(Collectors.toList());

        return new ServerboundFilterSettingsPacket(slot, settings);
    }

    public static void encode(final ServerboundFilterSettingsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        buffer.writeIntIdList(new IntArrayList(message.settings.stream().mapToInt(Integer::intValue).toArray()));
    }

    public static void handle(final ServerboundFilterSettingsPacket message, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();

            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
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
        ctx.get().setPacketHandled(true);
    }
}