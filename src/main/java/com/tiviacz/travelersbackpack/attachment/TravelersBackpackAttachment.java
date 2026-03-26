package com.tiviacz.travelersbackpack.attachment;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncComponentsPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TravelersBackpackAttachment {
    public static Codec<TravelersBackpackAttachment> CODEC = ItemStack.OPTIONAL_CODEC.xmap(TravelersBackpackAttachment::new, TravelersBackpackAttachment::getBackpack);
    public static StreamCodec<RegistryFriendlyByteBuf, TravelersBackpackAttachment> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(TravelersBackpackAttachment::new, TravelersBackpackAttachment::getBackpack);

    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public TravelersBackpackAttachment(ItemStack backpack) {
        this.backpack = backpack;
    }

    public boolean hasBackpack() {
        return this.backpack.getItem() instanceof TravelersBackpackItem;
    }

    public ItemStack getBackpack() {
        return this.backpack;
    }

    public void equipBackpack(ItemStack stack, Player player) {
        this.remove(player);
        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        this.backpack = stack;
        this.backpackWrapper = new BackpackWrapper(this.backpack, Reference.WEARABLE_SCREEN_ID, player, player.level());
        this.backpackWrapper.setBackpackOwner(player);

        //Update client
        synchronise(player);
    }

    public void updateBackpack(ItemStack stack, Player player) {
        if(this.backpackWrapper != null) {
            this.backpack = stack;
            this.backpackWrapper.setBackpackStack(this.backpack);
        } else {
            equipBackpack(stack, player);
        }
    }

    public void applyComponents(DataComponentMap map) {
        if(this.backpackWrapper != null) {
            this.backpack.applyComponents(map);
            this.backpackWrapper.setBackpackStack(this.backpack);
        }
    }

    public void removeWearable() {
        this.backpack = new ItemStack(Items.AIR, 0);
    }

    public void removeWrapper() {
        if(this.backpackWrapper != null) {
            this.backpackWrapper = null;
        }
    }

    public void remove(Player player) {
        removeWearable();
        removeWrapper();

        //Update client to remove old backpack wrapper
        if(player.level() != null && !player.level().isClientSide()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundSyncAttachmentPacket(player.getId(), this.backpack, true));
        }
    }

    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    public void synchronise(Player player) {
        if(player != null && !player.level().isClientSide()) {
            AttachmentUtils.getAttachment(player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundSyncAttachmentPacket(player.getId(), this.backpack)));
        }
    }

    public void synchronise(DataComponentMap map, Player player) {
        if(player != null && !player.level().isClientSide()) {
            AttachmentUtils.getAttachment(player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, new ClientboundSyncComponentsPacket(player.getId(), map)));
        }
    }
}