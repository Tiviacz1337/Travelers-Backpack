package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ClientboundUpdateConfigPacket implements IPacket<ClientboundUpdateConfigPacket> {
    private final CompoundTag configTag;

    public ClientboundUpdateConfigPacket(CompoundTag configTag) {
        this.configTag = configTag;
    }

    public static ClientboundUpdateConfigPacket decode(FriendlyByteBuf buffer) {
        CompoundTag configTag = buffer.readNbt();
        return new ClientboundUpdateConfigPacket(configTag);
    }

    public void encode(ClientboundUpdateConfigPacket message, FriendlyByteBuf buffer) {
        buffer.writeNbt(message.configTag);
    }

    public ResourceLocation getPacketId() {
        return ModNetwork.UPDATE_CONFIG_ID;
    }

    public static void handle(Minecraft client, ClientPacketListener listener, FriendlyByteBuf buf, PacketSender sender) {
        ClientboundUpdateConfigPacket message = decode(buf);
        client.execute(() -> {
            TravelersBackpack.LOGGER.info("Syncing config from server to client...");
            TravelersBackpackConfigData configData = TravelersBackpackConfig.readFromNbt(message.configTag);
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
}