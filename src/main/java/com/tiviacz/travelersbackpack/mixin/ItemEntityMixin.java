package com.tiviacz.travelersbackpack.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin
{
    @Shadow public abstract ItemStack getStack();

    //@Inject(at = @At(value = "HEAD"), method = "damage(Lnet/minecraft/entity/damage/DamageSource;F)Z")
    private void damageItemEntity(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info)
    {
        //if(this.getStack().getItem() instanceof TravelersBackpackItem)
       // {
        //    info.setReturnValue(false);
       // }
    }
}
