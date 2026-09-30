package com.tiviacz.travelersbackpack.mixin.entity.item;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.PowerParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void setUnlimitedLifetime();

    @Unique
    private boolean travelersbackpack$wasFloatingUp = false;

    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Unique
    private boolean travelersbackpack$isBackpack() {
        return this.getItem().getItem() instanceof TravelersBackpackItem;
    }

    @Inject(method = "setItem", at = @At("TAIL"))
    private void travelersbackpack$onSetItem(ItemStack stack, CallbackInfo ci) {
        if(stack.getItem() instanceof TravelersBackpackItem) {
            boolean neverDespawn = TravelersBackpackConfig.SERVER.backpackSettings.neverDespawn.get();
            if(neverDespawn) {
                this.setUnlimitedLifetime();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void travelersbackpack$onTick(CallbackInfo ci) {
        if(!this.travelersbackpack$isBackpack()) return;

        if(TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            if(!this.level().isClientSide() && !this.isNoGravity() && this.travelersbackpack$wasFloatingUp && this.getY() < this.level().getMinY()) {
                if(this.random.nextFloat() > 0.25F) {
                    float ab = this.random.nextFloat() * 2.0f;
                    float ag = this.random.nextFloat() * ((float)Math.PI * 2);
                    double n = Mth.cos(ag) * ab;
                    double o = 0.01 + this.random.nextDouble() * 0.5;
                    double p = Mth.sin(ag) * ab;
                    ((ServerLevel)this.level()).sendParticles(PowerParticleOption.create(ParticleTypes.DRAGON_BREATH, 1.0F), this.getX() + n * 0.1, this.getY() + 0.3, this.getZ() + p * 0.1, 0, n * 0.01F, o * 0.1F, p * 0.01F, 1.0F);
                }
            }
            if(!this.isNoGravity()) {
                if(this.isInWater() || this.isInLava()) {
                    this.onInsideBubbleColumn(false);
                    this.travelersbackpack$wasFloatingUp = true;
                } else if(this.travelersbackpack$wasFloatingUp) {
                    this.setNoGravity(true);
                    this.setDeltaMovement(Vec3.ZERO);
                }
            }
        }
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    private void travelersbackpack$fireImmune(CallbackInfoReturnable<Boolean> cir) {
        if(this.travelersbackpack$isBackpack() && TravelersBackpackConfig.SERVER.backpackSettings.fireResistant.get()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "ignoreExplosion", at = @At("HEAD"), cancellable = true)
    private void travelersbackpack$ignoreExplosion(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        if(this.travelersbackpack$isBackpack() && TravelersBackpackConfig.SERVER.backpackSettings.explosionResistant.get()) {
            cir.setReturnValue(true);
        }
    }
}