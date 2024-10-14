package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.BackpackLayerModel;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TravelersBackpackEntityLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {
    public TravelersBackpackEntityLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, LivingEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (pLivingEntity.getItemBySlot(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
            TravelersBackpackLayer.renderBackpackLayer(BackpackLayerModel.LAYER_MODEL, getParentModel(), pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLivingEntity.getItemBySlot(EquipmentSlot.BODY));
        }
    }
}