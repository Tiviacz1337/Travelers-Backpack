package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.items.upgrades.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.nbt.CompoundTag;
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

import java.util.List;

@Mixin(Mob.class)
public abstract class MobEntityMixin extends LivingEntity {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At(value = "TAIL"), method = "finalizeSpawn")
    protected void initialize(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, SpawnGroupData spawnData, CompoundTag dataTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if(this instanceof Object && TravelersBackpackConfig.getConfig().world.spawnEntitiesWithBackpack) {
            if((Object)this instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && (TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity))) {
                if(livingEntity.getRandom().nextFloat() < TravelersBackpackConfig.getConfig().world.chance) {
                    boolean isNether = livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = livingEntity.getRandom();
                    ItemStack backpack = isNether ?
                            TravelersBackpackConfig.getRandomCompatibleNetherBackpackEntry(rand).getDefaultInstance() :
                            TravelersBackpackConfig.getRandomCompatibleOverworldBackpackEntry(rand).getDefaultInstance();

                    NbtHelper.set(backpack, ModDataHelper.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());

                    boolean flag = false;
                    if(rand.nextFloat() > 0.5F) {
                        NbtHelper.set(backpack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
                        flag = true;
                    }
                    if(rand.nextFloat() > 0.25F) {
                        NbtHelper.set(backpack, ModDataHelper.COLOR, rand.nextInt());
                    }
                    if(flag) {
                        NbtHelper.set(backpack, ModDataHelper.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                    } else {
                        NbtHelper.set(backpack, ModDataHelper.RENDER_INFO, RenderInfo.EMPTY);
                    }
                    livingEntity.setItemSlot(EquipmentSlot.CHEST, backpack);
                }
            }
        }
    }
}