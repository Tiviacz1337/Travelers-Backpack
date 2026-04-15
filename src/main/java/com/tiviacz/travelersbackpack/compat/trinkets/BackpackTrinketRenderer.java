package com.tiviacz.travelersbackpack.compat.trinkets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.client.renderer.BackpackLayer;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.client.TrinketRenderer;
import eu.pb4.trinkets.api.client.TrinketRendererRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

public class BackpackTrinketRenderer implements TrinketRenderer {
    public static void initClient() {
        BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof TravelersBackpackItem)
                .forEach(item -> TrinketRendererRegistry.registerRenderer(item, new BackpackTrinketRenderer()));
    }

    @Override
    public void submit(ItemStack itemStack, TrinketSlotAccess trinketSlotAccess, EntityModel<? extends LivingEntityRenderState> entityModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, LivingEntityRenderState livingEntityRenderState, float v, float v1) {
        ItemStackRenderState backpackRenderState = new ItemStackRenderState();
        StackModelPart tools = new StackModelPart();
        if(itemStack.getItem() instanceof TravelersBackpackItem && entityModel instanceof PlayerModel playerModel && livingEntityRenderState instanceof HumanoidRenderState humanoidRenderState) {
            BackpackLayer.renderBackpackLayer(playerModel, poseStack, submitNodeCollector, i, humanoidRenderState, backpackRenderState, tools, itemStack);
        }
    }
}
