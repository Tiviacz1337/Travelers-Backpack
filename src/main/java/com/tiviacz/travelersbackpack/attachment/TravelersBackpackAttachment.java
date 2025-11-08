package com.tiviacz.travelersbackpack.attachment;

import com.mojang.serialization.Codec;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Preparation for removal of the required Cardinal Components dependency.
 * This class is not used in version 1.21.x.
 * Use the component TravelersBackpackComponent instead.
 * Currently, this class is only used for data transfer purposes.
 */
public class TravelersBackpackAttachment implements ITravelersBackpackAttachment {
    public static Codec<TravelersBackpackAttachment> CODEC = ItemStack.OPTIONAL_CODEC.xmap(TravelersBackpackAttachment::new, TravelersBackpackAttachment::getBackpack);
    public static StreamCodec<RegistryFriendlyByteBuf, TravelersBackpackAttachment> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.map(TravelersBackpackAttachment::new, TravelersBackpackAttachment::getBackpack);
    public static TravelersBackpackAttachment DEFAULT = new TravelersBackpackAttachment(new ItemStack(Items.AIR, 0));

    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public TravelersBackpackAttachment(ItemStack backpack) {
        this.backpack = backpack;
    }

    @Override
    public boolean hasBackpack() {
        return this.backpack.getItem() instanceof TravelersBackpackItem;
    }

    public ItemStack getBackpack() {
        return this.backpack;
    }

    @Override
    public void equipBackpack(ItemStack stack, Player player) {
        this.remove(player);
        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        this.backpack = stack;
        this.backpackWrapper = new BackpackWrapper(this.backpack, Reference.WEARABLE_SCREEN_ID, player.registryAccess(), player, player.level());
        this.backpackWrapper.setBackpackOwner(player);

        //Update client
        synchronise(player);
    }

    @Override
    public void updateBackpack(ItemStack stack, Player player) {
        if(this.backpackWrapper != null) {
            this.backpack = stack;
            this.backpackWrapper.setBackpackStack(this.backpack);
        } else {
            equipBackpack(stack, player);
        }
    }

    @Override
    public void applyComponents(DataComponentMap map) {
        if(this.backpackWrapper != null) {
            this.backpack.applyComponents(map);
            this.backpackWrapper.setBackpackStack(this.backpack);
        }
    }

    @Override
    public void removeWearable() {
        this.backpack = new ItemStack(Items.AIR, 0);
    }

    @Override
    public void removeWrapper() {
        if(this.backpackWrapper != null) {
            this.backpackWrapper = null;
        }
    }

    @Override
    public void remove(Player player) {
        removeWearable();
        removeWrapper();

        //Update client to remove old backpack wrapper
        /*if(this.player.level() != null && !this.player.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack, true));
        }*/
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    @Override
    public void synchronise(Player player) {

    }

    @Override
    public void synchronise(DataComponentMap map, Player player) {

    }
}