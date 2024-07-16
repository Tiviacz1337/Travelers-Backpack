package com.tiviacz.travelersbackpack.compat.trinkets;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.client.renderer.TravelersBackpackBlockEntityRenderer;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Triple;

public class TravelersBackpackRenderer implements TrinketRenderer
{
    public static void init()
    {
        for(TravelersBackpackItem backpack : ModItems.BACKPACKS)
        {
            TrinketRendererRegistry.registerRenderer(backpack, new TravelersBackpackRenderer());
        }
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        if(entity instanceof PlayerEntity player && contextModel instanceof PlayerEntityModel playerEntityModel)
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
            if(inv == null) return;
            TravelersBackpackWearableModel<AbstractClientPlayerEntity> model = new TravelersBackpackWearableModel<>(player, vertexConsumers, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).createModel());
            boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

            if(inv.getItemStack().isEmpty()) return;

            Identifier id = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

            boolean isColorable = false;
            boolean isCustomSleepingBag = false;

            if(inv.getItemStack().getNbt() != null && inv.getItemStack().getItem() == ModItems.STANDARD_TRAVELERS_BACKPACK)
            {
                if(BackpackDyeRecipe.hasColor(inv.getItemStack()))
                {
                    isColorable = true;
                    id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed.png");
                }
            }

            if(inv.getItemStack().getNbt() != null)
            {
                if(inv.getItemStack().getNbt().contains(ITravelersBackpackInventory.SLEEPING_BAG_COLOR))
                {
                    isCustomSleepingBag = true;
                }
            }

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(flag ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

            matrices.push();

            if(entity.isSneaking())
            {
                matrices.translate(0D, -0.155D, 0.025D);
            }

            playerEntityModel.copyBipedStateTo(model);
            model.setupAngles(playerEntityModel);

            matrices.translate(0, 0.175, 0.325);
            matrices.scale(0.85F, 0.85F, 0.85F);

            if(isColorable)
            {
                Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(inv.getItemStack()));
                model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

                id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
                vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
            }
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

            if(isCustomSleepingBag)
            {
                id = ResourceUtils.getSleepingBagTexture(inv.getSleepingBagColor());
            }
            else
            {
                id = ResourceUtils.getDefaultSleepingBagTexture();
            }

            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
            model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.25F);

            matrices.pop();
        }
    }
}