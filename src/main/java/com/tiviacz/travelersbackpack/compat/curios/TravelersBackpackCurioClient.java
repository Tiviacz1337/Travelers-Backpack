package com.tiviacz.travelersbackpack.compat.curios;

import top.theillusivec4.curios.api.client.ICurioRenderer;

public class TravelersBackpackCurioClient {
    public static void registerCurioRenderer() {
        /*ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> ICurioRenderer.register(holder.get(), TravelersBackpackCurioClient.Renderer::new));*/
    }

    public static class Renderer implements ICurioRenderer {
        /*@Override
        public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, @Nonnull MultiBufferSource renderTypeBuffer, int packedLight, S renderState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context, float yRotation, float xRotation) {
            if(stack.getItem() instanceof TravelersBackpackItem && renderLayerParent.getModel() instanceof PlayerModel playerModel && renderState instanceof AvatarRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, poseStack, renderTypeBuffer, packedLight, playerRenderState, stack);
            }
        }*/
    }
}