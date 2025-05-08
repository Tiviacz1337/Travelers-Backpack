package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackpackItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    private final Minecraft minecraft = Minecraft.getInstance();

    public BackpackItemStackRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(stack, null, minecraft.player, 0);
        for(var model : bakedModel.getRenderPasses(stack, true)) {
            for(var rendertype : model.getRenderTypes(stack, true)) {
                VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(model, stack, combinedLight, combinedOverlay, poseStack, ivertexbuilder);
            }
        }
        //BackpackBlockEntityRenderer.renderByItem(stack, poseStack, buffer, combinedLight, combinedOverlay);
    }
}