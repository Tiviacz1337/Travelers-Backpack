package com.tiviacz.travelersbackpack.inventory.upgrades.crafting;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
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
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingUpgrade extends UpgradeBase<CraftingUpgrade> implements IMoveSelector {
    public ItemStacksResourceHandler crafting;
    public ResultContainer resultSlots;
    public CraftingContainerImproved craftSlots;

    public CraftingUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> craftingContents) {
        super(manager, dataHolderSlot, new Point(TravelersBackpack.craftingTweaksLoaded ? 83 : 66, 112));
        this.crafting = createHandler(craftingContents);
    }

    @Override
    public void initializeContainers(BackpackBaseMenu menu, BackpackWrapper wrapper) {
        //Crafting Container
        this.craftSlots = new CraftingContainerImproved(menu, this);
        this.resultSlots = new ResultContainer();
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack, @Nullable Player player) {
        if(removedStack.has(ModDataComponents.BACKPACK_CONTAINER)) {
            NonNullList<ItemStack> retrievedContents = ContainerContentsHelper.getItems(removedStack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY), 9);
            ItemStacksResourceHandler tempHandler = new ItemStacksResourceHandler(retrievedContents);
            BackpackBaseMenu.checkHandlerAndPlaySound(tempHandler, player, StacksHandlerUtils.getSlots(tempHandler));

            //Save
            removedStack.set(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.fromItems(retrievedContents));
        }
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                positions.add(Pair.of(x + 7 + j * 18, y + 23 + i * 18));
            }
        }
        positions.add(Pair.of(x + 25, y + 89));
        return positions;
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
                    public boolean mayPlace(ItemStack pStack) {
                        return true;
                    }
                });
            }
        }

        slots.add(new ResultSlotExt(wrapper, menu.player, this.craftSlots, this.resultSlots, menu.CRAFTING_RESULT, x + 25, y + 89) {
            @Override
            public boolean mayPickup(Player player) {
                return isTabOpened();
            }

        });
        return slots;
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, 9, stack, index));
    }

    private ItemStacksResourceHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStacksResourceHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, StacksHandlerUtils.getStackInSlot(this, slot)));
            }

            @Override
            public boolean isValid(int slot, ItemResource resource) {
                return BackpackSlotItemHandler.isItemValid(resource.toStack());
            }
        };
    }
}