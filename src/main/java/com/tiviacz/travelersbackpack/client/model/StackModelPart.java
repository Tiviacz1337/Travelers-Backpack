package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class StackModelPart extends ModelPart {
    private List<ItemStack> tools = new ArrayList<>();
    private MultiBufferSource buffer;

    public StackModelPart(ModelPart parent) {
        super(parent.cubes, parent.children);
    }

    public void prepare(ItemStack stack, MultiBufferSource buffer) {
        if (stack.has(ModDataComponents.TOOLS_CONTAINER)) {
            this.tools = new ArrayList<>(stack.get(ModDataComponents.TOOLS_CONTAINER).getItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList());
        } else {
            if (!this.tools.isEmpty()) {
                this.tools.clear();
            }
        }
        this.buffer = buffer;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertices, int light, int overlay) {
        if (this.buffer == null) {
            //LogHelper.error("Rendering error! Trying to render StackModelPart without passing player or buffer!");
            return;
        }
        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        render(poseStack, this.buffer, light, overlay);
        poseStack.popPose();
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int pPackedLight, int pPackedOverlay) {
        if (tools.isEmpty()) return;

        ItemStack toolUpper = this.tools.get(0);
        ItemStack toolLower = ItemStack.EMPTY;

        if (!toolUpper.isEmpty() && tools.size() > 1) {
            toolLower = this.tools.get(tools.size() - 1);
        }

        poseStack.pushPose();

        if (!toolUpper.isEmpty()) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolUpper, null, null, 0);
            //model = ClientHooks.handleCameraTransforms(poseStack, model, ItemDisplayContext.NONE, false);

            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.translate(0.05D, 0.075D, 0.27D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180F));
            poseStack.scale(0.65F, 0.65F, 0.65F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolUpper, ItemDisplayContext.NONE, false, poseStack, buffer, pPackedLight, pPackedOverlay, model);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        if (!toolLower.isEmpty()) {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolLower, null, null, 0);
            //model = ClientHooks.handleCameraTransforms(poseStack, model, ItemDisplayContext.NONE, false);

            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.translate(-0.325, 0.95, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.scale(0.65F, 0.65F, 0.65F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolLower, ItemDisplayContext.NONE, false, poseStack, buffer, pPackedLight, pPackedOverlay, model);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}