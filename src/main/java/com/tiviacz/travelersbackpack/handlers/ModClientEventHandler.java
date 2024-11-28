package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.init.ModBlockEntityTypes;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler {
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping("key.travelersbackpack.toggle_tank", GLFW.GLFW_KEY_N, CATEGORY);

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
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
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BackpackTooltipComponent.class, ClientBackpackTooltipComponent::new);
    }

    // public static final ModelLayerLocation BACKPACK_BLOCK = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), "block");
    //public static final ModelLayerLocation BACKPACK = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"), "main");

    //@SubscribeEvent
    //public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        /*ModItems.ITEMS.getEntries().stream().filter(holder -> holder.get() instanceof TravelersBackpackItem).forEach(holder -> {
            event.registerLayerDefinition(createBackpackModelName(holder.getRegisteredName(), true), () -> BackpackModelData.createTravelersBackpack(true));
            event.registerLayerDefinition(createBackpackModelName(holder.getRegisteredName(), false), () -> BackpackModelData.createTravelersBackpack(false));
        }); */
    //event.registerLayerDefinition(BACKPACK_BLOCK, () -> BackpackModelData.createTravelersBackpack(false));
    //event.registerLayerDefinition(BACKPACK, () -> BackpackModelData.createTravelersBackpack(true));
    //}

    public static ModelLayerLocation createBackpackModelName(String name, boolean isWearable) {
        ResourceLocation location = ResourceLocation.tryParse(name);
        return new ModelLayerLocation(location.withPrefix("backpack/"), isWearable ? "main" : "block");
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt) {
        addPlayerLayer(evt, PlayerSkin.Model.WIDE);
        addPlayerLayer(evt, PlayerSkin.Model.SLIM);

        for(EntityType type : evt.getContext().getEntityRenderDispatcher().renderers.keySet()) {
            if(evt.getContext().getEntityRenderDispatcher().renderers.get(type) instanceof LivingEntityRenderer livingEntityRenderer) {
                if(livingEntityRenderer.getModel() instanceof HumanoidModel<?>) {
                    if(TravelersBackpack.endermanOverhaulLoaded && type == EntityType.ENDERMAN) continue;
                    if(livingEntityRenderer instanceof PlayerRenderer) continue;

                    livingEntityRenderer.addLayer(new BackpackEntityLayer(livingEntityRenderer));
                }
            }
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model) {
        EntityRenderer<? extends Player> renderer = evt.getPlayerSkin(model);

        if(renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BackpackLayer(livingRenderer));
        }
    }

    public static void registerScreenFactories() {
        MenuScreens.register(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), BackpackScreen::new);
        MenuScreens.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);
        MenuScreens.register(ModMenuTypes.BACKPACK_SETTINGS_MENU.get(), BackpackSettingsScreen::new);
    }

  /*  @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt) {
        addPlayerLayer(evt, PlayerSkin.Model.WIDE);
        addPlayerLayer(evt, PlayerSkin.Model.SLIM);

        for(EntityType<?> type : evt.getEntityTypes()) {
            if(evt.getRenderer(type) instanceof LivingEntityRenderer livingEntityRenderer) {
                if(livingEntityRenderer.getModel() instanceof HumanoidModel<?>) {

                    if(TravelersBackpack.endermanOverhaulLoaded && type == EntityType.ENDERMAN) continue;
                    if(livingEntityRenderer instanceof PlayerRenderer) continue;

                    livingEntityRenderer.addLayer(new BackpackEntityLayer(livingEntityRenderer));
                }
            }
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(model);
        if(renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BackpackLayer(livingRenderer));
        }
    } */

    public static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlockEntityTypes.BACKPACK.get(), BackpackBlockEntityRenderer::new);
    }

    public static void registerItemModelProperties() {
        ItemProperties.register(ModItems.HOSE.get(), ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "mode"), (stack, world, entity, propertyFunction) -> {
            if(stack.has(ModDataComponents.HOSE_MODES.get())) {
                int mode = stack.get(ModDataComponents.HOSE_MODES.get()).get(0);
                return (float)mode / 10.0F;
            }
            return 0.0F;
        });
    }
}