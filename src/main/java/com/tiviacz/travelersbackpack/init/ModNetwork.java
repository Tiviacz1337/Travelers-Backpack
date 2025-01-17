package com.tiviacz.travelersbackpack.init;


import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfigData;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork {
    public static final ResourceLocation UPDATE_CONFIG_ID = new ResourceLocation(TravelersBackpack.MODID, "update_config");
    public static final ResourceLocation SEND_MESSAGE_ID = new ResourceLocation(TravelersBackpack.MODID, "send_message");
    public static final ResourceLocation SYNC_ITEMSTACK_ID = new ResourceLocation(TravelersBackpack.MODID, "sync_itemstack");
    public static final ResourceLocation UPDATE_RECIPE_ID = new ResourceLocation(TravelersBackpack.MODID, "update_recipe");
    public static final ResourceLocation ABILITY_SLIDER_ID = new ResourceLocation(TravelersBackpack.MODID, "ability_slider");
    public static final ResourceLocation EQUIP_BACKPACK_ID = new ResourceLocation(TravelersBackpack.MODID, "equip_backpack");
    public static final ResourceLocation FILL_TANK_ID = new ResourceLocation(TravelersBackpack.MODID, "fill_tank");
    public static final ResourceLocation FILTER_SETTINGS_ID = new ResourceLocation(TravelersBackpack.MODID, "filter_settings");
    public static final ResourceLocation OPEN_BACKPACK_ID = new ResourceLocation(TravelersBackpack.MODID, "open_backpack");
    public static final ResourceLocation OPEN_SETTINGS_ID = new ResourceLocation(TravelersBackpack.MODID, "open_settings");
    public static final ResourceLocation REMOVE_UPGRADE_ID = new ResourceLocation(TravelersBackpack.MODID, "remove_upgrade");
    public static final ResourceLocation SHOW_TOOL_SLOTS_ID = new ResourceLocation(TravelersBackpack.MODID, "show_tool_slots");
    public static final ResourceLocation SLEEPING_BAG_ID = new ResourceLocation(TravelersBackpack.MODID, "sleeping_bag");
    public static final ResourceLocation SLOTS_ID = new ResourceLocation(TravelersBackpack.MODID, "slots");
    public static final ResourceLocation SORTER_ID = new ResourceLocation(TravelersBackpack.MODID, "sorter");
    public static final ResourceLocation SPECIAL_ACTION_ID = new ResourceLocation(TravelersBackpack.MODID, "special_action");
    public static final ResourceLocation TAB_ID = new ResourceLocation(TravelersBackpack.MODID, "tab");

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG_ID, ClientboundUpdateConfigPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SEND_MESSAGE_ID, ClientboundSendMessagePacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(SYNC_ITEMSTACK_ID, ClientboundSyncItemStackPacket::handle);
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_RECIPE_ID, ClientboundUpdateRecipePacket::handle);
    }

    public static void initServer() {
        ServerPlayNetworking.registerGlobalReceiver(ABILITY_SLIDER_ID, ServerboundAbilitySliderPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(EQUIP_BACKPACK_ID, ServerboundEquipBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(FILL_TANK_ID, ServerboundFillTankPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(FILTER_SETTINGS_ID, ServerboundFilterSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(OPEN_BACKPACK_ID, ServerboundOpenBackpackPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(OPEN_SETTINGS_ID, ServerboundOpenSettingsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_UPGRADE_ID, ServerboundRemoveUpgradePacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SHOW_TOOL_SLOTS_ID, ServerboundShowToolSlotsPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SLEEPING_BAG_ID, ServerboundSleepingBagPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SLOTS_ID, ServerboundSlotPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SORTER_ID, ServerboundSorterPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SPECIAL_ACTION_ID, ServerboundSpecialActionPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(TAB_ID, ServerboundTabPacket::handle);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            //Load default config from file
            TravelersBackpack.LOGGER.info("Loading config from file...");
            AutoConfig.getConfigHolder(TravelersBackpackConfigData.class).load();

            //Sync config from server to client if present
            PacketDistributorHelper.sendToPlayer(handler.player, new ClientboundUpdateConfigPacket(TravelersBackpackConfig.writeToNbt()));
        });
    }
}