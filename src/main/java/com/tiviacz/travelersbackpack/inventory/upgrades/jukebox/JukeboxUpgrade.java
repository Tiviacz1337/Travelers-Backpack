package com.tiviacz.travelersbackpack.inventory.upgrades.jukebox;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class JukeboxUpgrade extends UpgradeBase<JukeboxUpgrade> {
    public ItemStackHandler diskHandler;

    public JukeboxUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> musicDiskContents) {
        super(manager, dataHolderSlot, new Point(66, 46));
        this.diskHandler = createHandler(musicDiskContents);
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack) {
        NbtHelper.set(removedStack, ModDataHelper.IS_PLAYING, false);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new JukeboxWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
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
                return isTabOpened() && !NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.IS_PLAYING, false);
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return super.mayPickup(playerIn) && !NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.IS_PLAYING, false);
            }
        });
        return slots;
    }

    public boolean isPlayingRecord() {
        return NbtHelper.getOrDefault(getDataHolderStack(), ModDataHelper.IS_PLAYING, false);
    }

    public boolean canPlayRecord() {
        return !isPlayingRecord() && !diskHandler.getStackInSlot(0).isEmpty();
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        NbtHelper.update(dataHolderStack, ModDataHelper.BACKPACK_CONTAINER, 1, index, stack);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return stack.getItem() instanceof RecordItem;
            }
        };
    }
}