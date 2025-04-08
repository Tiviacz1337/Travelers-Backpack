package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.CraftingSlot;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CraftingUpgrade extends UpgradeBase<CraftingUpgrade> implements IMoveSelector {
    public ItemStackHandler crafting;
    public ResultContainer resultSlots;
    public CraftingContainerImproved craftSlots;

    public CraftingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> craftingContents) {
        super(manager, dataHolderSlot, new Point(66, 112));
        this.crafting = createHandler(craftingContents);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new CraftingWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public void initializeContainers(BackpackBaseMenu menu, BackpackWrapper wrapper) {
        //Crafting Container
        this.craftSlots = new CraftingContainerImproved(menu, this);
        this.resultSlots = new ResultContainer();
    }

    @Override
    public List<Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();

        menu.CRAFTING_GRID_START = menu.slots.size();
        menu.CRAFTING_RESULT = menu.slots.size() + 9;

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                slots.add(new CraftingSlot(this.craftSlots, j + i * 3, x + 7 + j * 18, y + 23 + i * 18) {
                    @Override
                    public boolean isActive() {
                        return isTabOpened();
                    }

                    @Override
                    public boolean mayPlace(ItemStack pStack) {
                        return true;
                    }

                    @Override
                    public boolean mayPickup(Player pPlayer) {
                        return true;
                    }
                });
            }
        }

        slots.add(new ResultSlotExt(wrapper, menu.player, this.craftSlots, this.resultSlots, 0, x + 25, y + 89) {
            @Override
            public boolean mayPickup(Player player) {
                return isTabOpened();
            }

            @Override
            public boolean isActive() {
                return isTabOpened();
            }

        });
        return slots;
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(9), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return BackpackSlotItemHandler.isItemValid(stack);
            }
        };
    }
}