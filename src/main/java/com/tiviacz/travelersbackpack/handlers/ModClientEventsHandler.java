package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.OverlayScreen;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventsHandler
{
    public static final ModelLayerLocation TRAVELERS_BACKPACK_BLOCK_ENTITY = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "main");
    public static final ModelLayerLocation TRAVELERS_BACKPACK_WEARABLE = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "wearable");
    public static final KeyMapping OPEN_INVENTORY = new KeyMapping(Reference.INVENTORY, GLFW.GLFW_KEY_B, Reference.CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping(Reference.TOGGLE_TANK, GLFW.GLFW_KEY_N, Reference.CATEGORY);
    public static final KeyMapping CYCLE_TOOL = new KeyMapping(Reference.CYCLE_TOOL, GLFW.GLFW_KEY_Z, Reference.CATEGORY);

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event)
    {
        event.register(OPEN_INVENTORY);
        event.register(TOGGLE_TANK);
        event.register(CYCLE_TOOL);
    }

    @SubscribeEvent
    public static void registerOverlay(final RegisterGuiOverlaysEvent evt)
    {
        evt.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "travelers_backpack", (gui, poseStack, partialTick, width, height) -> {
            Minecraft mc = Minecraft.getInstance();

            if(!mc.options.hideGui && CapabilityUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                OverlayScreen.renderOverlay(gui, mc, poseStack);
            }
        });
    }

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(TRAVELERS_BACKPACK_BLOCK_ENTITY, () -> TravelersBackpackBlockEntityRenderer.createTravelersBackpack(false));
        event.registerLayerDefinition(TRAVELERS_BACKPACK_WEARABLE, () -> TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt)
    {
        addPlayerLayer(evt, "default");
        addPlayerLayer(evt, "slim");

        for(EntityType type : Reference.COMPATIBLE_TYPE_ENTRIES)
        {
            addEntityLayer(evt, type);
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String skin)
    {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new TravelersBackpackLayer(livingRenderer));
        }
    }

    private static void addEntityLayer(EntityRenderersEvent.AddLayers evt, EntityType entityType)
    {
        EntityRenderer<? extends LivingEntity> renderer = evt.getRenderer(entityType);

        if(renderer instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new TravelersBackpackEntityLayer(livingRenderer));
        }
    }
}