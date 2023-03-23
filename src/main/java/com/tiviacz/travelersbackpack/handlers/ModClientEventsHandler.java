package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackEntityLayer;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackLayer;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TravelersBackpack.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClientEventsHandler
{
    public static final ModelLayerLocation TRAVELERS_BACKPACK_BLOCK_ENTITY = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "main");
    public static final ModelLayerLocation TRAVELERS_BACKPACK_WEARABLE = new ModelLayerLocation(new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"), "wearable");

    @SubscribeEvent
    public static void layerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(TRAVELERS_BACKPACK_BLOCK_ENTITY, () -> TravelersBackpackBlockEntityRenderer.createTravelersBackpack(false));
        event.registerLayerDefinition(TRAVELERS_BACKPACK_WEARABLE, () -> TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers evt)
    {
        addPlayerLayer(evt, "default");
        addPlayerLayer(evt, "slim");

        for(EntityType type : Reference.COMPATIBLE_TYPE_ENTRIES)
        {
            addEntityLayer(evt, type);
        }
    }

    private static void addPlayerLayer(EntityRenderersEvent.AddLayers evt, String skin)
    {
        EntityRenderer<? extends Player> renderer = evt.getSkin(skin);

        if (renderer instanceof LivingEntityRenderer livingRenderer) {
            livingRenderer.addLayer(new TravelersBackpackLayer(livingRenderer));
        }
    }

    private static void addEntityLayer(EntityRenderersEvent.AddLayers evt, EntityType entityType)
    {
        EntityRenderer<? extends LivingEntity> renderer = evt.getRenderer(entityType);

        if(renderer instanceof LivingEntityRenderer livingRenderer)
        {
            livingRenderer.addLayer(new TravelersBackpackEntityLayer(livingRenderer));
        }
    }
}