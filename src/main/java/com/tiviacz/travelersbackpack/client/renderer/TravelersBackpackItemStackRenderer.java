package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    public TravelersBackpackItemStackRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet) {
        super(renderDispatcher, modelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        TravelersBackpackBlockEntityRenderer.renderByItem(new RenderData(stack, stack.has(ModDataComponents.FLUID_TANKS) || stack.has(DataComponents.DYED_COLOR) || stack.has(ModDataComponents.SLEEPING_BAG_COLOR)), poseStack, buffer, combinedLight, combinedOverlay);
    }
}