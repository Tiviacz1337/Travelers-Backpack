package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public record ClientboundUpdateConfigPacket(CompoundTag compound) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientboundUpdateConfigPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "update_config"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateConfigPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG, ClientboundUpdateConfigPacket::compound, ClientboundUpdateConfigPacket::new);

    public static void handle(ClientboundUpdateConfigPacket message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            TravelersBackpack.LOGGER.info("Syncing config from server to client...");
            TravelersBackpackConfigData configData = TravelersBackpackConfig.readFromNbt(message.compound());
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).setConfig(configData);

            //Abilities
            BackpackAbilities.ALLOWED_ABILITIES.clear();
            TravelersBackpackConfig.loadItemsFromConfig(TravelersBackpackConfig.getConfig().backpackAbilities.allowedAbilities, com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES);

            //Load Backpack Effects
            BackpackAbilities.getBackpackEffects().clear();
            TravelersBackpackConfig.loadBackpackEffectsFromConfig(configData.backpackAbilities.backpackEffects, com.tiviacz.travelersbackpack.common.BackpackAbilities.BACKPACK_EFFECTS);

            //Update allowed abilities if added effect
            com.tiviacz.travelersbackpack.common.BackpackAbilities.getBackpackEffects().entries().stream().forEach(entry -> {
                if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.contains(entry.getKey())) {
                    com.tiviacz.travelersbackpack.common.BackpackAbilities.ALLOWED_ABILITIES.add(entry.getKey());
                }
                if(!com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.contains(entry.getKey())) {
                    com.tiviacz.travelersbackpack.common.BackpackAbilities.ITEM_ABILITIES_LIST.add(entry.getKey());
                }
            });

            //Remove all abilities that are not allowed //#TODO probably tweak
            List<Item> allowed = new ArrayList<>(BackpackAbilities.ALLOWED_ABILITIES);
            BackpackAbilities.ITEM_ABILITIES_LIST.removeIf(item -> !allowed.contains(item));

            //Cooldowns
            BackpackAbilities.getCooldowns().clear();
            TravelersBackpackConfig.loadCooldownsFromConfig(configData.backpackAbilities.cooldowns, com.tiviacz.travelersbackpack.common.BackpackAbilities.COOLDOWNS);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
