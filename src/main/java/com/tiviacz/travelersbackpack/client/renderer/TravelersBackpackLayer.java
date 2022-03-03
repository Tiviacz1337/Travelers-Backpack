package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    public TravelersBackpackWearableModel model;

    public TravelersBackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer)
    {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if(CapabilityUtils.isWearingBackpack(clientPlayer))
        {
            ITravelersBackpackContainer inv = CapabilityUtils.getBackpackInv(clientPlayer);

            if(inv != null && !clientPlayer.isInvisible())
            {
                if(TravelersBackpackConfig.curiosIntegration)
                {
                    if(TravelersBackpackCurios.getCurioTravelersBackpack(clientPlayer).isPresent())
                    {
                        ICuriosItemHandler curios = CuriosApi.getCuriosHelper().getCuriosHandler(clientPlayer).resolve().get();
                        IDynamicStackHandler stackHandler = curios.getStacksHandler("back").get().getStacks();

                        for(int i = 0; i < stackHandler.getSlots(); i++)
                        {
                            if(stackHandler.getStackInSlot(i).getItem() instanceof TravelersBackpackItem)
                            {
                                if(curios.getCurios().get("back").getRenders().get(i))
                                {
                                    renderLayer(poseStack, bufferIn, packedLightIn, clientPlayer, inv, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                                }
                                else
                                {
                                    return;
                                }
                            }
                        }
                    }
                }

                ItemStack stack = clientPlayer.getItemBySlot(EquipmentSlot.CHEST);

                if(!TravelersBackpackConfig.CLIENT.renderBackpackWithElytra.get())
                {
                    if(stack.getItem() instanceof ElytraItem)
                    {
                        return;
                    }
                    else
                    {
                        renderLayer(poseStack, bufferIn, packedLightIn, clientPlayer, inv, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                    }
                }
                else
                {
                    renderLayer(poseStack, bufferIn, packedLightIn, clientPlayer, inv, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        }
    }

    private void renderLayer(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, ITravelersBackpackContainer container, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        //model = new TravelersBackpackWearableModel(clientPlayer, bufferIn, Minecraft.getInstance().getEntityModels().bakeLayer(ModClientEventsHandler.TRAVELERS_BACKPACK_WEARABLE));
        model = new TravelersBackpackWearableModel(clientPlayer, bufferIn, TravelersBackpackBlockEntityRenderer.createTravelersBackpack(true).bakeRoot());
        boolean flag = container.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get();

        ResourceLocation loc = ResourceUtils.getBackpackTexture(container.getItemStack().getItem());

        boolean isColorable = false;

        if(container.getItemStack().getTag() != null)
        {
            if(BackpackDyeRecipe.hasColor(container.getItemStack()))
            {
                isColorable = true;
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        VertexConsumer vertexConsumer = bufferIn.getBuffer(flag ? RenderType.entityTranslucentCull(loc) : RenderType.entitySolid(loc));
        //IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, model.getRenderType(ResourceUtils.WEARABLE_RESOURCE_LOCATIONS.get(ModItems.BACKPACKS.indexOf(inv.getItemStack().getItem()))), false, true);

     //   if(inv.getItemStack().isEnchanted())
     //   {
     //       ivertexbuilder = ItemRenderer.getBuffer(bufferIn, model.getRenderType(loc), false, true);
     //   }

        poseStack.pushPose();

        if(clientPlayer.isCrouching())
        {
            poseStack.translate(0D, -0.155D, 0.025D);
        }

        //this.getParentModel().copyPropertiesTo(model);

        //model.prepareMobModel(clientPlayer, limbSwing, limbSwingAmount, partialTicks);
        //model.setupAnim(clientPlayer, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        //this.getParentModel().set
        this.getParentModel().copyPropertiesTo(model);
        model.setupAngles(this.getParentModel());
        //followBodyRotations(clientPlayer, model);
        //this.getEntityModel().setModelAttributes(model);    //#TODO Is it okay? I know no other way to stick model to player's model
        //model.setupAngles(this.getEntityModel());

        poseStack.translate(0, 0.175, 0.325);
        poseStack.scale(0.85F, 0.85F, 0.85F);

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(container.getItemStack()));
            model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            vertexConsumer = bufferIn.getBuffer(RenderType.entityCutout(loc));

        }

        model.renderToBuffer(poseStack, vertexConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

  /*  static void followBodyRotations(final LivingEntity livingEntity,
                                    final HumanoidModel<LivingEntity>... models) {

        EntityRenderer<? super LivingEntity> render =
                Minecraft.getInstance().getEntityRenderDispatcher()
                        .getRenderer(livingEntity);

        if (render instanceof LivingEntityRenderer) {
            @SuppressWarnings("unchecked") LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>
                    livingRenderer = (LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
            EntityModel<LivingEntity> entityModel = livingRenderer.getModel();

            if (entityModel instanceof HumanoidModel) {

                for (HumanoidModel<LivingEntity> model : models) {
                    HumanoidModel<LivingEntity> bipedModel = (HumanoidModel<LivingEntity>) entityModel;
                    bipedModel.copyPropertiesTo(model);
                }
            }
        }

    } */
}
