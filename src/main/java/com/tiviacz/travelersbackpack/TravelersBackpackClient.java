package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.client.renderer.RenderData;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
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
        HandledScreens.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, TravelersBackpackHandledScreen::new);
        HandledScreens.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, TravelersBackpackHandledScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, TravelersBackpackBlockEntityRenderer::new);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
        {
            if (entityRenderer instanceof PlayerEntityRenderer renderer) {
                registrationHelper.register(new TravelersBackpackFeature(renderer));
            }
        });
        for(Item item : ModItems.BACKPACKS)
        {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay)
                    -> TravelersBackpackBlockEntityRenderer.renderByItem(new RenderData(MinecraftClient.getInstance().player, stack, stack.hasNbt()), matrices, vertexConsumers, light, overlay));
                    //TravelersBackpackBlockEntityRenderer.render(new TravelersBackpackInventory(stack, MinecraftClient.getInstance().player, (byte)0), null, matrices, vertexConsumers, light, overlay));
        }
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
        });

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.POTION_STILL, ModFluids.POTION_FLOWING, new SimpleFluidRenderHandler(
                new Identifier(TravelersBackpack.MODID, "block/potion_still"),
                new Identifier(TravelersBackpack.MODID, "block/potion_flow"),
                13458603
        ));

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(new Identifier(TravelersBackpack.MODID, "block/milk_still"));
            registry.register(new Identifier(TravelersBackpack.MODID, "block/milk_flow"));
        });

        FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.MILK_STILL, ModFluids.MILK_FLOWING, new SimpleFluidRenderHandler(
                new Identifier(TravelersBackpack.MODID, "block/milk_still"),
                new Identifier(TravelersBackpack.MODID, "block/milk_flow"),
                0xFFFFFFFF
        ));

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.POTION_STILL, ModFluids.POTION_FLOWING);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.MILK_STILL, ModFluids.MILK_FLOWING);
    }

    public static void registerModelPredicate()
    {
        ModelPredicateProviderRegistry.register(ModItems.HOSE, new Identifier(TravelersBackpack.MODID, "mode"), (itemStack, clientWorld, livingEntity, par) ->
        {
            NbtCompound compound = itemStack.getNbt();
            if(compound == null) return 0;
            else return compound.getInt("Mode");
        });
    }
}