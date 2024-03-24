package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity
{
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    /**
     * Ability removal for attribute modifiers
     */
    private static boolean checkAbilitiesForRemoval = true;

    @Inject(at = @At(value = "TAIL"), method = "tick")
    private void abilityTick(CallbackInfo info)
    {
        if(this instanceof Object)
        {
            if((Object) this instanceof PlayerEntity player)
            {
                if(TravelersBackpackConfig.enableBackpackAbilities && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
                {
                    TravelersBackpackInventory.abilityTick(player);
                    if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, ComponentUtils.getWearingBackpack(player))) checkAbilitiesForRemoval = true;
                }

                if(checkAbilitiesForRemoval && !player.getWorld().isClient && (!ComponentUtils.isWearingBackpack(player) || !TravelersBackpackConfig.enableBackpackAbilities))
                {
                    BackpackAbilities.ABILITIES.armorAbilityRemovals(player);
                    checkAbilitiesForRemoval = false;
                }

                //Slowness
                if(TravelersBackpackConfig.getConfig().slownessDebuff.tooManyBackpacksSlowness && !player.isCreative())
                {
                    if(nextBackpackCountCheck > player.getWorld().getTime())
                    {
                        return;
                    }

                    nextBackpackCountCheck = player.getWorld().getTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

                    AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);

                    if(numberOfBackpacks.get() == 0) return;

                    int maxNumberOfBackpacks = TravelersBackpackConfig.maxNumberOfBackpacks;

                    if(numberOfBackpacks.get() > maxNumberOfBackpacks)
                    {
                        int numberOfSlownessLevels = Math.min(10, (int) Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.slownessPerExcessedBackpack));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "attack")
    private void attack(Entity target, CallbackInfo ci)
    {
        if(TravelersBackpackConfig.enableBackpackAbilities)
        {
            if(this instanceof Object)
            {
                if((Object) this instanceof PlayerEntity player)
                {
                    BackpackAbilities.beeAbility(player, target);
                }
            }
        }
    }

    private static AtomicInteger checkBackpacksForSlowness(PlayerEntity player)
    {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.getInventory().main.size(); i++)
        {
            if(player.getInventory().main.get(i).getItem() instanceof TravelersBackpackItem)
            {
                atomic.incrementAndGet();
            }
        }

        if(player.getInventory().offHand.get(0).getItem() instanceof TravelersBackpackItem)
        {
            atomic.incrementAndGet();
        }

        return atomic;
    }
}