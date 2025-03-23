package com.tiviacz.travelersbackpack.init;


import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork {
    public static final ResourceLocation UPDATE_CONFIG_ID = new ResourceLocation(TravelersBackpack.MODID, "update_config");
    public static final ResourceLocation SEND_MESSAGE_ID = new ResourceLocation(TravelersBackpack.MODID, "send_message");
    public static final ResourceLocation SYNC_ITEMSTACK_ID = new ResourceLocation(TravelersBackpack.MODID, "sync_itemstack");
    public static final ResourceLocation UPDATE_RECIPE_ID = new ResourceLocation(TravelersBackpack.MODID, "update_recipe");
    public static final ResourceLocation FILTER_SETTINGS_ID = new ResourceLocation(TravelersBackpack.MODID, "filter_settings");
    public static final ResourceLocation SLOTS_ID = new ResourceLocation(TravelersBackpack.MODID, "slots");
    public static final ResourceLocation SUPPORTER_BADGE_SERVERBOUND_ID = new ResourceLocation(TravelersBackpack.MODID, "supporter_badge_serverbound");
    public static final ResourceLocation SUPPORTER_BADGE_CLIENTBOUND_ID = new ResourceLocation(TravelersBackpack.MODID, "supporter_badge_clientbound");
    public static final ResourceLocation RETRIEVE_BACKPACK_ID = new ResourceLocation(TravelersBackpack.MODID, "retrieve_backpack");
    public static final ResourceLocation ACTION_TAG_ID = new ResourceLocation(TravelersBackpack.MODID, "action_tag");

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG_ID, ClientboundUpdateConfigPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SEND_MESSAGE_ID, ClientboundSendMessagePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_ITEMSTACK_ID, ClientboundSyncItemStackPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_RECIPE_ID, ClientboundUpdateRecipePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SUPPORTER_BADGE_CLIENTBOUND_ID, SupporterBadgePacket.Clientbound::handle);

        //Synchronise supporter badge visibility
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            boolean badgeVisibility = TravelersBackpackConfig.getConfig().client.showSupporterBadge;
            PacketDistributorHelper.sendToServer(new SupporterBadgePacket.Serverbound(badgeVisibility));
        });
    }

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(FILTER_SETTINGS_ID, ServerboundFilterSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SLOTS_ID, ServerboundSlotPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SUPPORTER_BADGE_SERVERBOUND_ID, SupporterBadgePacket.Serverbound::handle);
        ServerPlayNetworking.registerGlobalReceiver(RETRIEVE_BACKPACK_ID, ServerboundRetrieveBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(ACTION_TAG_ID, ServerboundActionTagPacket::handle);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            //Load default config from file
            TravelersBackpack.LOGGER.info("Loading config from file...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load();

            //Sync config from server to client if present
            PacketDistributorHelper.sendToPlayer(handler.player, new ClientboundUpdateConfigPacket(TravelersBackpackConfig.writeToNbt()));
        });
    }
}