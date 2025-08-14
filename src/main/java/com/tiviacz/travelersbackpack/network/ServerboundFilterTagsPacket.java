package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.FilterUpgradeBase;
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

public record ServerboundFilterTagsPacket(int slot, List<String> tags) implements CustomPacketPayload {
    public static final Type<ServerboundFilterTagsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "filter_tags"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundFilterTagsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerboundFilterTagsPacket::slot,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), ServerboundFilterTagsPacket::tags,
            ServerboundFilterTagsPacket::new
    );

    public static void handle(final ServerboundFilterTagsPacket message, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            if(player instanceof ServerPlayer serverPlayer && serverPlayer.containerMenu instanceof BackpackBaseMenu menu) {
                BackpackWrapper wrapper = menu.getWrapper();
                if(!wrapper.getUpgrades().getStackInSlot(message.slot).isEmpty()) {

                    ItemStack upgradeStack = wrapper.getUpgrades().getStackInSlot(message.slot).copy();
                    upgradeStack.set(ModDataComponents.FILTER_TAGS, message.tags);
                    wrapper.getUpgrades().setStackInSlot(message.slot, upgradeStack);

                    if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).isPresent()) {
                        if(wrapper.getUpgradeManager().mappedUpgrades.get(message.slot).get() instanceof FilterUpgradeBase<?, ?> filterUpgrade) {
                            filterUpgrade.getFilterSettings().updateFilterTags(message.tags);
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
