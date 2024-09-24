package com.tiviacz.travelersbackpack.client.renderer;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.client.model.BackpackBlockModel;
import com.tiviacz.travelersbackpack.client.model.BackpackFeatureModel;
import com.tiviacz.travelersbackpack.common.recipes.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Triple;

@Environment(value = EnvType.CLIENT)
public class TravelersBackpackFeature extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public TravelersBackpackFeature(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch)
    {
        if (TravelersBackpack.enableTrinkets()) return;

        if (ComponentUtils.isWearingBackpack(entity)) {
            ItemStack stack = ComponentUtils.getWearingBackpack(entity);
            renderBackpackFeature(BackpackFeatureModel.FEATURE_MODEL, getContextModel(), matrices, vertexConsumers, light, entity, stack);
        }
        /*if(TravelersBackpackConfig.getConfig().client.disableBackpackRender || TravelersBackpack.enableTrinkets()) return;

        if(ComponentUtils.isWearingBackpack(entity))
        {
            ITravelersBackpackInventory inv = ComponentUtils.getBackpackInv(entity);

            if(inv != null && !entity.isInvisible())
            {
                if(!TravelersBackpackConfig.getConfig().client.renderBackpackWithElytra && entity.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem) return;

                renderLayer(matrices, vertexConsumers, light, entity, inv);
            }
        } */
    }

    public static void renderBackpackFeature(BackpackFeatureModel model, BipedEntityModel bipedEntityModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, ItemStack stack) {
        if (!(stack.hasNbt() && stack.getNbt().contains(ITravelersBackpackInventory.VISIBILITY) ? stack.getNbt().getBoolean(ITravelersBackpackInventory.VISIBILITY) : true)) return;

        model.setLivingEntity(entity);
        model.setVertexConsumerProvider(vertexConsumers);

        boolean translucentLayer = stack.getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || stack.getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

        if (!(stack.getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        Identifier id = travelersBackpackItem.getBackpackTexture();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(translucentLayer ? RenderLayer.getEntityTranslucentCull(id) : RenderLayer.getEntitySolid(id));

        matrices.push();
        alignModel(matrices, bipedEntityModel, model, entity);

        if (BackpackDyeRecipe.hasColor(stack) && travelersBackpackItem == ModItems.STANDARD_TRAVELERS_BACKPACK) {
            id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(id));
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(stack));
            model.mainBody.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            id = new Identifier(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        }

        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        int sleepingBagColor = stack.hasNbt() && stack.getNbt().contains(ITravelersBackpackInventory.SLEEPING_BAG_COLOR) ? stack.getNbt().getInt(ITravelersBackpackInventory.SLEEPING_BAG_COLOR) : DyeColor.RED.getId();
        id = BackpackBlockModel.getSleepingBagTexture(sleepingBagColor);
        vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(id));
        model.sleepingBag.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);

        matrices.pop();
    }

    public static void alignModel(MatrixStack matrices, BipedEntityModel parent, BackpackFeatureModel backpackModel, LivingEntity entity) {
        if (entity.isInSneakingPose()) {
            matrices.translate(0D, -0.155D, 0.025D);
        }

        parent.copyBipedStateTo(backpackModel);
        backpackModel.setupAngles(parent);

        matrices.translate(0, 0.175, 0.325);
        matrices.scale(0.85F, 0.85F, 0.85F);

        if (entity.isBaby()) {
            matrices.translate(0F, 0.8F, -0.165F);
            float scaleFactor = entity.getScaleFactor();
            matrices.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }


        /*private void renderLayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, ITravelersBackpackInventory inv)
    {
        model = new BackpackFeatureModel<>(entity, vertexConsumers, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).createModel());
        boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK || inv.getItemStack().getItem() == ModItems.SNOW_TRAVELERS_BACKPACK;

        if(inv.getItemStack().isEmpty() || !(inv.getItemStack().getItem() instanceof TravelersBackpackItem travelersBackpackItem)) return;

        Identifier id = travelersBackpackItem.getBackpackTexture();

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

        this.getContextModel().copyBipedStateTo(model);
        model.setupAngles(this.getContextModel());

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
    } */
}