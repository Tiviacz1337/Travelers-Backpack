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
import com.tiviacz.travelersbackpack.compat.craftingtweaks.CraftingTweaksCompat;
import com.tiviacz.travelersbackpack.compat.trashslot.TrashSlotCompat;
import com.tiviacz.travelersbackpack.compat.trinkets.BackpackTrinketRenderer;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantAttributeHandler;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantRenderHandler;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.handlers.ScreenRenderHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityRenderLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TravelersBackpackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //Register client config
        ConfigRegistry.INSTANCE.register(TravelersBackpack.MODID, ModConfig.Type.CLIENT, TravelersBackpackConfig.clientSpec);
        ConfigScreenFactoryRegistry.INSTANCE.register(TravelersBackpack.MODID, ConfigurationScreen::new);

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
        //if(TravelersBackpack.polymorphLoaded) PolymorphCompat.registerWidget();
        if(TravelersBackpack.trashSlotLoaded) TrashSlotCompat.register();

        //Backpack Model Deserializer
        UnbakedModelDeserializer.register(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackDynamicModel.Loader.INSTANCE);
        CustomUnbakedBlockStateModel.register(BackpackDynamicModel.UnbakedBlockStateModel.ID, BackpackDynamicModel.UnbakedBlockStateModel.CODEC);
        registerCustomModels();

        //Register Color Providers
        registerBlockColorProvider();
        registerItemColorProvider();

        //Load Supporter Star Model
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(StarModelReloadListener.INSTANCE);

        //Screen Handlers
        ScreenRenderHandler.registerScreenEvents();

        //Crafting Tweaks Integration
        if(TravelersBackpack.craftingTweaksLoaded) CraftingTweaksCompat.registerCraftingTweaksAdditionClient();
        //if(TravelersBackpack.accessoriesLoaded) TravelersBackpackAccessory.initClient();
        if(TravelersBackpack.trinketsLoaded /*&& !TravelersBackpack.accessoriesLoaded*/)
            BackpackTrinketRenderer.initClient();

        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_SHAPED);
        RecipeSynchronization.synchronizeRecipeSerializer(ModRecipeSerializers.BACKPACK_UPGRADE);
    }

    public static final Identifier STAR_MODEL = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/supporter_star");
    public static final ExtraModelKey<BlockStateModel> STAR_MODEL_KEY = ExtraModelKey.create(STAR_MODEL::toString);

    public static final Identifier BACKPACK_BASE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_base");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_BASE_KEY = ExtraModelKey.create(BACKPACK_BASE::toString);
    public static final Identifier BACKPACK_BASE_DYED = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_base_dyed");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_BASE_DYED_KEY = ExtraModelKey.create(BACKPACK_BASE_DYED::toString);
    public static final Identifier BACKPACK_EXTRAS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_extras");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_EXTRAS_KEY = ExtraModelKey.create(BACKPACK_EXTRAS::toString);
    public static final Identifier BACKPACK_FOX_NOSE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_fox_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_FOX_NOSE_KEY = ExtraModelKey.create(BACKPACK_FOX_NOSE::toString);
    public static final Identifier BACKPACK_OCELOT_NOSE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_ocelot_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_OCELOT_NOSE_KEY = ExtraModelKey.create(BACKPACK_OCELOT_NOSE::toString);
    public static final Identifier BACKPACK_PIG_NOSE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_pig_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_PIG_NOSE_KEY = ExtraModelKey.create(BACKPACK_PIG_NOSE::toString);
    public static final Identifier BACKPACK_SLEEPING_BAG = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_sleeping_bag");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_SLEEPING_BAG_KEY = ExtraModelKey.create(BACKPACK_SLEEPING_BAG::toString);
    public static final Identifier BACKPACK_SLEEPING_BAG_EXTRAS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_sleeping_bag_extras");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_SLEEPING_BAG_EXTRAS_KEY = ExtraModelKey.create(BACKPACK_SLEEPING_BAG_EXTRAS::toString);
    public static final Identifier BACKPACK_TANKS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_tanks");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_TANKS_KEY = ExtraModelKey.create(BACKPACK_TANKS::toString);
    public static final Identifier BACKPACK_VILLAGER_NOSE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_villager_nose");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_VILLAGER_NOSE_KEY = ExtraModelKey.create(BACKPACK_VILLAGER_NOSE::toString);
    public static final Identifier BACKPACK_WARDEN_HORNS = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_warden_horns");
    public static final ExtraModelKey<BlockStateModel> BACKPACK_WARDEN_HORNS_KEY = ExtraModelKey.create(BACKPACK_WARDEN_HORNS::toString);
    public static final Identifier BACKPACK_WOLF_NOSE = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/backpack_wolf_nose");
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

        ItemModels.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackItemModel.Unbaked.MAP_CODEC);
    }

    public static void registerBlockColorProvider() {
        BlockColorRegistry.register(List.of(new BackpackTintSources.LeftFluidBlockTintSource(), new BackpackTintSources.RightFluidBlockTintSource(), new BackpackTintSources.BackpackBlockTintSource()), ModBlocks.STANDARD_TRAVELERS_BACKPACK);
        BlockColorRegistry.register(List.of(new BackpackTintSources.LeftFluidBlockTintSource(), new BackpackTintSources.RightFluidBlockTintSource()), ModBlocks.NETHERITE_TRAVELERS_BACKPACK,
                ModBlocks.DIAMOND_TRAVELERS_BACKPACK,
                ModBlocks.GOLD_TRAVELERS_BACKPACK,
                ModBlocks.EMERALD_TRAVELERS_BACKPACK,
                ModBlocks.IRON_TRAVELERS_BACKPACK,
                ModBlocks.LAPIS_TRAVELERS_BACKPACK,
                ModBlocks.REDSTONE_TRAVELERS_BACKPACK,
                ModBlocks.COAL_TRAVELERS_BACKPACK,

                ModBlocks.QUARTZ_TRAVELERS_BACKPACK,
                ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK,
                ModBlocks.END_TRAVELERS_BACKPACK,
                ModBlocks.NETHER_TRAVELERS_BACKPACK,
                ModBlocks.SANDSTONE_TRAVELERS_BACKPACK,
                ModBlocks.SNOW_TRAVELERS_BACKPACK,
                ModBlocks.SPONGE_TRAVELERS_BACKPACK,

                ModBlocks.CAKE_TRAVELERS_BACKPACK,

                ModBlocks.CACTUS_TRAVELERS_BACKPACK,
                ModBlocks.HAY_TRAVELERS_BACKPACK,
                ModBlocks.MELON_TRAVELERS_BACKPACK,
                ModBlocks.PUMPKIN_TRAVELERS_BACKPACK,

                ModBlocks.CREEPER_TRAVELERS_BACKPACK,
                ModBlocks.DRAGON_TRAVELERS_BACKPACK,
                ModBlocks.ENDERMAN_TRAVELERS_BACKPACK,
                ModBlocks.BLAZE_TRAVELERS_BACKPACK,
                ModBlocks.GHAST_TRAVELERS_BACKPACK,
                ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK,
                ModBlocks.SKELETON_TRAVELERS_BACKPACK,
                ModBlocks.SPIDER_TRAVELERS_BACKPACK,
                ModBlocks.WITHER_TRAVELERS_BACKPACK,
                ModBlocks.WARDEN_TRAVELERS_BACKPACK,

                ModBlocks.BAT_TRAVELERS_BACKPACK,
                ModBlocks.BEE_TRAVELERS_BACKPACK,
                ModBlocks.WOLF_TRAVELERS_BACKPACK,
                ModBlocks.FOX_TRAVELERS_BACKPACK,
                ModBlocks.OCELOT_TRAVELERS_BACKPACK,
                ModBlocks.HORSE_TRAVELERS_BACKPACK,
                ModBlocks.COW_TRAVELERS_BACKPACK,
                ModBlocks.PIG_TRAVELERS_BACKPACK,
                ModBlocks.SHEEP_TRAVELERS_BACKPACK,
                ModBlocks.CHICKEN_TRAVELERS_BACKPACK,
                ModBlocks.SQUID_TRAVELERS_BACKPACK,
                ModBlocks.VILLAGER_TRAVELERS_BACKPACK,
                ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK);
    }

    public static void registerItemColorProvider() {
        ItemTintSources.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_dye"), BackpackTintSources.BackpackItemTintSource.MAP_CODEC);
        ItemTintSources.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "left_fluid"), BackpackTintSources.LeftFluidItemTintSource.MAP_CODEC);
        ItemTintSources.ID_MAPPER.put(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "right_fluid"), BackpackTintSources.RightFluidItemTintSource.MAP_CODEC);
    }

    public static void registerBackpackItemEntityRenderer() {
        EntityRendererRegistry.register(ModItems.BACKPACK_ITEM_ENTITY, ItemEntityRenderer::new);
    }

    public static void registerSpecialRenderers() {
        HoseSpecialRenderer.register();
    }

    public static void registerFeatureRenderers() {
        LivingEntityRenderLayerRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
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
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "overlay"), HudOverlay::renderOverlay);
    }

    private static final FluidModel.Unbaked POTION_MODEL = new FluidModel.Unbaked(new Material(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_still")), new Material(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "block/potion_flow")), null, null);

    public static void setupFluidRendering() {
        FluidRenderingRegistry.register(ModFluids.POTION_STILL, ModFluids.POTION_FLOWING, POTION_MODEL);

        FluidVariantAttributes.register(ModFluids.POTION_STILL, new PotionFluidVariantAttributeHandler());
        FluidVariantAttributes.register(ModFluids.POTION_FLOWING, new PotionFluidVariantAttributeHandler());
        FluidVariantRendering.register(ModFluids.POTION_STILL, new PotionFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.POTION_FLOWING, new PotionFluidVariantRenderHandler());
    }

    public static void registerTooltipComponent() {
        ClientTooltipComponentCallback.EVENT.register((data ->
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

    private static class BackpackTintSources {
        private static class BackpackBlockTintSource implements BlockTintSource {
            @Override
            public int color(BlockState state) {
                return -1;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                    if(backpack.getWrapper().getBackpackStack().has(DataComponents.DYED_COLOR)) {
                        return ARGB.opaque(backpack.getWrapper().getBackpackStack().get(DataComponents.DYED_COLOR).rgb());
                    }
                }
                return -1;
            }
        }

        private static class LeftFluidBlockTintSource implements BlockTintSource {
            @Override
            public int color(BlockState state) {
                if(state.getFluidState().is(Fluids.WATER)) {
                    return 0xFF3F76E4;
                }
                return -1;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                    RenderInfo info = backpack.getWrapper().getRenderInfo();
                    return FluidVariantRendering.getColor(info.getLeftFluidStack().fluidVariant(), level, pos) | -16777216;
                }
                return -1;
            }
        }

        private static class RightFluidBlockTintSource implements BlockTintSource {
            @Override
            public int color(BlockState state) {
                if(state.getFluidState().is(Fluids.WATER)) {
                    return 0xFF3F76E4;
                }
                return -1;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if(level.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                    RenderInfo info = backpack.getWrapper().getRenderInfo();
                    return FluidVariantRendering.getColor(info.getRightFluidStack().fluidVariant(), level, pos) | -16777216;
                }
                return -1;
            }
        }

        public record BackpackItemTintSource(int defaultColor) implements ItemTintSource {
            public static final MapCodec<BackpackItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(BackpackItemTintSource::defaultColor)
            ).apply(instance, BackpackItemTintSource::new));

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

        public record LeftFluidItemTintSource(int defaultColor) implements ItemTintSource {
            public static final MapCodec<LeftFluidItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(LeftFluidItemTintSource::defaultColor)
            ).apply(instance, LeftFluidItemTintSource::new));

            @Override
            public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
                if(itemStack.getItem() instanceof TravelersBackpackItem) {
                    RenderInfo info = itemStack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                    FluidVariant variant = info.getLeftFluidStack().fluidVariant();
                    if(variant.getFluid().isSame(Fluids.WATER)) {
                        return 0xFF3F76E4;
                    }
                    return FluidVariantRendering.getColor(variant) | -16777216;
                }
                return defaultColor;
            }

            @Override
            public MapCodec<? extends ItemTintSource> type() {
                return MAP_CODEC;
            }
        }

        public record RightFluidItemTintSource(int defaultColor) implements ItemTintSource {
            public static final MapCodec<RightFluidItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(RightFluidItemTintSource::defaultColor)
            ).apply(instance, RightFluidItemTintSource::new));

            @Override
            public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
                if(itemStack.getItem() instanceof TravelersBackpackItem) {
                    RenderInfo info = itemStack.getOrDefault(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                    FluidVariant variant = info.getRightFluidStack().fluidVariant();
                    if(variant.getFluid().isSame(Fluids.WATER)) {
                        return 0xFF3F76E4;
                    }
                    return FluidVariantRendering.getColor(variant) | -16777216;
                }
                return defaultColor;
            }

            @Override
            public MapCodec<? extends ItemTintSource> type() {
                return MAP_CODEC;
            }
        }
    }
}