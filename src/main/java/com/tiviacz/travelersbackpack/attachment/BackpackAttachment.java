package com.tiviacz.travelersbackpack.attachment;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncComponentsPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.network.PacketDistributor;

public class BackpackAttachment {
    public final Player player;
    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public BackpackAttachment(IAttachmentHolder holder) {
        this.player = (Player)holder;
    }

    public boolean hasBackpack() {
        return this.backpack.getItem() instanceof TravelersBackpackItem;
    }

    public ItemStack getBackpack() {
        return this.backpack;
    }

    public void equipBackpack(ItemStack stack) {
        this.remove();
        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        this.backpack = stack;
        this.backpackWrapper = new BackpackWrapper(this.backpack, Reference.WEARABLE_SCREEN_ID, this.player, this.player.level());
        this.backpackWrapper.setBackpackOwner(this.player);

        //Update client
        synchronise();
    }

    public void updateBackpack(ItemStack stack) {
        if(this.backpackWrapper != null) {
            this.backpack = stack;
            this.backpackWrapper.setBackpackStack(this.backpack);
        } else {
            equipBackpack(stack);
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

    public void remove() {
        removeWearable();
        removeWrapper();

        //Update client to remove old backpack wrapper
        if(this.player.level() != null && !this.player.level().isClientSide()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack, true));
        }
    }

    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    public void synchronise() {
        if(player != null && !player.level().isClientSide()) {
            AttachmentUtils.getAttachment(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack)));
        }
    }

    public void synchronise(DataComponentMap map) {
        if(player != null && !player.level().isClientSide()) {
            AttachmentUtils.getAttachment(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncComponentsPacket(this.player.getId(), map)));
        }
    }

    public static class Serializer implements IAttachmentSerializer<BackpackAttachment> {
        @Override
        public BackpackAttachment read(IAttachmentHolder holder, ValueInput input) {
            BackpackAttachment attachment = new BackpackAttachment(holder);
            ItemStack backpack = input.read("Backpack", ItemStack.OPTIONAL_CODEC).orElseGet(() -> new ItemStack(Items.AIR, 0));
            attachment.equipBackpack(backpack);
            return attachment;
        }

        @Override
        public boolean write(BackpackAttachment attachment, ValueOutput output) {
            output.store("Backpack", ItemStack.OPTIONAL_CODEC, attachment.backpack);
            return true;
        }
    }
}