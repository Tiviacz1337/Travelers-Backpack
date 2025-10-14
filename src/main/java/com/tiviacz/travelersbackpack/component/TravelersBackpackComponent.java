package com.tiviacz.travelersbackpack.component;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.Slots;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.item.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.ComponentProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Dropping required CCA dependency in favor of an attachment system starting from 1.22
 */
@Deprecated(forRemoval = true, since = "1.22")
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
    public void synchronise(DataComponentMap map) {
        if(player != null && !player.level().isClientSide) {
            ComponentUtils.WEARABLE.sync(this.player, (buf, recipient) -> writeComponentPacket(buf, recipient, map));

            //Sync to watching clients
            for(ServerPlayer recipient : PlayerLookup.tracking(this.player)) {
                if(recipient.getId() == this.player.getId()) {
                    continue;
                }
                ComponentUtils.WEARABLE.syncWith(recipient, (ComponentProvider)this.player, (buf, rec) -> writeComponentPacket(buf, rec, map), p -> true);
            }
        }
    }

    /**
     * Saving on server
     *
     * @param valueInput a {@code ValueInput} on which this component's serializable data has been written
     */
    @Override
    public void readData(ValueInput valueInput) {
        ItemStack backpack = valueInput.read(BACKPACK, ItemStack.OPTIONAL_CODEC).orElseGet(() -> new ItemStack(Items.AIR, 0));
        equipBackpack(backpack);
        /*I temStack backpack = ItemStack.parse(registryLookup, compoundTag.getCompoundOrEmpty(BACKPACK)).orElseGet(() -> new ItemStack(Items.AIR, 0));
        equipBackpack(backpack);*/
    }

    @Override
    public void writeData(ValueOutput valueOutput) {
        valueOutput.store(BACKPACK, ItemStack.OPTIONAL_CODEC, this.backpack);

        /*CompoundTag compound = new CompoundTag();
        if(hasBackpack()) {
            ItemStack backpack = getBackpack();
            compound = (CompoundTag)backpack.save(registryLookup);
        }
        tag.put(BACKPACK, compound);*/
    }

    /**
     * Helper methods to write sync packets
     *
     * @param buf
     * @param recipient
     * @param map
     */
    public void writeComponentPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient, DataComponentMap map) {
        buf.writeInt(1);
        ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).encode(buf, map);
    }

    public void writeSyncPacket(ItemStack backpack, RegistryFriendlyByteBuf buf, ServerPlayer recipient, boolean removeData) {
        ItemStack backpackCopy = backpack.copy();
        if(backpackCopy.has(ModDataComponents.BACKPACK_CONTAINER)) {
            backpackCopy.remove(ModDataComponents.BACKPACK_CONTAINER);
        }
        //Client needs only visual representation, no need to send the whole data
        if(backpackCopy.has(ModDataComponents.SLOTS)) {
            Slots slots = backpackCopy.get(ModDataComponents.SLOTS);
            List<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedStacksHeavy = slots.memory();
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
            backpackCopy.set(ModDataComponents.SLOTS, new Slots(slots.unsortables(), reduced));
        }
        buf.writeInt(0);
        buf.writeBoolean(removeData);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, backpackCopy);
    }

    /**
     * Client synchronization packets
     *
     * @param buf       the buffer to write the data to
     * @param recipient the player to which the packet will be sent
     */
    @Override
    public void writeSyncPacket(RegistryFriendlyByteBuf buf, ServerPlayer recipient) {
        this.writeSyncPacket(getBackpack(), buf, recipient, false);
    }

    @Override
    public void applySyncPacket(RegistryFriendlyByteBuf buf) {
        int type = buf.readInt();
        if(type == 0) {
            boolean removeData = buf.readBoolean();
            ItemStack backpackStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            if(removeData) {
                remove();
            } else {
                updateBackpack(backpackStack);
            }

        } else {
            DataComponentMap map = ByteBufCodecs.fromCodecWithRegistries(DataComponentMap.CODEC).decode(buf);
            if(map != null) {
                applyComponents(map);
            }
        }
    }
}
