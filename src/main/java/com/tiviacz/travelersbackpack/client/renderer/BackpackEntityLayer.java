package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;

public class BackpackEntityLayer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    public BackpackEntityLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> renderer) {
        super(renderer);
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector sumbitNodeCollector, int packedLightIn, HumanoidRenderState state, float limbSwing, float limbSwingAmount) {
        ItemStack backpack = state.chestEquipment;
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            BackpackLayer.renderBackpackLayer(getParentModel(), poseStack, sumbitNodeCollector, packedLightIn, state, backpack);
        }
    }

   /* @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, HumanoidRenderState pLivingEntity, float p_117353_, float p_117354_) {
        ItemStack backpack = pLivingEntity.chestEquipment;
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            BackpackLayer.renderBackpackLayer(getParentModel(), pPoseStack, pBuffer, pPackedLight, pLivingEntity, backpack);
        }
    } */
}