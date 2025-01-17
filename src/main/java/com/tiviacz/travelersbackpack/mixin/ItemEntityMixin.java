package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.handler.StorageAccessWrapper;
import com.tiviacz.travelersbackpack.util.InventoryHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

    @Shadow
    public abstract void setItem(ItemStack stack);

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"), cancellable = true)
    private void playerTouch(Player player, CallbackInfo ci) {
        if(this.getItem().isEmpty() || this.pickupDelay > 0) {
            return;
        }

        Level level = player.level();

        if(ComponentUtils.isWearingBackpack(player)) {
            BackpackWrapper wrapper = ComponentUtils.getBackpackWrapper(player);
            if(wrapper.getUpgradeManager().pickupUpgrade.isPresent() && wrapper.getUpgradeManager().pickupUpgrade.get().canPickup(this.getItem())) {
                ItemStack remainingStack = InventoryHelper.insertItemStacked(new StorageAccessWrapper(wrapper, wrapper.getStorage()), this.getItem(), false);
                if(remainingStack != this.getItem()) {
                    level.playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (level.random.nextFloat() - level.random.nextFloat()) * 1.4F + 2.0F);
                    this.setItem(remainingStack);
                    ci.cancel();
                }
            }
        }
    }
}