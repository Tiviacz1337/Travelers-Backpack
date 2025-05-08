package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class StackModelPart extends BackpackModelPart {
    public List<ItemStack> prepare(ItemStack stack) {
        if(stack.has(ModDataComponents.TOOLS_CONTAINER)) {
            return new ArrayList<>(stack.get(ModDataComponents.TOOLS_CONTAINER).getItems()).stream().filter(itemStack -> !itemStack.isEmpty()).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public void render(ItemStack backpack, MultiBufferSource buffer, PoseStack poseStack, int light, int overlay) {
        if(buffer == null) {
            return;
        }
        render(prepare(backpack), poseStack, buffer, light, overlay);
    }

    public void render(List<ItemStack> tools, PoseStack poseStack, MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
        if(tools.isEmpty()) return;

        ItemStack toolUpper = tools.get(0);
        ItemStack toolLower = ItemStack.EMPTY;

        if(!toolUpper.isEmpty() && tools.size() > 1) {
            toolLower = tools.get(tools.size() - 1);
        }

        poseStack.pushPose();

        if(!toolUpper.isEmpty()) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolUpper, null, null, 0);
            model = applyTransform(model, poseStack);

            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.translate(0.04D, 0.075D, 0.17D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180F));
            poseStack.scale(0.50F, 0.50F, 0.50F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolUpper, ItemDisplayContext.NONE, false, poseStack, buffer, pPackedLight, pPackedOverlay, model);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        if(!toolLower.isEmpty()) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolLower, null, null, 0);
            model = applyTransform(model, poseStack);

            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

            poseStack.translate(-0.25, 0.75, -0.025);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.scale(0.50F, 0.50F, 0.50F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolLower, ItemDisplayContext.NONE, false, poseStack, buffer, pPackedLight, pPackedOverlay, model);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    //Forge
    public BakedModel applyTransform(BakedModel model, PoseStack poseStack) {
        return model.applyTransform(ItemDisplayContext.NONE, poseStack, false);
    }
}