package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.util.HumanoidRenderStateBackpackInject;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements HumanoidRenderStateBackpackInject {
    @Shadow public ItemStack chestEquipment;
    public ItemStack backpack;

    @Override
    public void setBackpackStack(ItemStack stack) {
        this.backpack = stack;
    }

    @Override
    public ItemStack getBackpackStack() {
        return this.backpack;
    }

    @Override
    public void setChestItem(ItemStack stack) {
        chestEquipment = stack;
    }
}
