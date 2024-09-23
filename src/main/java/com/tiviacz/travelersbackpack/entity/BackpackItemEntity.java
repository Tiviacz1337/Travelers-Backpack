package com.tiviacz.travelersbackpack.entity;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class BackpackItemEntity extends ItemEntity
{
    public boolean wasFloatingUp = false;
    public boolean isInvulnerable;

    public BackpackItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
        super(entityType, world);
        this.age = Integer.MAX_VALUE;
        this.isInvulnerable = TravelersBackpackConfig.getConfig().backpackSettings.invulnerableBackpack;
    }

    @Override
    public void tick() {
        if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
            if(!this.getWorld().isClient && !hasNoGravity() && wasFloatingUp && getY() < getWorld().getBottomY()) {
                if(random.nextFloat() > 0.25F) {
                    float ab = random.nextFloat() * 2.0f;
                    float ag = random.nextFloat() * ((float) Math.PI * 2);
                    double n = MathHelper.cos(ag) * ab;
                    double o = 0.01 + random.nextDouble() * 0.5;
                    double p = MathHelper.sin(ag) * ab;
                    ((ServerWorld)getWorld()).spawnParticles(ParticleTypes.DRAGON_BREATH, getPos().getX() + n * 0.1, getPos().getY() + 0.3, getPos().getZ() + p * 0.1, 0,  n * 0.01F, o * 0.1F, p * 0.01F, 1.0F);
                }
            }
            if (!hasNoGravity()) {
                if (isSubmergedInWater() || isInLava()) {
                    onBubbleColumnCollision(false);
                    wasFloatingUp = true;
                } else if (wasFloatingUp) {
                    setNoGravity(true);
                    setVelocity(Vec3d.ZERO);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean isSubmergedInWater() {
        if(TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
            return getY() < getWorld().getBottomY() + 1 || super.isSubmergedInWater();
        }
        return super.isSubmergedInWater();
    }

    @Override
    public boolean isFireImmune() {
        return this.isInvulnerable;
    }

    @Override
    public boolean isImmuneToExplosion() {
        return this.isInvulnerable;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return this.isInvulnerable;
    }

    @Override
    protected void tickInVoid() {
        if(!TravelersBackpackConfig.getConfig().backpackSettings.voidProtection) {
            this.discard();
        }
    }
}