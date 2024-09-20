package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackModelData;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler
{
    public static final ModelLayerLocation TRAVELERS_BACKPACK_BLOCK_ENTITY = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), "main");
    public static final ModelLayerLocation TRAVELERS_BACKPACK_WEARABLE = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), "wearable");
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
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModItems.BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerMenuScreensEvent(RegisterMenuScreensEvent event)
    {
        event.register(ModMenuTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY.get(), TravelersBackpackScreen::new);
        event.register(ModMenuTypes.TRAVELERS_BACKPACK_ITEM.get(), TravelersBackpackScreen::new);
    }

    @SubscribeEvent
    public static void registerOverlay(final RegisterGuiLayersEvent evt)
    {
        evt.registerBelow(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "overlay"), (pGuiGraphics, pPartialTick) ->
        {
            Minecraft mc = Minecraft.getInstance();

            if(TravelersBackpackConfig.CLIENT.overlay.enableOverlay.get() && !mc.options.hideGui && AttachmentUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR)
            {
                HudOverlay.renderOverlay(mc, pGuiGraphics);
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
        event.registerLayerDefinition(TRAVELERS_BACKPACK_BLOCK_ENTITY, () -> BackpackModelData.createTravelersBackpack(false));
        event.registerLayerDefinition(TRAVELERS_BACKPACK_WEARABLE, () -> BackpackModelData.createTravelersBackpack(true));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt)
    {
        addPlayerLayer(evt, PlayerSkin.Model.WIDE);
        addPlayerLayer(evt, PlayerSkin.Model.SLIM);

        for (EntityType<?> type : evt.getEntityTypes()) {
            if (evt.getRenderer(type) instanceof LivingEntityRenderer livingEntityRenderer) {
                if (livingEntityRenderer.getModel() instanceof HumanoidModel<?>) {

                    if(TravelersBackpack.endermanOverhaulLoaded && type == EntityType.ENDERMAN) continue;
                    if(livingEntityRenderer instanceof PlayerRenderer) continue;

                    livingEntityRenderer.addLayer(new TravelersBackpackEntityLayer(livingEntityRenderer));
                }
            }
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model)
    {
        EntityRenderer<? extends Player> renderer = evt.getSkin(model);

        if(renderer instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new TravelersBackpackLayer(livingRenderer));
        }
    }

    public static void registerBlockEntityRenderers()
    {
        BlockEntityRenderers.register(ModBlockEntityTypes.TRAVELERS_BACKPACK.get(), TravelersBackpackBlockEntityRenderer::new);
    }

    public static void registerItemModelProperties()
    {
        ItemProperties.register(ModItems.HOSE.get(), ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID,"mode"), (stack, world, entity, propertyFunction) -> {
            if(stack.has(ModDataComponents.HOSE_MODES))
            {
                int mode = stack.get(ModDataComponents.HOSE_MODES).get(0);
                return (float)mode / 10.0F;
            }
            return 0.0F;
        });
    }
}