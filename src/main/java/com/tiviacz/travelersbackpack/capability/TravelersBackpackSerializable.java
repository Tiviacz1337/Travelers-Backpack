package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncAttachmentPacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncComponentsPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.UnknownNullability;

public class TravelersBackpackSerializable implements ITravelersBackpack, INBTSerializable<CompoundTag> {
    public final Player player;
    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public TravelersBackpackSerializable(IAttachmentHolder holder) {
        this.player = (Player)holder;
    }

    @Override
    public boolean hasBackpack() {
        return this.backpack.getItem() instanceof TravelersBackpackItem;
    }

    @Override
    public ItemStack getBackpack() {
        return this.backpack;
    }

    @Override
    public void equipBackpack(ItemStack stack) {
        this.remove();
        if(!(stack.getItem() instanceof TravelersBackpackItem)) return;

        this.backpack = stack;
        this.backpackWrapper = new BackpackWrapper(this.backpack, Reference.WEARABLE_SCREEN_ID, this.player.registryAccess(), this.player, this.player.level());
        this.backpackWrapper.setBackpackOwner(this.player);

        //Update client
        synchronise();
    }

    @Override
    public void updateBackpack(ItemStack stack) {
        if(this.backpackWrapper != null) {
            this.backpack = stack;
            this.backpackWrapper.setBackpackStack(this.backpack);
        } else {
            equipBackpack(stack);
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
    public void remove() {
        removeWearable();
        removeWrapper();

        //Update client to remove old backpack wrapper
        if(this.player.level() != null && !this.player.level().isClientSide) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack, true));
        }
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    @Override
    public void synchronise() {
        if(player != null && !player.level().isClientSide) {
            AttachmentUtils.getAttachment(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack)));
        }
    }

    @Override
    public void synchronise(DataComponentMap map) {
        if(player != null && !player.level().isClientSide) {
            AttachmentUtils.getAttachment(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncComponentsPacket(this.player.getId(), map)));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compound = new CompoundTag();
        if(hasBackpack()) {
            ItemStack backpack = getBackpack();
            compound = (CompoundTag)backpack.saveOptional(provider);
        }
        return compound;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ItemStack backpack = ItemStack.parseOptional(provider, nbt);
        equipBackpack(backpack);
    }
}