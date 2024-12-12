package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

public class DeathHandler {
    public static void registerListeners() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
            if(livingEntity instanceof ServerPlayer player) {
                if(ComponentUtils.isWearingBackpack(player)) {
                    if(TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities && BackpackAbilities.creeperAbility(player)) {
                        return false;
                    }
                }
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if(livingEntity instanceof ServerPlayer player) {
                //Use different placing logic if no integration is loaded
                if(ComponentUtils.isWearingBackpack(player)) {
                    //If integration loaded - just remove backpack from component, rest is handled by integration
                    if(TravelersBackpack.enableIntegration()) {
                        //Create backup
                        if(!player.level().isClientSide)
                            BackpackManager.addBackpack((ServerPlayer)player, ComponentUtils.getWearingBackpack(player));
                        return;
                    }

                    //Continue if no integration detected
                    //Keep backpack on with Keep Inventory game rule
                    if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;

                    ItemStack stack = ComponentUtils.getWearingBackpack(player);

                    if(BackpackDeathHelper.onPlayerDrops(player.level(), player, stack)) {
                        if(player.level().isClientSide) return;

                        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                        itemEntity.setDefaultPickUpDelay();

                        PacketDistributor.sendToPlayer((ServerPlayer)player, new ClientboundSendMessagePacket(true, player.blockPosition()));
                        LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                        player.level().addFreshEntity(itemEntity);
                        //event.getDrops().add(itemEntity);

                        ComponentUtils.getComponent(player).ifPresent(attachment -> {
                            attachment.remove();
                            attachment.synchronise();
                        });
                        return;
                    }
                }
            }
            if(TravelersBackpackConfig.isOverworldEntityTypePossible(livingEntity) || TravelersBackpackConfig.isNetherEntityTypePossible(livingEntity)) {
                if(livingEntity.getItemBySlot(EquipmentSlot.BODY).getItem() instanceof TravelersBackpackItem) {
                    if(!(damageSource.getDirectEntity() instanceof Player)) return;

                    ItemEntity itemEntity = new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getItemBySlot(EquipmentSlot.BODY));
                    livingEntity.level().addFreshEntity(itemEntity);
                    //event.getDrops().add(itemEntity);
                }
            }
            return;
        });
    }
}
