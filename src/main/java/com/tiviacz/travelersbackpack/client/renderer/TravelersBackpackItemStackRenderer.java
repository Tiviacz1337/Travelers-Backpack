package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackItemStackRenderer extends BlockEntityWithoutLevelRenderer
{
    private final Supplier<TravelersBackpackBlockEntity> blockEntity;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public TravelersBackpackItemStackRenderer(BlockEntityRenderDispatcher renderDispatcher, EntityModelSet modelSet, Supplier<TravelersBackpackBlockEntity> blockEntity) {
        super(renderDispatcher, modelSet);

        this.blockEntity = blockEntity;
        this.blockEntityRenderDispatcher = renderDispatcher;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay)
    {
        //TravelersBackpackBlockEntityRenderer.render(new TravelersBackpackContainer(stack, Minecraft.getInstance().player, (byte)0), null, poseStack, buffer, combinedLight, combinedOverlay);
        TravelersBackpackBlockEntityRenderer.renderByItem(new RenderData(Minecraft.getInstance().player, stack, stack.hasTag()), poseStack, buffer, combinedLight, combinedOverlay);
    }
}