package com.tiviacz.travelersbackpack.handlers;

import com.google.common.reflect.TypeToken;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackDynamicModel;
import com.tiviacz.travelersbackpack.client.model.BackpackItemModel;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.client.renderer.HoseSpecialRenderer;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetBase;
import com.tiviacz.travelersbackpack.client.screens.widgets.UpgradeWidgetRegistry;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.PotionFluidType;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.init.ModMenuTypes;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.feeding.FeedingWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.jukebox.JukeboxUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.jukebox.JukeboxWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.magnet.MagnetUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.magnet.MagnetWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.refill.RefillUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.AbstractSmeltingWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.BlastFurnaceUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.FurnaceUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.smelting.SmokerUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TankWidget;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidWidget;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.lwjgl.glfw.GLFW;

import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT)
public class ModClientEventHandler {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "controls"));
    public static final KeyMapping OPEN_BACKPACK = new KeyMapping("key.travelersbackpack.inventory", GLFW.GLFW_KEY_B, CATEGORY);
    public static final KeyMapping SORT_BACKPACK = new KeyMapping("key.travelersbackpack.sort", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping ABILITY = new KeyMapping("key.travelersbackpack.ability", GLFW.GLFW_KEY_UNKNOWN, CATEGORY);
    public static final KeyMapping SWAP_TOOL = new KeyMapping("key.travelersbackpack.cycle_tool", GLFW.GLFW_KEY_Z, CATEGORY);
    public static final Identifier STAR_MODEL_LOCATION = Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "item/supporter_star");
    public static final StandaloneModelKey<BlockModelPart> STAR_MODEL = new StandaloneModelKey<>(STAR_MODEL_LOCATION::toString);

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
    public static void onLoaderRegistry(ModelEvent.RegisterLoaders event) {
        event.register(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackDynamicModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerItemModel(RegisterItemModelsEvent event) {
        event.register(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack"), BackpackItemModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerBlockStateModel(RegisterBlockStateModels event) {
        event.registerModel(BackpackDynamicModel.UnbakedBlockStateModel.ID, BackpackDynamicModel.UnbakedBlockStateModel.CODEC);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderer(RegisterSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "hose_modes"), HoseSpecialRenderer.TYPE);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterStandalone event) {
        event.register(STAR_MODEL, SimpleUnbakedStandaloneModel.simpleModelWrapper(STAR_MODEL_LOCATION));
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((state, blockDisplayReader, pos, tintIndex) -> {
            if(tintIndex != 0 || pos == null) {
                return -1;
            }
            if(blockDisplayReader.getBlockEntity(pos) instanceof BackpackBlockEntity backpack) {
                if(backpack.getWrapper().getBackpackStack().has(DataComponents.DYED_COLOR)) {
                    return ARGB.opaque(backpack.getWrapper().getBackpackStack().get(DataComponents.DYED_COLOR).rgb());
                }
            }
            return -1;
        }, ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "backpack_dye"), BackpackTintSource.MAP_CODEC);
    }

    public record BackpackTintSource(int defaultColor) implements ItemTintSource {
        public static final MapCodec<BackpackTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("default").forGetter(BackpackTintSource::defaultColor)
        ).apply(instance, BackpackTintSource::new));

        @Override
        public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
            DyedItemColor color = itemStack.get(DataComponents.DYED_COLOR);
            if(itemStack.getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK.get() && color != null) {
                return ARGB.opaque(color.rgb());
            }
            return defaultColor;
        }

        @Override
        public MapCodec<? extends ItemTintSource> type() {
            return MAP_CODEC;
        }
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
            public Identifier getStillTexture() {
                return PotionFluidType.POTION_STILL_RL;
            }

            @Override
            public Identifier getFlowingTexture() {
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
        evt.registerBelow(VanillaGuiLayers.HOTBAR, Identifier.fromNamespaceAndPath(TravelersBackpack.MODID, "overlay"), (pGuiGraphics, pPartialTick) -> {
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
        event.registerEntityModifier(
                new TypeToken<AvatarRenderer<?>>() {
                },
                (entity, state) -> {
                    if(entity instanceof Player player && state instanceof AvatarRenderState avatarState) {
                        ItemStack backpack = AttachmentUtils.getWearingBackpack(player);
                        if(!backpack.isEmpty()) {
                            avatarState.setRenderData(BackpackLayer.BACKPACK_KEY, backpack);
                        }
                        avatarState.setRenderData(BackpackLayer.NAME_KEY, player.getGameProfile().name());
                    }
                }
        );

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

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt) {
        addPlayerLayer(evt, PlayerModelType.WIDE);
        addPlayerLayer(evt, PlayerModelType.SLIM);

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

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers event, PlayerModelType skinModel) {
        EntityRenderer renderer = event.getPlayerRenderer(skinModel);
        if(renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new BackpackLayer(livingRenderer));
        }
    }

    public static void registerUpgradeWidgets() {
        UpgradeWidgetRegistry.register(CraftingUpgrade.class, (screen, upgrade, x, y) ->
                new CraftingWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(FeedingUpgrade.class, (screen, upgrade, x, y) ->
                new FeedingWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(JukeboxUpgrade.class, (screen, upgrade, x, y) ->
                new JukeboxWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(MagnetUpgrade.class, (screen, upgrade, x, y) ->
                new MagnetWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(AutoPickupUpgrade.class, (screen, upgrade, x, y) ->
                new AutoPickupWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(BlastFurnaceUpgrade.class, (screen, upgrade, x, y) ->
                new AbstractSmeltingWidget<>(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), "screen.travelersbackpack." + upgrade.getUpgradeName())
        );
        UpgradeWidgetRegistry.register(SmokerUpgrade.class, (screen, upgrade, x, y) ->
                new AbstractSmeltingWidget<>(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), "screen.travelersbackpack." + upgrade.getUpgradeName())
        );
        UpgradeWidgetRegistry.register(FurnaceUpgrade.class, (screen, upgrade, x, y) ->
                new AbstractSmeltingWidget<>(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), "screen.travelersbackpack." + upgrade.getUpgradeName())
        );
        UpgradeWidgetRegistry.register(TanksUpgrade.class, (screen, upgrade, x, y) ->
                new TankWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(VoidUpgrade.class, (screen, upgrade, x, y) ->
                new VoidWidget(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y))
        );
        UpgradeWidgetRegistry.register(RefillUpgrade.class, (screen, upgrade, x, y) ->
                new UpgradeWidgetBase<>(screen, upgrade, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y), new Point(137, 0), "screen.travelersbackpack.refill_upgrade")
        );
    }
}