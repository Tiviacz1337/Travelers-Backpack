package com.tiviacz.travelersbackpack;

import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackFeature;
import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;
import com.tiviacz.travelersbackpack.handlers.KeybindHandler;
import com.tiviacz.travelersbackpack.init.*;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
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
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY, TravelersBackpackHandledScreen::new);
        ScreenRegistry.register(ModScreenHandlerTypes.TRAVELERS_BACKPACK_ITEM, TravelersBackpackHandledScreen::new);
        BlockEntityRendererRegistry.register(ModBlockEntityTypes.TRAVELERS_BACKPACK_BLOCK_ENTITY_TYPE, TravelersBackpackBlockEntityRenderer::new);
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) ->
        {
            if (entityRenderer instanceof PlayerEntityRenderer) {
                registrationHelper.register(new TravelersBackpackFeature((PlayerEntityRenderer) entityRenderer));
            }
        });
        for(Item item : ModItems.BACKPACKS)
        {
            BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay)
                    -> TravelersBackpackBlockEntityRenderer.render(new TravelersBackpackInventory(stack, MinecraftClient.getInstance().player, (byte)0), null, matrices, vertexConsumers, light, overlay));
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

        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), ModFluids.POTION_STILL, ModFluids.POTION_FLOWING);
    }

    public static void registerModelPredicate()
    {
        FabricModelPredicateProviderRegistry.register(ModItems.HOSE, new Identifier(TravelersBackpack.MODID, "mode"), (itemStack, clientWorld, livingEntity, par) ->
        {
            NbtCompound compound = itemStack.getNbt();
            if(compound == null) return 0;
            else return compound.getInt("Mode");
        });
    }
}
