package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSendMessagePacket;
import com.tiviacz.travelersbackpack.util.BackpackDeathHelper;
import com.tiviacz.travelersbackpack.util.LogHelper;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;

public class DeathHandler {
    public static void registerListeners() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((livingEntity, damageSource, damageAmount) -> {
            if(livingEntity instanceof ServerPlayer player) {
                if(BackpackAbilities.ABILITIES.checkBackpack(player, ModItems.CREEPER_TRAVELERS_BACKPACK)) {
                    return !BackpackAbilities.creeperAbility(player);
                }
            }
            return true;
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if(livingEntity instanceof ServerPlayer player) {
                if(AttachmentUtils.isWearingBackpack(player)) {
                    //If integration detected, then do not use this logic, it will be handled by the integration/added to the grave
                    if(TravelersBackpack.enableIntegration()) {
                        return;
                    }

                    //Keep backpack on with Keep Inventory game rule
                    if(player.level() instanceof ServerLevel serverLevel) {
                        if(serverLevel.getGameRules().get(GameRules.KEEP_INVENTORY)) return;
                    }

                    ItemStack stack = AttachmentUtils.getWearingBackpack(player);

                    if(BackpackDeathHelper.onPlayerDrops(player.level(), player, stack)) {
                        if(player.level().isClientSide()) return;

                        ItemEntity itemEntity = new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), stack);
                        itemEntity.setDefaultPickUpDelay();

                        PacketDistributor.sendToPlayer(player, new ClientboundSendMessagePacket(true, player.blockPosition()));
                        LogHelper.info("There's no space for backpack. Dropping backpack item at" + " X: " + player.blockPosition().getX() + " Y: " + player.getY() + " Z: " + player.blockPosition().getZ());

                        player.level().addFreshEntity(itemEntity);

                        AttachmentUtils.getAttachment(player).ifPresent(attachment -> {
                            attachment.remove(player);
                            attachment.synchronise(player);
                        });
                        return;
                    }
                }
            }
            if(Reference.ALLOWED_TYPE_ENTRIES.contains(livingEntity.getType())) {
                if(livingEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof TravelersBackpackItem) {
                    if(!(damageSource.getDirectEntity() instanceof Player)) return;

                    ItemEntity itemEntity = new ItemEntity(livingEntity.level(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), livingEntity.getItemBySlot(EquipmentSlot.CHEST));
                    livingEntity.level().addFreshEntity(itemEntity);
                }
            }
        });
    }
}
