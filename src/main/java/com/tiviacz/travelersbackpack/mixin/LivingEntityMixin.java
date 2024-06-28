package com.tiviacz.travelersbackpack.mixin;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModNetwork;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity
{
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
                if(ComponentUtils.isWearingBackpack(player))
                {
                    //Keep backpack on with Keep Inventory game rule
                    if(player.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) return;

                    ItemStack stack = ComponentUtils.getWearingBackpack(player);

                    if(BackpackUtils.onPlayerDrops(player.getWorld(), player, stack))
                    {
                        if(player.getWorld().isClient) return;

                        ItemEntity itemEntity = new ItemEntity(player.getWorld(), player.getX(), player.getY(), player.getZ(), stack);
                        itemEntity.setToDefaultPickupDelay();

                        //PacketDistributor.PLAYER.with((ServerPlayer)player).send(new ClientboundSendMessagePacket(true, new BlockPos(player.blockPosition().getX(), player.blockPosition().getY(), player.blockPosition().getZ())));
                        PacketByteBuf data = PacketByteBufs.create();
                        data.writeBoolean(true);
                        data.writeBlockPos(player.getBlockPos());
                        ServerPlayNetworking.send((ServerPlayerEntity)player, ModNetwork.SEND_MESSAGE_ID, data);

                        LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.getBlockPos().getX() + " Y: " + player.getBlockPos().getY() + " Z: " + player.getBlockPos().getZ());

                        //If Trinkets loaded - handled by Trinkets
                        if(!TravelersBackpack.enableTrinkets())
                        {
                            player.dropStack(stack);
                        }

                        ComponentUtils.getComponent(player).removeWearable();
                        ComponentUtils.sync(player);
                    }


                    /*if(TravelersBackpack.isAnyGraveModInstalled()) return;

                    if(!player.getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
                    {
                        BackpackUtils.onPlayerDeath(player.getWorld(), player, ComponentUtils.getWearingBackpack(player));
                    } */
                }
                //ComponentUtils.sync(player);
            }

            if((Object)this instanceof LivingEntity livingEntity && (TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity)))
            {
                if(ComponentUtils.isWearingBackpack(livingEntity))
                {
                    livingEntity.dropStack(ComponentUtils.getWearingBackpack(livingEntity));
                }
            }
        }
    }
}