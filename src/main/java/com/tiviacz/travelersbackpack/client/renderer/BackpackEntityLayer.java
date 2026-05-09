package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

public class BackpackEntityLayer extends RenderLayer<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    //RenderStates
    private final StackModelPart tools;
    private final ItemStackRenderState backpackRenderState;

    public BackpackEntityLayer(RenderLayerParent<HumanoidRenderState, HumanoidModel<HumanoidRenderState>> renderer) {
        super(renderer);
        this.tools = new StackModelPart();
        this.backpackRenderState = new ItemStackRenderState();
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector sumbitNodeCollector, int packedLightIn, HumanoidRenderState state, float limbSwing, float limbSwingAmount) {
        ItemStack backpack = state.chestEquipment;
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            BackpackLayer.renderBackpackLayer(getParentModel(), poseStack, sumbitNodeCollector, packedLightIn, state, this.backpackRenderState, this.tools, backpack);
        }
    }
}