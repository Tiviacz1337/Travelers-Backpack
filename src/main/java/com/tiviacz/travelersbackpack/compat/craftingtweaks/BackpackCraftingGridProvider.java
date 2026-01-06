package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import net.blay09.mods.craftingtweaks.api.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BackpackCraftingGridProvider implements CraftingGridProvider {
    @Override
    public String getModId() {
        return "travelersbackpack";
    }

    @Override
    public boolean requiresServerSide() {
        return true;
    }

    @Override
    public boolean handles(AbstractContainerMenu menu) {
        return menu instanceof BackpackBaseMenu;
    }

    @Override
    public void buildCraftingGrids(CraftingGridBuilder craftingGridBuilder, AbstractContainerMenu abstractContainerMenu) {
        if(abstractContainerMenu instanceof BackpackBaseMenu backpackMenu) {
            craftingGridBuilder.addGrid(backpackMenu.CRAFTING_GRID_START, 9).setButtonAlignment(ButtonAlignment.RIGHT)
                    .clearHandler(this::clearGrid).rotateHandler(this::rotateGrid)
                    .balanceHandler(new BackpackBalanceGridHandler()).transferHandler(new BackpackTransferGridHandler())
                    .hideAllTweakButtons();
        }
    }

    public void clearGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, boolean forced) {
        if(!(menu instanceof BackpackBaseMenu backpackMenu)) {
            return;
        }
        Optional<CraftingUpgrade> upgrade = backpackMenu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
        if(upgrade.isEmpty() || !upgrade.get().isTabOpened()) {
            return;
        }

        Container craftMatrix = grid.getCraftingMatrix(player, menu);
        if(craftMatrix != null) {
            int start = grid.getGridStartSlot(player, menu);
            int size = grid.getGridSize(player, menu);

            for(int i = start; i < start + size; ++i) {
                int slotIndex = menu.slots.get(i).getContainerSlot();
                ItemStack itemStack = craftMatrix.getItem(slotIndex);
                if(!itemStack.isEmpty()) {
                    ItemStack returnStack = itemStack.copy();
                    player.getInventory().add(returnStack);
                    craftMatrix.setItem(slotIndex, returnStack.getCount() == 0 ? ItemStack.EMPTY : returnStack);
                    if(returnStack.getCount() > 0 && forced) {
                        player.drop(returnStack, false);
                        craftMatrix.setItem(slotIndex, ItemStack.EMPTY);
                    }
                }
            }
            menu.broadcastChanges();
        }
    }

    protected boolean ignoresSlotId(int slotId) {
        return slotId == 4;
    }

    protected int rotateSlotId(int slotId, boolean counterClockwise) {
        if(!counterClockwise) {
            switch(slotId) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 5;
                case 3:
                    return 0;
                case 4:
                default:
                    break;
                case 5:
                    return 8;
                case 6:
                    return 3;
                case 7:
                    return 6;
                case 8:
                    return 7;
            }
        } else {
            switch(slotId) {
                case 0:
                    return 3;
                case 1:
                    return 0;
                case 2:
                    return 1;
                case 3:
                    return 6;
                case 4:
                default:
                    break;
                case 5:
                    return 2;
                case 6:
                    return 7;
                case 7:
                    return 8;
                case 8:
                    return 5;
            }
        }

        return 0;
    }

    public void rotateGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, boolean reverse) {
        if(!(menu instanceof BackpackBaseMenu backpackMenu)) {
            return;
        }
        Optional<CraftingUpgrade> upgrade = backpackMenu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
        if(upgrade.isEmpty() || !upgrade.get().isTabOpened()) {
            return;
        }

        Container craftMatrix = grid.getCraftingMatrix(player, menu);
        if(craftMatrix != null) {
            int start = grid.getGridStartSlot(player, menu);
            int size = grid.getGridSize(player, menu);
            Container matrixClone = new SimpleContainer(size);

            for(int i = 0; i < size; ++i) {
                int slotIndex = menu.slots.get(start + i).getContainerSlot();
                matrixClone.setItem(i, craftMatrix.getItem(slotIndex));
            }

            for(int i = 0; i < size; ++i) {
                if(!this.ignoresSlotId(i)) {
                    int slotIndex = menu.slots.get(start + this.rotateSlotId(i, reverse)).getContainerSlot();
                    craftMatrix.setItem(slotIndex, matrixClone.getItem(i));
                }
            }

            menu.broadcastChanges();
        }
    }

    public static class BackpackBalanceGridHandler implements GridBalanceHandler<AbstractContainerMenu> {

        @Override
        public void balanceGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
            if(!(menu instanceof BackpackBaseMenu backpackMenu)) {
                return;
            }
            Optional<CraftingUpgrade> upgrade = backpackMenu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
            if(upgrade.isEmpty() || !upgrade.get().isTabOpened()) {
                return;
            }

            Container craftMatrix = grid.getCraftingMatrix(player, menu);
            if(craftMatrix != null) {
                ArrayListMultimap<String, Integer> itemMap = ArrayListMultimap.create();
                Multiset<String> itemCount = HashMultiset.create();
                int start = grid.getGridStartSlot(player, menu);
                int size = grid.getGridSize(player, menu);

                for(int i = start; i < start + size; ++i) {
                    int slotIndex = menu.slots.get(i).getContainerSlot();
                    ItemStack itemStack = craftMatrix.getItem(slotIndex);
                    if(!itemStack.isEmpty() && itemStack.getMaxStackSize() > 1) {
                        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
                        String key = Objects.toString(registryName);
                        if(itemStack.getTag() != null) {
                            key = key + "@" + itemStack.getTag();
                        }

                        itemMap.put(key, slotIndex);
                        itemCount.add(key, itemStack.getCount());
                    }
                }

                for(String key : itemMap.keySet()) {
                    List<Integer> balanceList = itemMap.get(key);
                    int totalCount = itemCount.count(key);
                    int countPerStack = totalCount / balanceList.size();
                    int restCount = totalCount % balanceList.size();

                    for(int index : balanceList) {
                        ItemStack stack = craftMatrix.getItem(index);
                        stack.setCount(countPerStack);
                        craftMatrix.setItem(index, stack);
                    }

                    int idx = 0;

                    while(restCount > 0) {
                        int index = balanceList.get(idx);
                        ItemStack stack = craftMatrix.getItem(index);
                        if(stack.getCount() < stack.getMaxStackSize()) {
                            ItemStack copy = stack.copy();
                            copy.grow(1);
                            craftMatrix.setItem(index, copy);
                            //itemStack.grow(1);
                            --restCount;
                        }

                        ++idx;
                        if(idx >= balanceList.size()) {
                            idx = 0;
                        }
                    }
                }

                menu.broadcastChanges();
            }
        }

        @Override
        public void spreadGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu) {
            if(!(menu instanceof BackpackBaseMenu backpackMenu)) {
                return;
            }
            Optional<CraftingUpgrade> upgrade = backpackMenu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
            if(upgrade.isEmpty() || !upgrade.get().isTabOpened()) {
                return;
            }

            Container craftMatrix = grid.getCraftingMatrix(player, menu);
            if(craftMatrix != null) {
                boolean emptyBiggestSlot;
                do {
                    ItemStack biggestSlotStack = null;
                    int biggestSlotStackSlot = 0;
                    int biggestSlotSize = 1;
                    int start = grid.getGridStartSlot(player, menu);
                    int size = grid.getGridSize(player, menu);

                    for(int i = start; i < start + size; ++i) {
                        int slotIndex = menu.slots.get(i).getContainerSlot();
                        ItemStack itemStack = craftMatrix.getItem(slotIndex);
                        if(!itemStack.isEmpty() && itemStack.getCount() > biggestSlotSize) {
                            biggestSlotStack = itemStack;
                            biggestSlotStackSlot = slotIndex;
                            biggestSlotSize = itemStack.getCount();
                        }
                    }

                    if(biggestSlotStack == null) {
                        return;
                    }

                    emptyBiggestSlot = false;

                    for(int i = start; i < start + size; ++i) {
                        int slotIndex = menu.slots.get(i).getContainerSlot();
                        ItemStack itemStack = craftMatrix.getItem(slotIndex);
                        if(itemStack.isEmpty()) {
                            if(biggestSlotStack.getCount() > 1) {
                                ItemStack splitted = biggestSlotStack.split(1);
                                craftMatrix.setItem(biggestSlotStackSlot, biggestSlotStack);
                                craftMatrix.setItem(slotIndex, splitted);
                            } else {
                                emptyBiggestSlot = true;
                            }
                        }
                    }
                } while(emptyBiggestSlot);

                this.balanceGrid(grid, player, menu);
            }
        }
    }

    public static class BackpackTransferGridHandler implements GridTransferHandler<AbstractContainerMenu> {
        @Override
        public ItemStack putIntoGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, int slotId, ItemStack itemStack) {
            Container craftMatrix = grid.getCraftingMatrix(player, menu);
            if(craftMatrix == null) {
                return itemStack;
            } else {
                ItemStack craftStack = craftMatrix.getItem(slotId).copy();
                if(!craftStack.isEmpty()) {
                    if(ItemStack.isSameItemSameTags(craftStack, itemStack)) {
                        int spaceLeft = Math.min(craftMatrix.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
                        if(spaceLeft > 0) {
                            ItemStack splitStack = itemStack.split(Math.min(spaceLeft, itemStack.getCount()));
                            craftStack.grow(splitStack.getCount());
                            craftMatrix.setItem(slotId, craftStack);
                            if(itemStack.getCount() <= 0) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else {
                    ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getMaxStackSize()));
                    craftMatrix.setItem(slotId, transferStack);
                }

                return itemStack.getCount() <= 0 ? ItemStack.EMPTY : itemStack;
            }
        }

        @Override
        public boolean transferIntoGrid(CraftingGrid grid, Player player, AbstractContainerMenu menu, Slot fromSlot) {
            Container craftMatrix = grid.getCraftingMatrix(player, menu);
            if(craftMatrix == null) {
                return false;
            } else {
                int start = grid.getGridStartSlot(player, menu);
                int size = grid.getGridSize(player, menu);
                ItemStack itemStack = fromSlot.getItem();
                if(itemStack.isEmpty()) {
                    return false;
                } else {
                    int firstEmptySlot = -1;

                    for(int i = start; i < start + size; ++i) {
                        int slotIndex = menu.slots.get(i).getContainerSlot();
                        ItemStack craftStack = craftMatrix.getItem(slotIndex).copy();
                        if(!craftStack.isEmpty()) {
                            if(ItemStack.isSameItemSameTags(craftStack, itemStack)) {
                                int spaceLeft = Math.min(craftMatrix.getMaxStackSize(), craftStack.getMaxStackSize()) - craftStack.getCount();
                                if(spaceLeft > 0) {
                                    ItemStack splitStack = itemStack.split(Math.min(spaceLeft, itemStack.getCount()));
                                    craftStack.grow(splitStack.getCount());
                                    craftMatrix.setItem(slotIndex, craftStack);
                                    if(itemStack.getCount() <= 0) {
                                        return true;
                                    }
                                }
                            }
                        } else if(firstEmptySlot == -1) {
                            firstEmptySlot = slotIndex;
                        }
                    }

                    if(itemStack.getCount() > 0 && firstEmptySlot != -1) {
                        ItemStack transferStack = itemStack.split(Math.min(itemStack.getCount(), craftMatrix.getMaxStackSize()));
                        craftMatrix.setItem(firstEmptySlot, transferStack);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }

        @Override
        public boolean canTransferFrom(Player player, AbstractContainerMenu abstractContainerMenu, Slot slot, CraftingGrid craftingGrid) {
            if(abstractContainerMenu instanceof BackpackBaseMenu menu) {
                Optional<CraftingUpgrade> upgrade = menu.getWrapper().getUpgradeManager().getUpgrade(CraftingUpgrade.class);
                if(upgrade.isEmpty() || !upgrade.get().isTabOpened()) {
                    return false;
                }
                if(slot.mayPickup(player)) {
                    return slot.container == player.getInventory() || slot.index < menu.BACKPACK_INV_END;
                }
            }
            return false;
        }
    }
}