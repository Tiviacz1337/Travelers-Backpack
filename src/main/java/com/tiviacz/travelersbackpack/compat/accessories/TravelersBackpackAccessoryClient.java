package com.tiviacz.travelersbackpack.compat.accessories;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import io.wispforest.accessories.api.client.AccessoriesRenderStateKeys;
import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.client.AccessoryRenderState;
import io.wispforest.accessories.api.client.renderers.SimpleAccessoryRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class TravelersBackpackAccessoryClient {
    public static void init() {
        /*ModItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof TravelersBackpackItem)
                .forEach(holder -> AccessoriesRendererRegistry.registerRenderer(holder.get(), TravelersBackpackAccessoryClient.Renderer::new));*/
    }

    public static class Renderer implements SimpleAccessoryRenderer {
        @Override
        public <S extends LivingEntityRenderState> void render(AccessoryRenderState accessoryState, S entityState, EntityModel<S> model, PoseStack matrices, SubmitNodeCollector collector) {
            var stack = accessoryState.getStateData(AccessoriesRenderStateKeys.ITEM_STACK);
            var light = accessoryState.getStateData(AccessoriesRenderStateKeys.LIGHT);
            var backpackRenderState = new ItemStackRenderState();
            var tools = new StackModelPart();
            if(stack.getItem() instanceof TravelersBackpackItem && model instanceof PlayerModel playerModel && entityState instanceof AvatarRenderState playerRenderState) {
                BackpackLayer.renderBackpackLayer(playerModel, matrices, collector, light, playerRenderState, backpackRenderState, tools, stack);
            }
        }

        @Override
        public <S extends LivingEntityRenderState> void align(AccessoryRenderState accessoryRenderState, S s, EntityModel<S> entityModel, PoseStack poseStack) {

        }
    }
}