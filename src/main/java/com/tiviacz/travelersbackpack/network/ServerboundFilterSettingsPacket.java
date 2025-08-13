package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.upgrades.filter.IFilter;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ServerboundFilterSettingsPacket(int slot, List<Integer> settings) implements CustomPacketPayload {
    public static final Type<ServerboundFilterSettingsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "filter_settings"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFilterSettingsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundFilterSettingsPacket::slot,
            ByteBufCodecs.INT.apply(ByteBufCodecs.list()), ServerboundFilterSettingsPacket::settings,
            ServerboundFilterSettingsPacket::new
    );

    public static void handle(final ServerboundFilterSettingsPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            Player player = ctx.player();

            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot()).isEmpty()) {

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot()).copy();
                    upgradeStack.set(ModDataComponents.FILTER_SETTINGS, message.settings());
                    wrapper.getUpgrades().setStackInSlot(message.slot(), upgradeStack);

                    if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot()).isPresent()) {
                        if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot()).get() instanceof IFilter filter) {
                            filter.updateSettings();
                        }
                    }
                    menu.getWrapper().saveHandler.run();
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
