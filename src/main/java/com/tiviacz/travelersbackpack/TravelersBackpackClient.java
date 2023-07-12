package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityFeature;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.client.screen.tooltip.BackpackTooltipComponent;
import com.tiviacz.travelersbackpack.client.screen.tooltip.BackpackTooltipData;
import com.tiviacz.travelersbackpack.fluids.milk.MilkFluidVariantRenderHandler;
import com.tiviacz.travelersbackpack.fluids.potion.PotionFluidVariantRenderHandler;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class TravelersBackpackClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, TravelersBackpackHandledScreen::new);
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, TravelersBackpackHandledScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, TravelersBackpackBlockEntityRenderer::new);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
        {
            if(entityRenderer instanceof PlayerEntityRenderer renderer) {
                registrationHelper.register(new TravelersBackpackFeature(renderer));
            }

            if(Reference.COMPATIBLE_TYPE_ENTRIES.contains(entityType))
            {
                registrationHelper.register(new TravelersBackpackEntityFeature((LivingEntityRenderer<LivingEntity, BipedEntityModel<LivingEntity>>)entityRenderer));
            }
        });
        for(Item item : ModItems.BACKPACKS)
        {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay)
                    -> TravelersBackpackBlockEntityRenderer.renderByItem(new RenderData(MinecraftClient.getInstance().player, stack, stack.hasNbt()), matrices, vertexConsumers, light, overlay));
        }
        registerTooltipComponent();
        KeybindHandler.initKeybinds();
        KeybindHandler.registerListeners();
        ModNetwork.initClient();
        registerModelPredicate();
        setupFluidRendering();
    }

    public static void setupFluidRendering()
    {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier(TravelersBackpack.MODID, "block/potion_still"));
            registry.register(new Identifier(TravelersBackpack.MODID, "block/potion_flow"));
            registry.register(new Identifier(TravelersBackpack.MODID, "block/milk_still"));
            registry.register(new Identifier(TravelersBackpack.MODID, "block/milk_flow"));
        });

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.POTION_STILL, ModFluids.POTION_FLOWING, new SimpleFluidRenderHandler(
                new Identifier(TravelersBackpack.MODID, "block/potion_still"),
                new Identifier(TravelersBackpack.MODID, "block/potion_flow"),
                13458603
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.MILK_STILL, ModFluids.MILK_FLOWING, new SimpleFluidRenderHandler(
                new Identifier(TravelersBackpack.MODID, "block/milk_still"),
                new Identifier(TravelersBackpack.MODID, "block/milk_flow"),
                0xFFFFFFFF
        ));

        FluidVariantRendering.register(ModFluids.POTION_STILL, new PotionFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.POTION_FLOWING, new PotionFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.MILK_STILL, new MilkFluidVariantRenderHandler());
        FluidVariantRendering.register(ModFluids.MILK_FLOWING, new MilkFluidVariantRenderHandler());

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.POTION_STILL, ModFluids.POTION_FLOWING);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.MILK_STILL, ModFluids.MILK_FLOWING);
    }

    public static void registerModelPredicate()
    {
        FabricModelPredicateProviderRegistry.register(ModItems.HOSE, new Identifier(TravelersBackpack.MODID, "mode"), (itemStack, clientWorld, livingEntity, par) ->
        {
            NbtCompound compound = itemStack.getNbt();
            if(compound == null)
            {
                return 0;
            }
            else
            {
                return compound.getInt("Mode");
            }
        });
    }

    public static void registerTooltipComponent()
    {
        TooltipComponentCallback.EVENT.register((data ->
        {
            if(data instanceof BackpackTooltipData)
            {
                return new BackpackTooltipComponent((BackpackTooltipData)data);
            }
            return null;
        }));
    }
}