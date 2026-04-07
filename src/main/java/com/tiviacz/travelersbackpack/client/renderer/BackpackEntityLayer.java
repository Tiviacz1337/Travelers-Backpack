package com.tiviacz.travelersbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tiviacz.travelersbackpack.client.model.StackModelPart;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
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
    public void submit(PoseStack pPoseStack, SubmitNodeCollector sumbitNodeCollector, int pPackedLight, HumanoidRenderState pLivingEntity, float p_117353_, float p_117354_) {
        ItemStack backpack = pLivingEntity.chestEquipment;
        if(backpack.getItem() instanceof TravelersBackpackItem) {
            BackpackLayer.renderBackpackLayer(getParentModel(), pPoseStack, sumbitNodeCollector, pPackedLight, pLivingEntity, this.backpackRenderState, this.tools, backpack);
        }
    }
}