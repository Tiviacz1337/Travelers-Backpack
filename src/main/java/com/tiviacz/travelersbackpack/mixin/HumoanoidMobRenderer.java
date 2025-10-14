package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.HumanoidRenderStateBackpackInject;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
public class HumoanoidMobRenderer {
    @Inject(at = @At(value = "TAIL"), method = "extractHumanoidRenderState")
    private static void render(LivingEntity entity, HumanoidRenderState reusedState, float partialTick, ItemModelResolver itemModelResolver, CallbackInfo ci) {
        if(entity instanceof Player player && reusedState instanceof HumanoidRenderStateBackpackInject injectedReusedState) {
            ItemStack backpack = ComponentUtils.getWearingBackpack(player);
            if(!backpack.isEmpty()) {
                injectedReusedState.setBackpackStack(backpack);
            } else if(injectedReusedState.getBackpackStack() != null) {
                injectedReusedState.setBackpackStack(null);
            }
        }

        if(entity instanceof Zombie || entity instanceof Skeleton || entity instanceof EnderMan || entity instanceof WitherSkeleton || entity instanceof Piglin) {
            if(reusedState instanceof HumanoidRenderStateBackpackInject injectedReusedState) {
                ItemStack backpack = entity.getItemBySlot(EquipmentSlot.CHEST);
                if(backpack.getItem() instanceof TravelersBackpackItem) {
                    injectedReusedState.setChestItem(backpack);
                }
            }
        }
    }
}
