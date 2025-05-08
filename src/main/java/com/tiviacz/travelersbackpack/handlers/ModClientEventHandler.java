package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackDynamicModel;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackItemStackRenderer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
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
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler {
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final KeyMapping TOGGLE_TANK = new KeyMapping("key.travelersbackpack.toggle_tank", GLFW.GLFW_KEY_N, CATEGORY);
    public static final ModelResourceLocation STAR_MODEL = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "item/supporter_star"));

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
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(STAR_MODEL);
    }

    @SubscribeEvent
    public static void registerClientExtenstions(RegisterClientExtensionsEvent event) {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> event.registerItem(new IClientItemExtensions() {
                    private final Supplier<BlockEntityWithoutLevelRenderer> renderer = () -> new BackpackItemStackRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return renderer.get();
                    }
                }, holder));

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
    public static void onModelRegistry(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackDynamicModel.Loader.INSTANCE);
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
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, blockDisplayReader, pos, tintIndex) -> {
            if(tintIndex != 0 || pos == null) {
                return -1;
            }
            if(blockDisplayReader.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                if(backpack.getWrapper().getBackpackStack().has(DataComponents.DYED_COLOR)) {
                    return FastColor.ARGB32.opaque(backpack.getWrapper().getBackpackStack().get(DataComponents.DYED_COLOR).rgb());
                }
            }
            return -1;
        }, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if(tintIndex != 0) {
                return -1;
            }
            if(stack.has(DataComponents.DYED_COLOR)) {
                return FastColor.ARGB32.opaque(stack.get(DataComponents.DYED_COLOR).rgb());
            }
            return -1;
        }, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    @SubscribeEvent
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
    }

    public static void registerItemModelProperties() {
        ItemProperties.register(ModItems.HOSE.get(), ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "mode"), (stack, world, entity, propertyFunction) -> {
            if(stack.has(ModDataComponents.HOSE_MODES)) {
                int mode = stack.get(ModDataComponents.HOSE_MODES).get(0);
                return (float)mode / 10.0F;
            }
            return 0.0F;
        });
    }
}