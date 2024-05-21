package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventsHandler
{
    public static final ModelLayerLocation TRAVELERS_BACKPACK_BLOCK_ENTITY = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "main");
    public static final ModelLayerLocation TRAVELERS_BACKPACK_WEARABLE = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "wearable");
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping("key.travelersbackpack.toggle_tank", GLFW.GLFW_KEY_N, CATEGORY);

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event)
    {
        event.register(OPEN_BACKPACK);
        event.register(SORT_BACKPACK);
        event.register(ABILITY);
        event.register(SWAP_TOOL);
        event.register(TOGGLE_TANK);
    }

    @SubscribeEvent
    public static void registerOverlay(final RegisterGuiOverlaysEvent evt)
    {
        evt.registerBelow(VanillaGuiOverlay.HOTBAR.id(), new ResourceLocation(TravelersBackpack.MODID, "overlay"), (gui, poseStack, partialTick, width, height) ->
        {
            Minecraft mc = Minecraft.getInstance();

            if(TravelersBackpackConfig.enableOverlay && !mc.options.hideGui && AttachmentUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                HudOverlay.renderOverlay(mc, poseStack);
            }
        });
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(BackpackTooltipComponent.class, ClientBackpackTooltipComponent::new);
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
        addPlayerLayer(evt, PlayerSkin.Model.WIDE);
        addPlayerLayer(evt, PlayerSkin.Model.SLIM);

        for(EntityType type : Reference.COMPATIBLE_TYPE_ENTRIES)
        {
            if(TravelersBackpack.endermanOverhaulLoaded && type == EntityType.ENDERMAN) continue;

            addEntityLayer(evt, type);
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model)
    {
        EntityRenderer<? extends Player> renderer = evt.getSkin(model);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
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