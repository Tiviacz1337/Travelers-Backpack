package com.tiviacz.travelersbackpack.inventory.upgrades.jukebox;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.SlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JukeboxUpgrade extends UpgradeBase<JukeboxUpgrade> {
    public ItemStackHandler diskHandler;

    public JukeboxUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> musicDiskContents) {
        super(manager, dataHolderSlot, new Point(66, 46));
        this.diskHandler = createHandler(musicDiskContents);
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack) {
        NbtHelper.set(removedStack, ModDataHelper.IS_PLAYING, false);
        //removedStack.set(ModDataComponents.IS_PLAYING.get(), false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase createWidget(BackpackScreen screen, int x, int y) {
        return new JukeboxWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new SlotItemHandler(diskHandler, 0, x + 7, y + 23) {
            @Override
            public boolean isActive() {
                return isTabOpened();
            }

            @Override
            public boolean mayPlace(ItemStack pStack) {
                return isTabOpened() && !NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot()), ModDataHelper.IS_PLAYING, false); //getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot()).getOrDefault(ModDataComponents.IS_PLAYING.get(), false);
            }

            @Override
            public boolean mayPickup(Player playerIn) {
                return super.mayPickup(playerIn) && !NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot()), ModDataHelper.IS_PLAYING, false);
            }
        });
        return slots;
    }

    public boolean isPlayingRecord() {
        return NbtHelper.getOrDefault(getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot), ModDataHelper.IS_PLAYING, false);
        //return getUpgradeManager().getUpgradesHandler().getStackInSlot(this.dataHolderSlot).getOrDefault(ModDataComponents.IS_PLAYING.get(), false);
    }

    public boolean canPlayRecord() {
        return !isPlayingRecord() && !diskHandler.getStackInSlot(0).isEmpty();
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        NbtHelper.update(dataHolderStack, ModDataHelper.BACKPACK_CONTAINER, 1, index, stack);
        //dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER.get(), new BackpackContainerContents(1), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                ItemStack stack = getUpgradeManager().getUpgradesHandler().getStackInSlot(getDataHolderSlot());

                //Crash prevent for TS (???)
                if(stack.isEmpty()) return;

                setSlotChanged(stack, slot, getStackInSlot(slot));
                getUpgradeManager().getUpgradesHandler().setStackInSlot(getDataHolderSlot(), stack);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return stack.getItem() instanceof RecordItem;
                //return NbtHelper.has(stack, ModDataComponents.JUKEBOX_PLAYABLE);
                //return stack.has(DataComponents.JUKEBOX_PLAYABLE);
            }
        };
    }
}
