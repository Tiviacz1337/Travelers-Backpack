package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.util.RenderUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class FluidRenderer extends ModelRenderer
{
    private final PlayerEntity player;
    private final IRenderTypeBuffer buffer;

    public FluidRenderer(Model model, PlayerEntity player, IRenderTypeBuffer buffer)
    {
        super(model);
        this.player = player;
        this.buffer = buffer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn)
    {
        matrixStackIn.push();
        this.translateRotate(matrixStackIn);
        render(this.player, matrixStackIn, this.buffer, packedLightIn);
        matrixStackIn.pop();
    }

    public void render(PlayerEntity player, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn)
    {
        RenderSystem.enableRescaleNormal();
        matrixStackIn.push();
        matrixStackIn.scale(1F, 1.05F, 1F);

        ITravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);

        RenderUtils.renderFluidInTank(inv, inv.getRightTank(), matrixStackIn, bufferIn, packedLightIn,0.24F, -0.55F, -0.235F);
        RenderUtils.renderFluidInTank(inv, inv.getLeftTank(), matrixStackIn, bufferIn, packedLightIn, -0.66F, -0.55F, -0.235F);

        matrixStackIn.pop();
        RenderSystem.disableRescaleNormal();
    }
}