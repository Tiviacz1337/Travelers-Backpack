package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.TravelersBackpackWearableModel;
import com.tiviacz.travelersbackpack.common.BackpackDyeRecipe;
import com.tiviacz.travelersbackpack.compat.curios.TravelersBackpackCurios;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import com.tiviacz.travelersbackpack.util.ResourceUtils;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackLayer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>
{
    public TravelersBackpackWearableModel model;

    public TravelersBackpackLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn)
    {
        super(entityRendererIn);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if(CapabilityUtils.isWearingBackpack(entitylivingbaseIn))
        {
            ITravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(entitylivingbaseIn);

            if(inv != null && entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible())
            {
                if(TravelersBackpackConfig.curiosIntegration)
                {
                    if(TravelersBackpackCurios.getCurioTravelersBackpack(entitylivingbaseIn).isPresent())
                    {
                        ICuriosItemHandler curios = CuriosApi.getCuriosHelper().getCuriosHandler(entitylivingbaseIn).resolve().get();
                        IDynamicStackHandler stackHandler = curios.getStacksHandler("back").get().getStacks();

                        for(int i = 0; i < stackHandler.getSlots(); i++)
                        {
                            if(stackHandler.getStackInSlot(i).getItem() instanceof TravelersBackpackItem)
                            {
                                if(curios.getCurios().get("back").getRenders().get(i))
                                {
                                    renderLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, inv);
                                }
                                else
                                {
                                    return;
                                }
                            }
                        }
                    }
                }

                ItemStack stack = entitylivingbaseIn.getItemStackFromSlot(EquipmentSlotType.CHEST);

                if(!TravelersBackpackConfig.CLIENT.renderBackpackWithElytra.get())
                {
                    if(stack.getItem() instanceof ElytraItem)
                    {
                        return;
                    }
                    else
                    {
                        renderLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, inv);
                    }
                }
                else
                {
                    renderLayer(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, inv);
                }
            }
        }
    }

    private void renderLayer(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, AbstractClientPlayerEntity entitylivingbaseIn, ITravelersBackpackInventory inv)
    {
        model = new TravelersBackpackWearableModel(entitylivingbaseIn, bufferIn);
        boolean flag = inv.getItemStack().getItem() == ModItems.QUARTZ_TRAVELERS_BACKPACK.get();

        ResourceLocation loc = ResourceUtils.getBackpackTexture(inv.getItemStack().getItem());

        boolean isColorable = false;

        if(inv.getItemStack().getTag() != null)
        {
            if(BackpackDyeRecipe.hasColor(inv.getItemStack()))
            {
                isColorable = true;
                loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed.png");
            }
        }

        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(flag ? RenderType.getEntityTranslucentCull(loc) : RenderType.getEntitySolid(loc));
        //IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, model.getRenderType(ResourceUtils.WEARABLE_RESOURCE_LOCATIONS.get(ModItems.BACKPACKS.indexOf(inv.getItemStack().getItem()))), false, true);

     //   if(inv.getItemStack().isEnchanted())
     //   {
     //       ivertexbuilder = ItemRenderer.getBuffer(bufferIn, model.getRenderType(loc), false, true);
     //   }

        matrixStackIn.push();

        if(entitylivingbaseIn.isSneaking())
        {
            matrixStackIn.translate(0D, -0.155D, 0.025D);
        }

        this.getEntityModel().setModelAttributes(model);    //#TODO Is it okay? I know no other way to stick model to player's model
        model.setupAngles(this.getEntityModel());

        matrixStackIn.translate(0, 0.175, 0.325);
        matrixStackIn.scale(0.85F, 0.85F, 0.85F);

        if(isColorable)
        {
            Triple<Float, Float, Float> rgb = RenderUtils.intToRGB(BackpackDyeRecipe.getColor(inv.getItemStack()));
            model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, rgb.getLeft(), rgb.getMiddle(), rgb.getRight(), 1.0F);

            loc = new ResourceLocation(TravelersBackpack.MODID, "textures/model/dyed_extras.png");
            ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(loc));

        }
        model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStackIn.pop();
    }
}
