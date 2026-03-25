package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.attachment.TravelersBackpackAttachment;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModAttachmentTypes;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
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
                if(AttachmentUtils.isWearingBackpack(player)) {
                    BackpackWrapper.tick(AttachmentUtils.getWearingBackpack(player), player, false);
                }
                if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(player))) {
                    if(!checkAbilitiesForRemoval && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, AttachmentUtils.getWearingBackpack(player)))
                        checkAbilitiesForRemoval = true;
                }
                if(checkAbilitiesForRemoval && !player.level().isClientSide() && (!AttachmentUtils.isWearingBackpack(player) || !TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get())) {
                    ServerActions.runAbilitiesRemoval(player);
                    checkAbilitiesForRemoval = false;
                }

                //Slowness
                if(TravelersBackpackConfig.SERVER.slownessDebuff.tooManyBackpacksSlowness.get() && !player.isCreative()) {
                    if(nextBackpackCountCheck > player.level().getGameTime()) {
                        return;
                    }

                    nextBackpackCountCheck = player.level().getGameTime() + BACKPACK_COUNT_CHECK_COOLDOWN;

                    AtomicInteger numberOfBackpacks = checkBackpacksForSlowness(player);

                    if(numberOfBackpacks.get() == 0) return;

                    int maxNumberOfBackpacks = TravelersBackpackConfig.SERVER.slownessDebuff.maxNumberOfBackpacks.get();

                    if(numberOfBackpacks.get() > maxNumberOfBackpacks) {
                        int numberOfSlownessLevels = Math.min(10, (int)Math.ceil((numberOfBackpacks.get() - maxNumberOfBackpacks) * TravelersBackpackConfig.SERVER.slownessDebuff.slownessPerExcessedBackpack.get()));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, BACKPACK_COUNT_CHECK_COOLDOWN * 2, numberOfSlownessLevels - 1, false, false));
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "attack")
    private void attack(Entity target, CallbackInfo ci) {
        if(TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get()) {
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

    //Transfer method for cardinal components data
    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void fromTag(ValueInput view, CallbackInfo ci) {
        Optional<ValueInput> cca = view.child("cardinal_components");
        cca.ifPresent(nested -> {
            Optional<ValueInput> backpack = nested.child("travelersbackpack:travelersbackpack");
            backpack.ifPresent(nestedBackpack -> {
                ItemStack stack = nestedBackpack.read("Wearable", ItemStack.OPTIONAL_CODEC).orElseGet(() -> new ItemStack(Items.AIR, 0));
                this.setAttached(ModAttachmentTypes.TRAVELERS_BACKPACK, new TravelersBackpackAttachment(stack));
            });
        });
    }
}