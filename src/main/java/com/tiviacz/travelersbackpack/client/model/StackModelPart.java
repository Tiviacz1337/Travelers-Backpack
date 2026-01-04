package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class StackModelPart extends BackpackModelPart {
    private final ItemModelResolver resolver;
    private final ItemStackRenderState upper;
    private final ItemStackRenderState lower;

    public StackModelPart() {
        this.resolver = Minecraft.getInstance().getItemModelResolver();
        this.upper = new ItemStackRenderState();
        this.lower = new ItemStackRenderState();
    }

    public List<ItemStack> prepare(ItemStack stack) {
        if(stack.has(ModDataComponents.TOOLS_CONTAINER)) {
            return new ArrayList<>(stack.get(ModDataComponents.TOOLS_CONTAINER).getItems()).stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public void render(ItemStack backpack, SubmitNodeCollector collector, PoseStack poseStack, int light, int overlay) {
        if(collector == null) {
            return;
        }
        render(prepare(backpack), poseStack, collector, light, overlay);
    }

    public void render(List<ItemStack> tools, PoseStack poseStack, SubmitNodeCollector collector, int pPackedLight, int pPackedOverlay) {
        if(tools.isEmpty()) return;

        ItemStack toolUpper = tools.get(0);
        ItemStack toolLower = ItemStack.EMPTY;

        if(!toolUpper.isEmpty() && tools.size() > 1) {
            toolLower = tools.get(tools.size() - 1);
        }

        poseStack.pushPose();

        if(!toolUpper.isEmpty()) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.translate(0.04D, 0.075D, 0.17D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180F));
            poseStack.scale(0.50F, 0.50F, 0.50F);

            resolver.updateForTopItem(upper, toolUpper, getDisplayContext(toolUpper), null, null, 0);
            upper.submit(poseStack, collector, pPackedLight, pPackedOverlay, 0);
           // Minecraft.getInstance().getItemRenderer().renderStatic(toolUpper, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, poseStack, buffer, null, 0);

            poseStack.popPose();
        }

        if(!toolLower.isEmpty()) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.translate(-0.25, 0.75, -0.025);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.scale(0.50F, 0.50F, 0.50F);

            resolver.updateForTopItem(lower, toolLower, getDisplayContext(toolLower), null, null, 0);
            lower.submit(poseStack, collector, pPackedLight, pPackedOverlay, 0);
            //Minecraft.getInstance().getItemRenderer().renderStatic(toolLower, ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, poseStack, buffer, null, 0);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    public ItemDisplayContext getDisplayContext(ItemStack stack) {
        if(stack.is(Items.TRIDENT) || stack.is(Items.SPYGLASS)) {
            return ItemDisplayContext.GUI;
        }
        return ItemDisplayContext.NONE;
    }
}