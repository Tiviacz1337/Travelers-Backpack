package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    public abstract void playNotifySound(SoundEvent sound, SoundSource source, float volume, float pitch);

    @Shadow
    @Final
    private Abilities abilities;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    private static long nextBackpackCountCheck = 0;
    private static final int BACKPACK_COUNT_CHECK_COOLDOWN = 100;

    /**
     * Ability removal for attribute modifiers
     */
    private static boolean checkAbilitiesForRemoval = true;

    @Inject(at = @At(value = "TAIL"), method = "tick")
    private void abilityTick(CallbackInfo info) {
        if(this instanceof Object) {
            if((Object)this instanceof Player player) {
                if(ComponentUtils.isWearingBackpack(player)) {
                    BackpackWrapper.tick(ComponentUtils.getWearingBackpack(player), player, false);
                }
                if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player))) {
                    if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, ComponentUtils.getWearingBackpack(player)))
                        checkAbilitiesForRemoval = true;
                }
                if(checkAbilitiesForRemoval && !player.level().isClientSide && (!ComponentUtils.isWearingBackpack(player) || !TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities)) {
                    ServerActions.runAbilitiesRemoval(player);
                    checkAbilitiesForRemoval = false;
                }

                //Slowness
                if(TravelersBackpackConfig.getConfig().slownessDebuff.tooManyBackpacksSlowness && !player.isCreative()) {
                    if(nextBackpackCountCheck > player.level().getGameTime()) {
                        return;
                    }

                    nextBackpackCountCheck = player.level().getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

                    AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);

                    if(numberOfBackpacks.get() == 0) return;

                    int maxNumberOfBackpacks = TravelersBackpackConfig.getConfig().slownessDebuff.maxNumberOfBackpacks;

                    if(numberOfBackpacks.get() > maxNumberOfBackpacks) {
                        int numberOfSlownessLevels = Math.min(10, (int)Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.getConfig().slownessDebuff.slownessPerExcessedBackpack));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "attack")
    private void attack(Entity target, CallbackInfo ci) {
        if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities) {
            if(this instanceof Object) {
                if((Object)this instanceof Player player) {
                    BackpackAbilities.beeAbility(player, target);
                    BackpackAbilities.witherAbility(player, target);
                    BackpackAbilities.wardenAbility(player, target);
                }
            }
        }
    }

    @Inject(method = "getFlyingSpeed", at = @At(value = "RETURN"), cancellable = true)
    protected void getFlyingSpeed(CallbackInfoReturnable<Float> cir) {
        if((Object)this instanceof Player player) {
            if(BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.FOX_TRAVELERS_BACKPACK)) {
                if(!this.abilities.flying || this.isPassenger()) {
                    cir.setReturnValue(this.isSprinting() ? 0.025999999F + 0.013F : 0.02F + 0.013F);
                }
            }
        }
    }

    private static AtomicInteger checkBackpacksForSlowness(Player player) {
        AtomicInteger atomic = new AtomicInteger(0);

        for(int i = 0; i < player.getInventory().getNonEquipmentItems().size(); i++) {
            if(player.getInventory().getNonEquipmentItems().get(i).getItem() instanceof TravelersBackpackItem) {
                atomic.incrementAndGet();
            }
        }

        if(player.getOffhandItem().getItem() instanceof TravelersBackpackItem) {
            atomic.incrementAndGet();
        }

        return atomic;
    }
}