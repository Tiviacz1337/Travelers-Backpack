package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.client.screens.widgets.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

public class ServerboundFilterSettingsPacket {
    private final int slot;
    private final List<Integer> settings;

    public ServerboundFilterSettingsPacket(int slot, List<Integer> settings) {
        this.slot = slot;
        this.settings = settings;
    }

    public static ServerboundFilterSettingsPacket decode(final FriendlyByteBuf buffer) {
        final int slot = buffer.readInt();
        final List<Integer> settings = ByteBufCodecs.INT.apply(ByteBufCodecs.list()).decode(buffer);

        return new ServerboundFilterSettingsPacket(slot, settings);
    }

    public static void encode(final ServerboundFilterSettingsPacket message, final FriendlyByteBuf buffer) {
        buffer.writeInt(message.slot);
        ByteBufCodecs.INT.apply(ByteBufCodecs.list()).encode(buffer, message.settings);
    }

    public static void handle(final ServerboundFilterSettingsPacket message, final CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.getSender();

            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    upgradeStack.set(ModDataComponents.FILTER_SETTINGS.get(), message.settings);
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
        ctx.setPacketHandled(true);
    }
}