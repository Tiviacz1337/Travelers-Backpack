package com.tiviacz.travelersbackpack;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.client.model.BackpackDynamicModel;
import com.tiviacz.travelersbackpack.client.model.BackpackItemModel;
import com.tiviacz.travelersbackpack.client.model.StarModelReloadListener;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.client.renderer.HoseSpecialRenderer;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.commands.BackpackIconCommands;
import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.compat.craftingtweaks.CraftingTweaksCompat;
import com.tiviacz.travelersbackpack.compat.polymorph.PolymorphCompat;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantAttributeHandler;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantRenderHandler;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.handlers.ScreenRenderHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TravelersBackpackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //Load render types
        BackpackDynamicModel.Loader.loadVanillaRenderTypes();

        //Handled Screens
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_MENU, BackpackScreen::new);
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_BLOCK_MENU, BackpackScreen::new);
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_SETTINGS_MENU, BackpackSettingsScreen::new);

        //Feature renderers
        registerFeatureRenderers();

        //Builtin Item Renderer
        registerSpecialRenderers();

        //Hud Overlay
        registerHudOverlay();

        //Backpack Tooltip
        registerTooltipComponent();

        //Keybindings
        KeybindHandler.initKeybinds();
        KeybindHandler.registerListener();

        //Client Network
        ModNetwork.initClient();

        //Fluids Rendering
        setupFluidRendering();

        //Backpack Item Entity
        registerBackpackItemEntityRenderer();

        //Client Commands
        registerClientCommands();

        //Polymorph Integration
        if(TravelersBackpack.polymorphLoaded) PolymorphCompat.registerWidget();

        //Backpack Model Deserializer
        UnbakedModelDeserializer.register(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackDynamicModel.Loader.INSTANCE);
        CustomUnbakedBlockStateModel.register(BackpackDynamicModel.UnbakedBlockStateModel.ID, BackpackDynamicModel.UnbakedBlockStateModel.CODEC);
        registerCustomModels();

        //Register Color Providers
        registerBlockColorProvider();
        registerItemColorProvider();

        //Load Supporter Star Model
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(StarModelReloadListener.INSTANCE);

        //Screen Handlers
        ScreenRenderHandler.registerScreenEvents();

        //Synchronize custom recipes
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_SHAPED);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_UPGRADE);

        //Crafting Tweaks Integration
        if(TravelersBackpack.craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAdditionClient();
        if(TravelersBackpack.accessoriesLoaded) TravelersBackpackAccessory.initClient();
        if(TravelersBackpack.trinketsLoaded && !TravelersBackpack.accessoriesLoaded)
            TravelersBackpackTrinket.initClient();
    }

    public static final ResourceLocation STAR_MODEL = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "item/supporter_star");
    public static final ExtraModelKey<BlockStateModel> STAR_MODEL_KEY = ExtraModelKey.create(STAR_MODEL::toString);

    public static final ResourceLocation BACKPACK_BASE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_base");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_BASE_KEY = ExtraModelKey.create(BACKPACK_BASE::toString);
    public static final ResourceLocation BACKPACK_BASE_DYED = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_base_dyed");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_BASE_DYED_KEY = ExtraModelKey.create(BACKPACK_BASE_DYED::toString);
    public static final ResourceLocation BACKPACK_EXTRAS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_extras");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_EXTRAS_KEY = ExtraModelKey.create(BACKPACK_EXTRAS::toString);
    public static final ResourceLocation BACKPACK_FOX_NOSE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_fox_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_FOX_NOSE_KEY = ExtraModelKey.create(BACKPACK_FOX_NOSE::toString);
    public static final ResourceLocation BACKPACK_OCELOT_NOSE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_ocelot_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_OCELOT_NOSE_KEY = ExtraModelKey.create(BACKPACK_OCELOT_NOSE::toString);
    public static final ResourceLocation BACKPACK_PIG_NOSE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_pig_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_PIG_NOSE_KEY = ExtraModelKey.create(BACKPACK_PIG_NOSE::toString);
    public static final ResourceLocation BACKPACK_SLEEPING_BAG = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_sleeping_bag");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_SLEEPING_BAG_KEY = ExtraModelKey.create(BACKPACK_SLEEPING_BAG::toString);
    public static final ResourceLocation BACKPACK_SLEEPING_BAG_EXTRAS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_sleeping_bag_extras");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_SLEEPING_BAG_EXTRAS_KEY = ExtraModelKey.create(BACKPACK_SLEEPING_BAG_EXTRAS::toString);
    public static final ResourceLocation BACKPACK_TANKS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_tanks");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_TANKS_KEY = ExtraModelKey.create(BACKPACK_TANKS::toString);
    public static final ResourceLocation BACKPACK_VILLAGER_NOSE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_villager_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_VILLAGER_NOSE_KEY = ExtraModelKey.create(BACKPACK_VILLAGER_NOSE::toString);
    public static final ResourceLocation BACKPACK_WARDEN_HORNS = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_warden_horns");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_WARDEN_HORNS_KEY = ExtraModelKey.create(BACKPACK_WARDEN_HORNS::toString);
    public static final ResourceLocation BACKPACK_WOLF_NOSE = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_wolf_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_WOLF_NOSE_KEY = ExtraModelKey.create(BACKPACK_WOLF_NOSE::toString);

    public static void registerCustomModels() {
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModel(STAR_MODEL_KEY, SimpleUnbakedExtraModel.blockStateModel(STAR_MODEL));

            pluginContext.addModel(BACKPACK_BASE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_BASE));
            pluginContext.addModel(BACKPACK_BASE_DYED_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_BASE_DYED));
            pluginContext.addModel(BACKPACK_EXTRAS_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_EXTRAS));
            pluginContext.addModel(BACKPACK_FOX_NOSE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_FOX_NOSE));
            pluginContext.addModel(BACKPACK_OCELOT_NOSE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_OCELOT_NOSE));
            pluginContext.addModel(BACKPACK_PIG_NOSE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_PIG_NOSE));
            pluginContext.addModel(BACKPACK_SLEEPING_BAG_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_SLEEPING_BAG));
            pluginContext.addModel(BACKPACK_SLEEPING_BAG_EXTRAS_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_SLEEPING_BAG_EXTRAS));
            pluginContext.addModel(BACKPACK_TANKS_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_TANKS));
            pluginContext.addModel(BACKPACK_VILLAGER_NOSE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_VILLAGER_NOSE));
            pluginContext.addModel(BACKPACK_WARDEN_HORNS_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_WARDEN_HORNS));
            pluginContext.addModel(BACKPACK_WOLF_NOSE_KEY, SimpleUnbakedExtraModel.blockStateModel(BACKPACK_WOLF_NOSE));
        });

        ItemModels.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackItemModel.Unbaked.MAP_CODEC);
    }

    public static void registerBlockColorProvider() {
        ColorProviderRegistry.BLOCK.register((state, level, pos, tintIndex) -> {
            if(tintIndex != 2 || pos == null) {
                return -1;
            }
            if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                if(backpack.getWrapper().getBackpackStack().has(DataComponents.DYED_COLOR)) {
                    return ARGB.opaque(backpack.getWrapper().getBackpackStack().get(DataComponents.DYED_COLOR).rgb());
                }
            }
            return -1;
        }, ModBlocks.STANDARD_TRAVELERS_BACKPACK);
    }

    public static void registerItemColorProvider() {
        ItemTintSources.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_dye"), BackpackTintSource.MAP_CODEC);
        ItemTintSources.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "left_fluid"), LeftFluidTintSource.MAP_CODEC);
        ItemTintSources.ID_MAPPER.put(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "right_fluid"), RightFluidTintSource.MAP_CODEC);
    }

    public static void registerBackpackItemEntityRenderer() {
        EntityRendererRegistry.register(ModItems.BACKPACK_ITEM_ENTITY, ItemEntityRenderer::new);
    }

    public static void registerSpecialRenderers() {
        HoseSpecialRenderer.register();
    }

    public static void registerFeatureRenderers() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
        {
            if(entityRenderer instanceof AvatarRenderer renderer) {
                registrationHelper.register(new BackpackLayer(renderer));
            }
            if(entityRenderer.getModel() instanceof HumanoidModel && entityRenderer instanceof LivingEntityRenderer livingEntityRenderer) {
                if(entityRenderer instanceof AvatarRenderer) return;
                registrationHelper.register(new BackpackEntityLayer(livingEntityRenderer));
            }
        });
    }

    public static void registerHudOverlay() {
        HudRenderCallback.EVENT.register(HudOverlay::renderOverlay);
    }

    public static void setupFluidRendering() {
        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.POTION_STILL, ModFluids.POTION_FLOWING, new SimpleFluidRenderHandler(
                ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_still"),
                ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_flow"),
                13458603
        ));

        FluidVariantAttributes.register(ModFluids.POTION_STILL, new PotionFluidVariantAttributeHandler());
        FluidVariantAttributes.register(ModFluids.POTION_FLOWING, new PotionFluidVariantAttributeHandler());
        FluidVariantRendering.register(ModFluids.POTION_STILL, new PotionFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.POTION_FLOWING, new PotionFluidVariantRenderHandler());
    }

    public static void registerTooltipComponent() {
        TooltipComponentCallback.EVENT.register((data ->
        {
            if(data instanceof BackpackTooltipComponent) {
                return new ClientBackpackTooltipComponent((BackpackTooltipComponent)data);
            }
            return null;
        }));
    }

    public static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register(BackpackIconCommands::new);
    }

    public record BackpackTintSource(int defaultColor) implements ItemTintSource {
        public static final MapCodec<BackpackTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(BackpackTintSource::defaultColor)
        ).apply(instance, BackpackTintSource::new));

        @Override
        public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
            DyedItemColor color = itemStack.get(DataComponents.DYED_COLOR);
            if(itemStack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK && color != null) {
                return ARGB.opaque(color.rgb());
            }
            return defaultColor;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }

    public record LeftFluidTintSource(int defaultColor) implements ItemTintSource {
        public static final MapCodec<LeftFluidTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(LeftFluidTintSource::defaultColor)
        ).apply(instance, LeftFluidTintSource::new));

        @Override
        public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
            if(itemStack.getItem() instanceof TravelersBackpackItem) {
                RenderInfo info = itemStack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                return FluidVariantRendering.getColor(info.getLeftFluidStack().fluidVariant()) | -16777216;
            }
            return defaultColor;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }

    public record RightFluidTintSource(int defaultColor) implements ItemTintSource {
        public static final MapCodec<RightFluidTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(RightFluidTintSource::defaultColor)
        ).apply(instance, RightFluidTintSource::new));

        @Override
        public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
            if(itemStack.getItem() instanceof TravelersBackpackItem) {
                RenderInfo info = itemStack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                return FluidVariantRendering.getColor(info.getRightFluidStack().fluidVariant()) | -16777216;
            }
            return defaultColor;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
    }
}