package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpackold.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At(value = "TAIL"), method = "initialize")
    protected void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, CallbackInfoReturnable<EntityData> cir) {
        if (this instanceof Object && TravelersBackpackConfig.getConfig().world.spawnEntitiesWithBackpack) {
            if ((Object) this instanceof LivingEntity livingEntity && (TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity))) {
                if (world.getRandom().nextFloat() < TravelersBackpackConfig.getConfig().world.chance) {
                    boolean isNether = livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.WITHER_SKELETON;
                    Random rand = world.getRandom();
                    ItemStack backpack = isNether ?
                            TravelersBackpackConfig.getRandomCompatibleNetherBackpackEntry(rand).getDefaultStack() :
                            TravelersBackpackConfig.getRandomCompatibleOverworldBackpackEntry(rand).getDefaultStack();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextBetween(0, DyeColor.values().length - 1)].getId());

                    livingEntity.equipStack(EquipmentSlot.BODY, backpack);
                }
            }
        }
    }
}