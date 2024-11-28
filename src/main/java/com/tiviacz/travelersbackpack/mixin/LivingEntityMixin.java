package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.SendMessagePacket;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
    @Shadow public abstract LivingEntity getLastAttacker();

    public LivingEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Inject(at = @At(value = "HEAD"), method = "tryUseTotem", cancellable = true)
    private void tryUseTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir)
    {
        if(this instanceof Object)
        {
            if((Object)this instanceof PlayerEntity player)
            {
                if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities && BackpackAbilities.creeperAbility(player))
                {
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "dropInventory")
    private void onDeath(CallbackInfo info)
    {
        if(this instanceof Object)
        {
            if((Object)this instanceof PlayerEntity player)
            {
                //Use different placing logic if no integration is loaded
                if(ComponentUtils.isWearingBackpack(player))
                {
                    //If integration loaded - just remove backpack from component, rest is handled by integration
                    if(TravelersBackpack.enableIntegration())
                    {
                        //Create backup
                        if(!player.getWorld().isClient) BackpackManager.addBackpack((ServerPlayerEntity)player, ComponentUtils.getWearingBackpack(player));

                        ComponentUtils.getComponent(player).removeWearable();
                        ComponentUtils.sync(player);
                        return;
                    }

                    //Continue if no integration detected
                    //Keep backpack on with Keep Inventory game rule
                    if(player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;

                    ItemStack stack = ComponentUtils.getWearingBackpack(player);

                    if(BackpackUtils.onPlayerDrops(player.getWorld(), player, stack))
                    {
                        if(player.getWorld().isClient) return;

                        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), stack);
                        itemEntity.setToDefaultPickupDelay();

                        ServerPlayNetworking.send((ServerPlayerEntity)player, new SendMessagePacket(true, player.getBlockPos()));
                        LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.getBlockPos().getX() + " Y: " + player.getBlockPos().getY() + " Z: " + player.getBlockPos().getZ());
                        player.dropStack(stack);

                        ComponentUtils.getComponent(player).removeWearable();
                        ComponentUtils.sync(player);
                    }
                }
            }

            if((Object)this instanceof LivingEntity livingEntity && (TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity)))
            {
                if (livingEntity.getEquippedStack(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
                    if (!(getLastAttacker() instanceof PlayerEntity)) return;

                    livingEntity.dropStack(livingEntity.getEquippedStack(EquipmentSlot.BODY));
                }
            }
        }
    }
}