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

    private final ItemStackRenderState backpackRenderState = new ItemStackRenderState();
    //private final StackModelPart tools = new StackModelPart();

    @Override
    public void submit(ItemStack itemStack, TrinketSlotAccess trinketSlotAccess, EntityModel<? extends LivingEntityRenderState> entityModel, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, LivingEntityRenderState livingEntityRenderState, float v, float v1) {
        if(itemStack.getItem() instanceof TravelersBackpackItem && entityModel instanceof PlayerModel playerModel && livingEntityRenderState instanceof HumanoidRenderState humanoidRenderState) {
            StackModelPart tools = new StackModelPart();
            BackpackLayer.renderBackpackLayer(playerModel, poseStack, submitNodeCollector, i, humanoidRenderState, backpackRenderState, tools, itemStack);
        }
    }
}
