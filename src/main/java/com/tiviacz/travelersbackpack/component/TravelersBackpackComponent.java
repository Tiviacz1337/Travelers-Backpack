package com.tiviacz.travelersbackpack.component;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import dev.onyxstudios.cca.api.v3.component.ComponentProvider;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
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
        this.backpackWrapper = new BackpackWrapper(this.backpack, Reference.WEARABLE_SCREEN_ID, this.player, this.player.level());
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
    public void applyComponents(CompoundTag compound) {
        if(this.backpackWrapper != null) {
            for(String key : compound.getAllKeys()) {
                this.backpack.getOrCreateTag().put(key, compound.get(key));
            }
            this.backpackWrapper.setBackpackStack(this.backpack);
        }
    }

    @Override
    public void removeBackpack() {
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
        removeBackpack();
        removeWrapper();

        //Update client to remove old backpack wrapper
        if(this.player.level() != null && !this.player.level().isClientSide) {
            ComponentUtils.WEARABLE.sync(this.player, (buf, recipient) -> writeSyncPacket(getBackpack(), buf, recipient, true));

            //Sync to watching clients
            for(ServerPlayer recipient : PlayerLookup.tracking(this.player)) {
                if(recipient.getId() == this.player.getId()) {
                    continue;
                }
                ComponentUtils.WEARABLE.syncWith(recipient, (ComponentProvider)this.player, (buf, rec) -> writeSyncPacket(getBackpack(), buf, rec, true), p -> true);
            }
        }
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    @Override
    public void synchronise() {
        if(player != null && !player.level().isClientSide) {
            ComponentUtils.WEARABLE.sync(this.player);

            //Sync to watching clients
            for(ServerPlayer recipient : PlayerLookup.tracking(this.player)) {
                if(recipient.getId() == this.player.getId()) {
                    continue;
                }
                ComponentUtils.WEARABLE.syncWith(recipient, (ComponentProvider)this.player, (buf, rec) -> writeSyncPacket(buf, rec), p -> true);
            }
        }
    }

    @Override
    public void synchronise(CompoundTag compound) {
        if(player != null && !player.level().isClientSide) {
            ComponentUtils.WEARABLE.sync(this.player, (buf, recipient) -> writeComponentPacket(buf, recipient, compound));

            //Sync to watching clients
            for(ServerPlayer recipient : PlayerLookup.tracking(this.player)) {
                if(recipient.getId() == this.player.getId()) {
                    continue;
                }
                ComponentUtils.WEARABLE.syncWith(recipient, (ComponentProvider)this.player, (buf, rec) -> writeComponentPacket(buf, rec, compound), p -> true);
            }
        }
    }

    /**
     * Saving on server
     *
     * @param compoundTag a {@code NbtCompound} on which this component's serializable data has been written
     */
    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        ItemStack backpack = ItemStack.of(compoundTag);
        equipBackpack(backpack);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        ItemStack backpack = getBackpack(); //Keeping it as it was to prevent backpack disappear
        backpack.save(tag);
    }

    /**
     * Helper methods to write sync packets
     *
     * @param buf
     * @param recipient
     * @param map
     */
    public void writeComponentPacket(FriendlyByteBuf buf, ServerPlayer recipient, CompoundTag map) {
        buf.writeInt(1);
        buf.writeNbt(map);
    }

    public void writeSyncPacket(ItemStack backpack, FriendlyByteBuf buf, ServerPlayer recipient, boolean removeData) {
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.hasTag()) {
            backpackCopy.getTag().remove(ModDataHelper.BACKPACK_CONTAINER);
        }
        //Client needs only visual representation, no need to send the whole data
        if(backpackCopy.hasTag() && backpackCopy.getTag().contains(ModDataHelper.MEMORY_SLOTS)) {
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedStacksHeavy = NbtHelper.get(backpackCopy, ModDataHelper.MEMORY_SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> reduced = new ArrayList<>();

            for(Pair<Integer, Pair<ItemStack, Boolean>> outerPair : memorizedStacksHeavy) {
                int index = outerPair.getFirst();
                ItemStack innerStack = outerPair.getSecond().getFirst().copy();
                boolean matchComponents = outerPair.getSecond().getSecond();
                if(matchComponents) {
                    innerStack = new ItemStack(innerStack.getItem(), innerStack.getCount());
                }
                if(innerStack.isEmpty()) {
                    continue;
                }
                reduced.add(Pair.of(index, Pair.of(innerStack, matchComponents)));
            }
            NbtHelper.set(backpack, ModDataHelper.MEMORY_SLOTS, reduced);
        }
        buf.writeInt(0);
        buf.writeBoolean(removeData);
        buf.writeItem(backpackCopy);
    }

    /**
     * Client synchronization packets
     *
     * @param buf       the buffer to write the data to
     * @param recipient the player to which the packet will be sent
     */
    @Override
    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer recipient) {
        this.writeSyncPacket(getBackpack(), buf, recipient, false);
    }

    @Override
    public void applySyncPacket(FriendlyByteBuf buf) {
        int type = buf.readInt();
        if(type == 0) {
            boolean removeData = buf.readBoolean();
            ItemStack backpackStack = buf.readItem();
            if(removeData) {
                remove();
            } else {
                updateBackpack(backpackStack);
            }

        } else {
            CompoundTag map = buf.readNbt();
            if(map != null) {
                applyComponents(map);
            }
        }
    }
}