package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.upgrades.pickup.AutoPickupUpgrade;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public int pickupDelay;

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void playerTouch(Player player, CallbackInfo ci) {
        if(this.getItem().isEmpty() || this.pickupDelay > 0) {
            return;
        }

        Level level = player.level();

        if(ComponentUtils.isWearingBackpack(player)) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
            wrapper.getUpgradeManager().getUpgrade(AutoPickupUpgrade.class).ifPresent(pickupUpgrade -> {
                if(pickupUpgrade.canPickup(getItem()) && pickupUpgrade.tryPickup((ItemEntity)(Object)this, level, player.blockPosition())) {
                    ci.cancel();
                }
            });
        }
    }
}