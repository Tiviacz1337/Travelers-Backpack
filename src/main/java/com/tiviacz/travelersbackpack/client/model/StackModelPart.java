package com.tiviacz.travelersbackpack.client.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.screens.OverlayScreen;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class StackModelPart extends ModelPart
{
    public StackModelPart(ModelPart parent)
    {
        super(parent.cubes, parent.children);
    }

    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, Player player, MultiBufferSource buffer, int combinedLight, int combinedOverlay, float r, float g, float b, float a)
    {
        ITravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

        List<ItemStack> tools = OverlayScreen.getTools(container.getTier(), container.getHandler());

        ItemStack toolUpper = container.getHandler().getStackInSlot(container.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST));
        ItemStack toolLower = ItemStack.EMPTY;

        if(!toolUpper.isEmpty() && tools.size() > 1)
        {
            toolLower = container.getHandler().getStackInSlot(container.getTier().getSlotIndex(Tiers.SlotType.TOOL_SECOND) + tools.size() - 2);
        }

        poseStack.pushPose();
        this.translateAndRotate(poseStack);

        if(!toolUpper.isEmpty())
        {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolUpper, player.level, player, 0);
            model = ForgeHooksClient.handleCameraTransforms(poseStack, model, ItemDisplayContext.NONE, false);

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.pushPose();
            poseStack.translate(0.05D, 0.075D, 0.27D);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.mulPose(Axis.XP.rotationDegrees(180F));
            poseStack.scale(0.65F, 0.65F, 0.65F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolUpper, ItemDisplayContext.NONE, false, poseStack, buffer, combinedLight, combinedOverlay, model);

            poseStack.popPose();
            RenderSystem.disableBlend();
        }

        if(!toolLower.isEmpty())
        {
            BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolLower, player.level, player, 0);
            model = ForgeHooksClient.handleCameraTransforms(poseStack, model, ItemDisplayContext.NONE, false);

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            poseStack.pushPose();
            poseStack.translate(-0.35, 0.95, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(90F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45F));
            poseStack.scale(0.65F, 0.65F, 0.65F);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer().render(toolLower, ItemDisplayContext.NONE, false, poseStack, buffer, combinedLight, combinedOverlay, model);

            poseStack.popPose();
            RenderSystem.disableBlend();
        }

        poseStack.popPose();
    }
}