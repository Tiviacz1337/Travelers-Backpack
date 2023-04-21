package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.entity.IEntityTravelersBackpackComponent;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
public abstract class MobEntityMixin extends LivingEntity
{
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(at = @At(value = "TAIL"), method = "initialize")
    protected void initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir)
    {
        if(this instanceof Object && TravelersBackpackConfig.spawnEntitiesWithBackpack)
        {
            if((Object)this instanceof LivingEntity livingEntity && Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType()))
            {
                IEntityTravelersBackpackComponent component = ComponentUtils.getComponent(livingEntity);

                if(!component.hasWearable() && world.getRandom().nextBetween(0, TravelersBackpackConfig.spawnChance) == 0)
                {
                    boolean isNether = livingEntity.getType() == EntityType.PIGLIN || livingEntity.getType() == EntityType.WITHER_SKELETON;
                    Random rand = world.getRandom();
                    ItemStack backpack = isNether ?
                            ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.get(rand.nextBetween(0, ModItems.COMPATIBLE_NETHER_BACKPACK_ENTRIES.size() - 1)).getDefaultStack() :
                            ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.get(rand.nextBetween(0, ModItems.COMPATIBLE_OVERWORLD_BACKPACK_ENTRIES.size() - 1)).getDefaultStack();

                    backpack.getOrCreateNbt().putInt("SleepingBagColor", DyeColor.values()[rand.nextBetween(0, DyeColor.values().length - 1)].getId());

                    component.setWearable(backpack);
                    component.sync();
                }
            }
        }
    }
}