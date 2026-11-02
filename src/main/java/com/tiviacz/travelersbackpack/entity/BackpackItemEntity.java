package com.tiviacz.travelersbackpack.entity;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BackpackItemEntity extends ItemEntity {
    public boolean wasFloatingUp = false;
    public boolean fireResistant;
    public boolean explosionResistant;
    public boolean invulnerableBackpack;

    //Fix for item entity floating
    public int backpackAge;

    public BackpackItemEntity(EntityType<? extends ItemEntity> entityType, Level level) {
        super(entityType, level);
        boolean neverDespawn = TravelersBackpackConfig.getConfig().backpackSettings.neverDespawn;
        if(neverDespawn) {
            this.setUnlimitedLifetime();
        }
        this.fireResistant = TravelersBackpackConfig.getConfig().backpackSettings.fireResistant;
        this.explosionResistant = TravelersBackpackConfig.getConfig().backpackSettings.explosionResistant;
        this.invulnerableBackpack = TravelersBackpackConfig.getConfig().backpackSettings.invulnerableBackpack;
        this.backpackAge = 0;
    }

    @Override
    public void tick() {
        if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
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
        this.backpackAge++;
        super.tick();
    }

    @Override
    public boolean isInWater() {
        if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
            return getY() < level().getMinBuildHeight() + 1 || super.isInWater();
        }
        return super.isInWater();
    }

    @Override
    public boolean fireImmune() {
        return this.fireResistant;
    }

    @Override
    public boolean ignoreExplosion() {
        return this.explosionResistant;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.invulnerableBackpack;
    }

    @Override
    protected void onBelowWorld() {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
            this.discard();
        }
    }

    @Override
    public int getAge() {
        return this.backpackAge;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("BackpackAge", this.backpackAge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.backpackAge = compound.getInt("BackpackAge");
    }
}