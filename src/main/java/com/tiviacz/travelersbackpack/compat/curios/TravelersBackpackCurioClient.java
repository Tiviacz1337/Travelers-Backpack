package com.tiviacz.travelersbackpack.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class TravelersBackpackCurioClient {
    public static void registerCurioRenderer() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> ICurioRenderer.register(holder.get(), TravelersBackpackCurioClient.Renderer::new));
    }

    public static class Renderer implements ICurioRenderer {
        private final ItemStackRenderState backpackRenderState = new ItemStackRenderState();
        private final StackModelPart tools = new StackModelPart();

        @Override
        public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, S renderState, RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context, float yRotation, float xRotation) {
            if(stack.getItem() instanceof TravelersBackpackItem && renderLayerParent.getModel() instanceof PlayerModel playerModel && renderState instanceof AvatarRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, poseStack, submitNodeCollector, packedLight, playerRenderState, backpackRenderState, tools, stack);
            }
        }
    }
}