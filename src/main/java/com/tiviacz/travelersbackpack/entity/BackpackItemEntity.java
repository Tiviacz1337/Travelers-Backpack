package com.tiviacz.travelersbackpack.entity;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BackpackItemEntity extends ItemEntity {
    public boolean wasFloatingUp = false;
    public boolean isInvulnerable;

    public BackpackItemEntity(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
        this.lifespan = Integer.MAX_VALUE;
        this.isInvulnerable = TravelersBackpackConfig.SERVER.backpackSettings.invulnerableBackpack.get();
    }

    @Override
    public void tick() {
        if(TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            if(!this.level().isClientSide && !isNoGravity() && wasFloatingUp && getY() < level().getMinBuildHeight()) {
                if(random.nextFloat() > 0.25F) {
                    float ab = random.nextFloat() * 2.0f;
                    float ag = random.nextFloat() * ((float)Math.PI * 2);
                    double n = Mth.cos(ag) * ab;
                    double o = 0.01 + random.nextDouble() * 0.5;
                    double p = Mth.sin(ag) * ab;
                    ((ServerLevel)level()).sendParticles(ParticleTypes.DRAGON_BREATH, position().x() + n * 0.1, position().y() + 0.3, position().z() + p * 0.1, 0, n * 0.01F, o * 0.1F, p * 0.01F, 1.0F);
                }
            }
            if(!isNoGravity()) {
                if(isInWater() || isInLava()) {
                    onInsideBubbleColumn(false);
                    wasFloatingUp = true;
                } else if(wasFloatingUp) {
                    setNoGravity(true);
                    setDeltaMovement(Vec3.ZERO);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean isInWater() {
        if(TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            return getY() < level().getMinBuildHeight() + 1 || super.isInWater();
        }
        return super.isInWater();
    }

    @Override
    public boolean fireImmune() {
        return this.isInvulnerable;
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return this.isInvulnerable;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.isInvulnerable;
    }

    @Override
    protected void onBelowWorld() {
        if(!TravelersBackpackConfig.SERVER.backpackSettings.voidProtection.get()) {
            this.discard();
        }
    }
}