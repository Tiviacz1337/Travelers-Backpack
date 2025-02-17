package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.renderer.*;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.PotionFluidType;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler {
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping("key.travelersbackpack.toggle_tank", GLFW.GLFW_KEY_N, CATEGORY);
    public static final ResourceLocation STAR_MODEL = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "item/supporter_star");

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
    public static void registerSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackSpecialRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderer(RegisterSelectItemModelPropertyEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_modes"), HoseSpecialRenderer.TYPE);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(STAR_MODEL);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderer(RegisterSpecialBlockModelRendererEvent event) {
        ModBlocks.BLOCKS.getEntries().stream().filter(holder -> holder.get() instanceof TravelersBackpackBlock).forEach(holder ->
                event.register(holder.get(), new BackpackSpecialRenderer.Unbaked(holder.get().asItem())));
    }

    @SubscribeEvent
    public static void registerClientExtenstions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final int EMPTY_COLOR = 0xf800f8;

            @Override
            public int getTintColor() {
                return EMPTY_COLOR | 0xFF000000;
            }

            @Override
            public int getTintColor(FluidStack stack) {
                return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).getColor();
            }

            @Override
            public ResourceLocation getStillTexture() {
                return PotionFluidType.POTION_STILL_RL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return PotionFluidType.POTION_FLOW_RL;
            }
        }, ModFluids.POTION_FLUID_TYPE);
    }

    @SubscribeEvent
    public static void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);
        event.register(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), BackpackScreen::new);
        event.register(ModMenuTypes.BACKPACK_SETTINGS_MENU.get(), BackpackSettingsScreen::new);
    }

    @SubscribeEvent
    public static void registerOverlay(final RegisterGuiLayersEvent evt) {
        evt.registerBelow(VanillaGuiLayers.HOTBAR, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "overlay"), (pGuiGraphics, pPartialTick) -> {
            Minecraft mc = Minecraft.getInstance();
            if(TravelersBackpackConfig.CLIENT.overlay.enableOverlay.get() && !mc.options.hideGui && AttachmentUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                HudOverlay.renderOverlay(AttachmentUtils.getWearingBackpack(mc.player), mc, pGuiGraphics);
            }
        });
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BackpackTooltipComponent.class, ClientBackpackTooltipComponent::new);
    }

    @SubscribeEvent
    public static void registerRenderStateModifier(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(PlayerRenderer.class, (abstractClientPlayer, playerRenderState) -> {
            ItemStack backpack = AttachmentUtils.getWearingBackpack(abstractClientPlayer);
            if(!backpack.isEmpty()) {
                playerRenderState.setRenderData(BackpackLayer.BACKPACK_KEY, backpack);
            }
        });

        //Zombie
        event.registerEntityModifier(ZombieRenderer.class, (mob, mobRenderState) -> {
            ItemStack chest = mob.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() instanceof TravelersBackpackItem) {
                mobRenderState.chestEquipment = chest;
            }
        });

        //Skeleton
        event.registerEntityModifier(SkeletonRenderer.class, (mob, mobRenderState) -> {
            ItemStack chest = mob.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() instanceof TravelersBackpackItem) {
                mobRenderState.chestEquipment = chest;
            }
        });

        //Enderman
        event.registerEntityModifier(EndermanRenderer.class, (mob, mobRenderState) -> {
            ItemStack chest = mob.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() instanceof TravelersBackpackItem) {
                mobRenderState.chestEquipment = chest;
            }
        });

        //Wither skeleton
        event.registerEntityModifier(WitherSkeletonRenderer.class, (mob, mobRenderState) -> {
            ItemStack chest = mob.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() instanceof TravelersBackpackItem) {
                mobRenderState.chestEquipment = chest;
            }
        });

        //Piglin
        event.registerEntityModifier(PiglinRenderer.class, (mob, mobRenderState) -> {
            ItemStack chest = mob.getItemBySlot(EquipmentSlot.CHEST);
            if(chest.getItem() instanceof TravelersBackpackItem) {
                mobRenderState.chestEquipment = chest;
            }
        });
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

        for(EntityType<?> type : evt.getEntityTypes()) {
            if(evt.getRenderer(type) instanceof HumanoidMobRenderer renderer) {
                if(renderer.getModel() instanceof HumanoidModel) {

                    if(TravelersBackpack.endermanOverhaulLoaded && type == EntityType.ENDERMAN) continue;
                    //if(mobRenderer instanceof PlayerRenderer) continue;

                    renderer.addLayer(new BackpackEntityLayer(renderer));
                }
            }
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, PlayerSkin.Model model) {
        EntityRenderer renderer = evt.getSkin(model);
        if(renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BackpackLayer(livingRenderer));
        }
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlockEntityTypes.BACKPACK.get(), BackpackBlockEntityRenderer::new);
    }
}