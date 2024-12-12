package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "TAIL"), method = "finalizeSpawn")
    protected void initialize(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if(this instanceof Object && TravelersBackpackConfig.getConfig().world.spawnEntitiesWithBackpack) {
            if((Object)this instanceof LivingEntity livingEntity && (TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity))) {
                if(level.getRandom().nextFloat() < TravelersBackpackConfig.getConfig().world.chance) {
                    boolean isNether = livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = level.getRandom();
                    ItemStack backpack = isNether ?
                            TravelersBackpackConfig.getRandomCompatibleNetherBackpackEntry(rand).getDefaultInstance() :
                            TravelersBackpackConfig.getRandomCompatibleOverworldBackpackEntry(rand).getDefaultInstance();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextInt(0, DyeColor.values().length - 1)].getId());

                    livingEntity.setItemSlot(EquipmentSlot.BODY, backpack);
                }
            }
        }
    }
}