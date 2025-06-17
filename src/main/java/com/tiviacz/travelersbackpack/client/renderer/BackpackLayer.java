package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.client.model.BackpackModel;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Supporters;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BackpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final BackpackModel BACKPACK_MODEL = new BackpackModel();

    public BackpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer clientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(TravelersBackpack.enableIntegration()) return;

        if(CapabilityUtils.isWearingBackpack(clientPlayer)) {
            ItemStack stack = CapabilityUtils.getWearingBackpack(clientPlayer);
            renderBackpackLayer(getParentModel(), poseStack, bufferIn, packedLightIn, clientPlayer, stack);
        }
    }

    public static void renderBackpackLayer(HumanoidModel humanoidModel, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, LivingEntity entity, ItemStack stack) {
        if(!NbtHelper.getOrDefault(stack, ModDataHelper.IS_VISIBLE, true))
            return;

        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        poseStack.pushPose();
        alignModel(poseStack, humanoidModel, BACKPACK_MODEL, entity);
        BACKPACK_MODEL.render(poseStack, packedLightIn, bufferIn, stack);

        if(entity instanceof Player player && Supporters.SUPPORTERS.contains(player.getGameProfile().getName())) {
            BACKPACK_MODEL.supporterBadgeModel.render(poseStack, packedLightIn);
        }

        poseStack.popPose();
    }

    public static void alignModel(PoseStack poseStack, HumanoidModel parent, BackpackModel backpackModel, LivingEntity entity) {
        backpackModel.copyFrom(parent.body);
        backpackModel.supporterBadgeModel.copyFrom(parent.body);

        if(entity.isBaby()) {
            poseStack.translate(0F, 0.8F, -0.165F);
            float scaleFactor = entity.getScale();
            poseStack.scale(scaleFactor + 0.1F, scaleFactor + 0.1F, scaleFactor + 0.1F);
        }
    }
}