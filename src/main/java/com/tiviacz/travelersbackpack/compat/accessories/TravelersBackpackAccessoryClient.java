package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.renderers.SimpleAccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotPath;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.item.ItemStack;

public class TravelersBackpackAccessoryClient {
    public static void init() {
        ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder.get(), TravelersBackpackAccessoryClient.Renderer::new));
    }

    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <S extends LivingEntityRenderState> void render(ItemStack stack, SlotPath path, PoseStack matrices, EntityModel<S> model, S renderState, MultiBufferSource multiBufferSource, int light, float partialTicks) {
            if(stack.getItem() instanceof TravelersBackpackItem && model instanceof PlayerModel playerModel && renderState instanceof PlayerRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, matrices, multiBufferSource, light, playerRenderState, stack);
            }
        }

        @Override
        public <S extends LivingEntityRenderState> void align(ItemStack itemStack, SlotPath slotPath, EntityModel<S> entityModel, S s, PoseStack poseStack) {

        }
    }
}