package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.upgrade.TanksUpgradeItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ItemContainerContents;
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
    protected void initialize(ServerLevelAccessor level, DifficultyInstance difficulty, EntitySpawnReason spawnType, SpawnGroupData spawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir) {
        if(TravelersBackpackConfig.serverSpec.isLoaded() && TravelersBackpackConfig.SERVER.world.spawnEntitiesWithBackpack.get()) {
            if((Object)this instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && !livingEntity.isBaby() && Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType())) {
                if(level.getRandom().nextFloat() < TravelersBackpackConfig.SERVER.world.chance.get()) {
                    boolean isNether = livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.WITHER_SKELETON;
                    RandomSource rand = level.getRandom();
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextIntBetweenInclusive(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultInstance();

                    backpack.set(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.values()[rand.nextIntBetweenInclusive(0, DyeColor.values().length - 1)].getId());
                    boolean flag = false;
                    if(rand.nextFloat() > 0.5F) {
                        backpack.set(ModDataComponents.STARTER_UPGRADES, ItemContainerContents.fromItems(List.of(ModItems.TANKS_UPGRADE.getDefaultInstance())));
                        flag = true;
                    }
                    if(rand.nextFloat() > 0.25F) {
                        backpack.set(DataComponents.DYED_COLOR, new DyedItemColor(rand.nextInt()));
                    }
                    if(flag) {
                        backpack.set(ModDataComponents.RENDER_INFO, TanksUpgradeItem.writeToRenderData());
                    } else {
                        backpack.set(ModDataComponents.RENDER_INFO, RenderInfo.EMPTY);
                    }
                    livingEntity.setItemSlot(EquipmentSlot.CHEST, backpack);
                }
            }
        }
    }
}