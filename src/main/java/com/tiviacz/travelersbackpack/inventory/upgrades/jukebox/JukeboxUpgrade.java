package com.tiviacz.travelersbackpack.inventory.upgrades.jukebox;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.ArrayList;
import java.util.List;

public class JukeboxUpgrade extends UpgradeBase<JukeboxUpgrade> {
    public BackpackResourceHandler diskHandler;

    public JukeboxUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> musicDiskContents) {
        super(manager, dataHolderSlot, new Point(66, 46));
        this.diskHandler = createHandler(musicDiskContents);
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack) {
        removedStack.set(ModDataComponents.IS_PLAYING, false);
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(Pair.of(x + 7, y + 23));
        return positions;
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new UpgradeSlotItemHandler<>(this, diskHandler, 0, x + 7, y + 23) {
            @Override
            public boolean mayPlace(ItemStack pStack) {
                return isTabOpened() && !getDataHolderStack().getOrDefault(ModDataComponents.IS_PLAYING, false);
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return super.mayPickup(playerIn) && !getDataHolderStack().getOrDefault(ModDataComponents.IS_PLAYING, false);
            }
        });
        return slots;
    }

    public boolean isPlayingRecord() {
        return getDataHolderStack().getOrDefault(ModDataComponents.IS_PLAYING, false);
    }

    public boolean canPlayRecord() {
        return !isPlayingRecord() && !diskHandler.getStackInSlot(0).isEmpty();
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(1), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
    }

    private BackpackResourceHandler createHandler(NonNullList<ItemStack> stacks) {
        return new BackpackResourceHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                return resource.has(DataComponents.JUKEBOX_PLAYABLE);
            }
        };
    }
}