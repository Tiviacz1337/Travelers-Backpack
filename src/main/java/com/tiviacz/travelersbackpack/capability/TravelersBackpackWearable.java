package com.tiviacz.travelersbackpack.capability;

import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundSyncCapabilityPacket;
import com.tiviacz.travelersbackpack.network.ClientboundSyncComponentsPacket;
import com.tiviacz.travelersbackpack.util.PacketDistributorHelper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class TravelersBackpackWearable implements ITravelersBackpack, INBTSerializable<CompoundTag> {
    public final Player player;
    public BackpackWrapper backpackWrapper;
    public ItemStack backpack = new ItemStack(Items.AIR, 0);

    public TravelersBackpackWearable(Player player) {
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
            PacketDistributorHelper.sendToPlayersTrackingEntityAndSelf((ServerPlayer)this.player, new ClientboundSyncCapabilityPacket(this.player.getId(), this.backpack, true));
        }
    }

    @Override
    public BackpackWrapper getWrapper() {
        return this.backpackWrapper;
    }

    @Override
    public void synchronise() {
        if(player != null && !player.level().isClientSide) {
            CapabilityUtils.getCapability(this.player).ifPresent(cap -> PacketDistributorHelper.sendToPlayersTrackingEntityAndSelf((ServerPlayer)this.player, new ClientboundSyncCapabilityPacket(this.player.getId(), this.backpack)));
        }
    }

    @Override
    public void synchronise(CompoundTag compound) {
        if(player != null && !player.level().isClientSide) {
            CapabilityUtils.getCapability(this.player).ifPresent(cap -> PacketDistributorHelper.sendToPlayersTrackingEntityAndSelf((ServerPlayer)this.player, new ClientboundSyncComponentsPacket(this.player.getId(), compound)));
        }
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        if(hasBackpack()) {
            ItemStack backpack = getBackpack();
            compound = backpack.save(new CompoundTag());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ItemStack backpack = ItemStack.of(nbt);
        equipBackpack(backpack);
    }
}