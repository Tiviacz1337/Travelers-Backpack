package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackpackEntityLayer extends RenderLayer<LivingEntity, HumanoidModel<LivingEntity>> {
    public BackpackEntityLayer(RenderLayerParent<LivingEntity, HumanoidModel<LivingEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, LivingEntity pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        ItemStack backpack = pLivingEntity.getItemBySlot(EquipmentSlot.CHEST);
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            BackpackLayer.renderBackpackLayer(getParentModel(), pPoseStack, pBuffer, pPackedLight, pLivingEntity, backpack);
        }
    }
}