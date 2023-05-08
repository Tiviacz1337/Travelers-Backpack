package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;

public class StackRenderer extends ModelRenderer
{
    private final PlayerEntity player;
    private final IRenderTypeBuffer buffer;

    public StackRenderer(Model model, PlayerEntity player, IRenderTypeBuffer buffer)
    {
        super(model);
        this.player = player;
        this.buffer = buffer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn)
    {
        matrixStackIn.pushPose();
        this.translateAndRotate(matrixStackIn);
        render(this.player, matrixStackIn, this.buffer, packedLightIn, packedOverlayIn);
        matrixStackIn.popPose();
    }

    public void render(PlayerEntity player, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        ITravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
        ItemStack toolUpper = inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER));
        ItemStack toolLower = inv.getInventory().getStackInSlot(inv.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER));

        if(!toolUpper.isEmpty())
        {
            IBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolUpper, player.level, player);
            model = ForgeHooksClient.handleCameraTransforms(matrixStackIn, model, ItemCameraTransforms.TransformType.NONE, false);

            RenderSystem.enableRescaleNormal();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            matrixStackIn.pushPose();
            matrixStackIn.translate(0.05, 0.075, 0.27);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(45F));
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(180F));
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);

            Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer()
                    .render(toolUpper, ItemCameraTransforms.TransformType.NONE, false, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, model);

            matrixStackIn.popPose();
            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
        }

        if(!toolLower.isEmpty())
        {
            IBakedModel model = Minecraft.getInstance().getItemRenderer().getModel(toolLower, player.level, player);
            model = ForgeHooksClient.handleCameraTransforms(matrixStackIn, model, ItemCameraTransforms.TransformType.NONE, false);

            RenderSystem.enableRescaleNormal();
            RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            matrixStackIn.pushPose();
            matrixStackIn.translate(-0.35, 0.95, 0);
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(90F));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(45F));
            matrixStackIn.scale(0.65F, 0.65F, 0.65F);

            Minecraft.getInstance().getTextureManager().bind(PlayerContainer.BLOCK_ATLAS);
            Minecraft.getInstance().getItemRenderer()
                        .render(toolLower, ItemCameraTransforms.TransformType.NONE, false, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, model);
            matrixStackIn.popPose();

            RenderSystem.disableRescaleNormal();
            RenderSystem.disableBlend();
        }
    }
}