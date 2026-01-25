package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackDynamicModel;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventHandler {
    public static final String CATEGORY = "key.travelersbackpack.category";
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final ResourceLocation STAR_MODEL = new ResourceLocation(TravelersBackpack.MODID, "item/supporter_star");

    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(OPEN_BACKPACK);
        event.register(SORT_BACKPACK);
        event.register(ABILITY);
        event.register(SWAP_TOOL);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModItems.BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BackpackTooltipComponent.class, ClientBackpackTooltipComponent::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(STAR_MODEL);
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelEvent.RegisterGeometryLoaders event) {
        event.register("backpack", BackpackDynamicModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent evt) {
        evt.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "travelers_backpack", (gui, poseStack, partialTick, width, height) -> {
            Minecraft mc = Minecraft.getInstance();
            if(TravelersBackpackConfig.CLIENT.overlay.enableOverlay.get() && !mc.options.hideGui && CapabilityUtils.isWearingBackpack(mc.player) && mc.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                HudOverlay.renderOverlay(CapabilityUtils.getWearingBackpack(mc.player), mc, poseStack);
            }
        });
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt) {
        addPlayerLayer(evt, "default");
        addPlayerLayer(evt, "slim");

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

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, blockDisplayReader, pos, tintIndex) -> {
            if(tintIndex != 0 || pos == null) {
                return -1;
            }
            if(blockDisplayReader.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                if(NbtHelper.has(backpack.getWrapper().getBackpackStack(), ModDataHelper.COLOR)) {
                    return BackpackDyeRecipe.getColor(backpack.getWrapper().getBackpackStack());
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
            if(NbtHelper.has(stack, ModDataHelper.COLOR)) {
                return BackpackDyeRecipe.getColor(stack);
            }
            return -1;
        }, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String model) {
        EntityRenderer<? extends Player> renderer = evt.getSkin(model);

        if(renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BackpackLayer(livingRenderer));
        }
    }

    public static void registerScreenFactories() {
        MenuScreens.register(ModMenuTypes.BACKPACK_BLOCK_MENU.get(), BackpackScreen::new);
        MenuScreens.register(ModMenuTypes.BACKPACK_MENU.get(), BackpackScreen::new);
        MenuScreens.register(ModMenuTypes.BACKPACK_SETTINGS_MENU.get(), BackpackSettingsScreen::new);
    }

    public static void registerItemModelProperties() {
        ItemProperties.register(ModItems.HOSE.get(), new ResourceLocation(TravelersBackpack.MODID, "mode"), (stack, world, entity, propertyFunction) -> {
            if(NbtHelper.has(stack, ModDataHelper.HOSE_MODES)) {
                List<Integer> modes = NbtHelper.get(stack, ModDataHelper.HOSE_MODES);
                if(!modes.isEmpty()) {
                    return (float)modes.get(0) / 10.0F;
                }
            }
            return 0.0F;
        });
    }
}