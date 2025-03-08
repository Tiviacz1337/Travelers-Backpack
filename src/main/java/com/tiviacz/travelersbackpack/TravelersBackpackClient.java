package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.client.renderer.BackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.BackpackSettingsScreen;
import com.tiviacz.travelersbackpack.client.screens.HudOverlay;
import com.tiviacz.travelersbackpack.client.screens.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screens.tooltip.ClientBackpackTooltipComponent;
import com.tiviacz.travelersbackpack.commands.BackpackIconCommands;
import com.tiviacz.travelersbackpack.compat.accessories.TravelersBackpackAccessory;
import com.tiviacz.travelersbackpack.compat.polymorph.PolymorphCompat;
import com.tiviacz.travelersbackpack.compat.trinkets.TravelersBackpackTrinket;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantAttributeHandler;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantRenderHandler;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class TravelersBackpackClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //Handled Screens
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_MENU, BackpackScreen::new);
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_BLOCK_MENU, BackpackScreen::new);
        MenuScreens.register(ModScreenHandlerTypes.BACKPACK_SETTINGS_MENU, BackpackSettingsScreen::new);

        //BlockEntity renderer
        BlockEntityRenderers.register(ModBlockEntityTypes.BACKPACK, BackpackBlockEntityRenderer::new);

        //Feature renderers
        registerFeatureRenderers();

        //Builtin Item Renderer
        registerBuiltinItemRenderer();

        //Hud Overlay
        registerHudOverlay();

        //Backpack Tooltip
        registerTooltipComponent();

        //Keybindings
        KeybindHandler.initKeybinds();
        KeybindHandler.registerListener();

        //Client Network
        ModNetwork.initClient();

        //Hose Model Predicate
        registerModelPredicate();

        //Fluids Rendering
        setupFluidRendering();

        //Backpack Item Entity
        registerBackpackItemEntityRenderer();

        //Client Commands
        registerClientCommands();

        //Polymorph Integration
        if(TravelersBackpack.polymorphLoaded) PolymorphCompat.registerWidget();

        //Crafting Tweaks Integration
        //if(TravelersBackpack.craftingTweaksLoaded) TravelersBackpackCraftingGridProvider.registerClient();
        if(TravelersBackpack.accessoriesLoaded) TravelersBackpackAccessory.initClient();
        if(TravelersBackpack.trinketsLoaded) TravelersBackpackTrinket.initClient();
    }

    public static final ModelResourceLocation STAR_MODEL = new ModelResourceLocation(ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "item/supporter_star"), "standalone");

    public static void registerBackpackItemEntityRenderer() {
        EntityRendererRegistry.register(ModItems.BACKPACK_ITEM_ENTITY, ItemEntityRenderer::new);
    }

    public static void registerFeatureRenderers() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
        {
            if(entityRenderer instanceof PlayerRenderer renderer) {
                registrationHelper.register(new BackpackLayer(renderer));
            }
            if(entityRenderer.getModel() instanceof HumanoidModel && entityRenderer instanceof LivingEntityRenderer) {
                if(entityRenderer instanceof PlayerRenderer) return;
                registrationHelper.register(new BackpackEntityLayer((LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>>)entityRenderer));
            }
        });
    }

    public static void registerBuiltinItemRenderer() {
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof TravelersBackpackItem).forEach(item -> BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay)
                -> BackpackBlockEntityRenderer.renderByItem(stack, matrices, vertexConsumers, light, overlay)));
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

        //FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.MILK_STILL, ModFluids.MILK_FLOWING, new SimpleFluidRenderHandler(
        //        ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/milk_still"),
        //        ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "block/milk_flow"),
        //        0xFFFFFFFF
        //));

        FluidVariantAttributes.register(ModFluids.POTION_STILL, new PotionFluidVariantAttributeHandler());
        FluidVariantAttributes.register(ModFluids.POTION_FLOWING, new PotionFluidVariantAttributeHandler());
        FluidVariantRendering.register(ModFluids.POTION_STILL, new PotionFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.POTION_FLOWING, new PotionFluidVariantRenderHandler());

        //FluidVariantAttributes.register(ModFluids.MILK_STILL, new MilkFluidVariantAttributeHandler());
        //FluidVariantAttributes.register(ModFluids.MILK_FLOWING, new MilkFluidVariantAttributeHandler());

        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ModFluids.POTION_STILL, ModFluids.POTION_FLOWING);
        //BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ModFluids.MILK_STILL, ModFluids.MILK_FLOWING);
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

    public static void registerModelPredicate() {
        FabricModelPredicateProviderRegistry.register(ModItems.HOSE, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "mode"), (itemStack, clientWorld, livingEntity, par) ->
        {
            if(itemStack.has(ModDataComponents.HOSE_MODES)) {
                int mode = itemStack.get(ModDataComponents.HOSE_MODES).get(0);
                return (float)mode / 10.0F;
            }
            return 0.0F;
        });
    }

    public static void registerClientCommands() {
        ClientCommandRegistrationCallback.EVENT.register(BackpackIconCommands::new);
    }
}