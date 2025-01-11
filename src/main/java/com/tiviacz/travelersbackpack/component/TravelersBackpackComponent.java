package com.tiviacz.travelersbackpack.component;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncComponentsPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributor;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackComponent implements ITravelersBackpack {
    private final String BACKPACK = "Wearable";
    public final Player player;
    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public TravelersBackpackComponent(Player player) {
        this.player = player;
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
        synchronise();
        //if (this.player.level() != null && !this.player.level().isClientSide) {
        //    PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack, true));
        //}
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    @Override
    public void synchronise() {
        ComponentUtils.WEARABLE.sync(this.player);
        //if(player != null && !player.level().isClientSide) {
        //    AttachmentUtils.getAttachment(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncAttachmentPacket(this.player.getId(), this.backpack)));
        //}
    }

    @Override
    public void synchronise(DataComponentMap map) {
        if(player != null && !player.level().isClientSide) {
            ComponentUtils.getComponent(this.player).ifPresent(cap -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(this.player, new ClientboundSyncComponentsPacket(this.player.getId(), map)));
        }
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag, HolderLookup.Provider registryLookup) {
        ItemStack backpack = ItemStack.parseOptional(registryLookup, compoundTag.getCompound(BACKPACK));
        equipBackpack(backpack);
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
        CompoundTag compound = new CompoundTag();
        if(hasBackpack()) {
            ItemStack backpack = getBackpack();
            compound = (CompoundTag)backpack.saveOptional(registryLookup);
        }
        tag.put(BACKPACK, compound);
    }

    public void writeToNbtClient(CompoundTag tag, HolderLookup.Provider registryLookup) {
        CompoundTag compound = new CompoundTag();
        if(hasBackpack()) {
            ItemStack backpackCopy = getBackpack().copy();
            if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
                backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
            }
            if(backpackCopy.has(ModDataComponents.SLOTS)) {
                Slots slots = backpackCopy.get(ModDataComponents.SLOTS);
                List<Pair<Integer, Pair<ItemStack, Boolean>>> smallerMemory = new ArrayList<>();
                List<Pair<Integer, Pair<ItemStack, Boolean>>> memory = slots.memory();
                for(Pair<Integer, Pair<ItemStack, Boolean>> pair : memory) {
                    Integer slot = pair.getFirst();
                    Pair<ItemStack, Boolean> pairInner = pair.getSecond();
                    ItemStack smallerStack = pairInner.getFirst().getItem().getDefaultInstance();
                    smallerMemory.add(Pair.of(slot, Pair.of(smallerStack, pairInner.getSecond())));
                }
                Slots smallerSlots = new Slots(slots.unsortables(), smallerMemory);
                backpackCopy.set(ModDataComponents.SLOTS, smallerSlots);
            }
            compound = (CompoundTag)backpackCopy.saveOptional(registryLookup);
        }
        tag.put(BACKPACK, compound);
    }

    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        CompoundTag tag = new CompoundTag();
        this.writeToNbtClient(tag, buf.registryAccess());
        buf.writeNbt(tag);
    }
}
