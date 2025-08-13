package com.tiviacz.travelersbackpack.inventory.menu;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.compat.polymorph.PolymorphCompat;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.menu.slot.*;
import com.tiviacz.travelersbackpack.inventory.upgrades.IUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.inventory.upgrades.crafting.CraftingUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.tanks.TanksUpgrade;
import com.tiviacz.travelersbackpack.inventory.upgrades.voiding.VoidUpgrade;
import com.tiviacz.travelersbackpack.items.upgrades.UpgradeItem;
import com.tiviacz.travelersbackpack.network.ClientboundUpdateRecipePacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class BackpackBaseMenu extends AbstractBackpackMenu {
    public List<UpgradeLockableSlotItemHandler> upgradeSlot = new ArrayList<>();
    public int unmodifiableSlotCount = 0;

    public int TOOL_START, TOOL_END;
    public int UPGRADE_START, UPGRADE_END;
    public int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    public int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    public int CRAFTING_RESULT;
    public int CRAFTING_GRID_START, CRAFTING_GRID_END;

    public BackpackBaseMenu(MenuType<?> type, int windowID, Inventory inventory, BackpackWrapper wrapper) {
        super(type, windowID, inventory, wrapper);
        this.addSlots();
    }

    //Add all slots - menu initialization
    public void addSlots() {
        if(this.wrapper.tanksVisible()) {
            extendedScreenOffset = 22;
        }

        //Storage Slots
        this.addBackpackStorageSlots(wrapper);
        this.BACKPACK_INV_END = this.slots.size();

        //Tool Slots
        this.TOOL_START = this.slots.size();
        this.addBackpackToolSlots(wrapper);
        this.TOOL_END = this.slots.size();

        //Upgrades
        this.UPGRADE_START = this.slots.size();
        this.addBackpackUpgradeSlots(wrapper);
        this.UPGRADE_END = this.slots.size();

        //Player Inventory
        this.PLAYER_INV_START = this.slots.size();
        this.addPlayerInventoryAndHotbar(inventory, getWrapper().getBackpackSlotIndex());
        this.PLAYER_HOT_END = this.slots.size();

        this.unmodifiableSlotCount = this.slots.size();

        //Listeners
        this.addUpgradeListeners();

        //Upgrades
        this.addUpgradeSlots(wrapper);
    }

    //Update storage, player, upgrade slots
    //Add slots that can be modified - slots from upgrades
    public void addModifiableSlots() {
        if(this.wrapper.tanksVisible()) {
            this.extendedScreenOffset = 22;
        }

        //Update Player Slots, Storage Slots
        this.updateSlotsPosition();

        //Update Upgrade Slots
        this.updateBackpackUpgradeSlots();

        //Listeners from Upgrades
        this.addUpgradeListeners();

        //Slots from Upgrades
        this.addUpgradeSlots(wrapper);

        //Update result slot on client
        this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(craftingUpgrade -> canCraft(inventory.player.level(), inventory.player));
    }

    //Reset Modifiable slots - remove slots if upgrades removed
    public void updateModifiableSlots() {
        this.extendedScreenOffset = 0;

        if(this.lastSlots.size() > this.unmodifiableSlotCount) {
            this.lastSlots.subList(this.unmodifiableSlotCount, this.lastSlots.size()).clear();
        }
        if(this.slots.size() > this.unmodifiableSlotCount) {
            this.slots.subList(this.unmodifiableSlotCount, this.slots.size()).clear();
        }
        if(this.remoteSlots.size() > this.unmodifiableSlotCount) {
            this.remoteSlots.subList(this.unmodifiableSlotCount, this.remoteSlots.size()).clear();
        }

        this.addModifiableSlots();
    }

    public void updateSlotsPosition() {
        int slot = 0;

        for(int i = BACKPACK_INV_START; i < BACKPACK_INV_END; i++) {
            if(this.slots.get(i).getClass().equals(BackpackSlotItemHandler.class)) {
                this.slots.get(i).x = this.extendedScreenOffset + 8 + slot * 18;

                if(slot < this.wrapper.getSlotsInRow() - 1) {
                    slot++;
                } else {
                    slot = 0;
                }
            }
        }

        int modifiedOffset = this.extendedScreenOffset * 2;
        if(this.wrapper.isExtended()) {
            modifiedOffset += (18 * 2);
        }

        for(int i = UPGRADE_START; i < UPGRADE_END; i++) {
            if(this.slots.get(i).getClass().equals(UpgradeLockableSlotItemHandler.class)) {
                this.slots.get(i).x = 9 * 18 + modifiedOffset + 15;
            }
        }

        modifiedOffset = this.extendedScreenOffset;
        if(this.wrapper.isExtended()) {
            modifiedOffset += 18;
        }

        slot = 0;

        for(int i = PLAYER_INV_START; i < PLAYER_HOT_END; i++) {
            if(this.slots.get(i).container instanceof Inventory) {
                this.slots.get(i).x = modifiedOffset + 8 + slot * 18;

                if(slot < 8) {
                    slot++;
                } else {
                    slot = 0;
                }
            }
        }
    }

    public void updateSlots() {
        this.extendedScreenOffset = 0;

        this.lastSlots.clear();
        this.slots.clear();
        this.remoteSlots.clear();

        addSlots();
    }

    public void addUpgradeListeners() {
        for(Optional<? extends IUpgrade> upgrade : wrapper.getUpgradeManager().mappedUpgrades.values()) {
            upgrade.ifPresent(iUpgrade -> iUpgrade.initializeContainers(this, this.wrapper));
        }
    }

    public void updateBackpackUpgradeSlots() {
        AtomicInteger nextSlot = new AtomicInteger();
        boolean tabOpened = false;
        int lastOccupiedSlot = -1;

        for(int i = wrapper.getUpgrades().getSlots() - 1; i >= 0; i--) {
            if(!wrapper.getUpgrades().getStackInSlot(i).isEmpty()) {
                if(i != 0 && lastOccupiedSlot == -1) {
                    lastOccupiedSlot = i;
                }
                if(!tabOpened && wrapper.getUpgradeManager().hasUpgradeInSlot(i)) {
                    tabOpened = wrapper.getUpgrades().getStackInSlot(i).getOrDefault(ModDataComponents.TAB_OPEN, false);
                }
            }
        }

        boolean finalTabOpened = tabOpened;
        int finalLastOccupiedSlot = lastOccupiedSlot;

        this.slots.stream().filter(slot -> slot instanceof UpgradeLockableSlotItemHandler).forEach(slot -> {
            UpgradeLockableSlotItemHandler upgradeSlot = (UpgradeLockableSlotItemHandler)slot;
            upgradeSlot.setHidden(false);
            int j = slot.getContainerSlot();
            if(j > 0) {
                Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(j - 1);
                if(upgrade != null && upgrade.isPresent()) {
                    nextSlot.addAndGet(upgrade.get().getTabSize().y() + 1);
                } else {
                    nextSlot.addAndGet(24 + 1);
                }
            }

            upgradeSlot.y = 15 + 18 + nextSlot.get();
            if(finalTabOpened) {
                if(upgradeSlot.getContainerSlot() > finalLastOccupiedSlot) {
                    upgradeSlot.setHidden(true);
                }
            }

            upgradeSlot.setLocked(upgradeSlot.getItem().getItem() instanceof UpgradeItem);
        });
    }

    public void addBackpackUpgradeSlots(BackpackWrapper wrapper) {
        upgradeSlot.clear();

        int modifiedOffset = this.extendedScreenOffset * 2;
        if(this.wrapper.isExtended()) {
            modifiedOffset += (18 * 2);
        }

        int nextSlot = 0;
        boolean tabOpened = false;
        int lastOccupiedSlot = -1;

        for(int i = wrapper.getUpgrades().getSlots() - 1; i >= 0; i--) {
            if(!wrapper.getUpgrades().getStackInSlot(i).isEmpty()) {
                if(i != 0 && lastOccupiedSlot == -1) {
                    lastOccupiedSlot = i;
                }
                if(!tabOpened && wrapper.getUpgradeManager().hasUpgradeInSlot(i)) {
                    tabOpened = wrapper.getUpgrades().getStackInSlot(i).getOrDefault(ModDataComponents.TAB_OPEN, false);
                }
            }
        }

        for(int i = 0; i < wrapper.getUpgrades().getSlots(); i++) {

            if(i > 0) {
                Optional<? extends IUpgrade> upgrade = wrapper.getUpgradeManager().mappedUpgrades.get(i - 1);
                if(upgrade != null && upgrade.isPresent()) {
                    nextSlot += upgrade.get().getTabSize().y() + 1;
                } else {
                    nextSlot += 24 + 1;
                }
            }

            UpgradeLockableSlotItemHandler slot = new UpgradeLockableSlotItemHandler(this, wrapper.getUpgrades(), i, 9 * 18 + modifiedOffset + 15, 15 + 18 + nextSlot);
            if(tabOpened) {
                if(slot.getContainerSlot() > lastOccupiedSlot) {
                    slot.setHidden(true);
                }
            }
            this.addSlot(slot);
        }
    }

    @Override
    protected Slot addSlot(Slot slot) {
        if(slot instanceof UpgradeLockableSlotItemHandler upgradeSlotItemHandler) {
            this.upgradeSlot.add(upgradeSlotItemHandler);
        }
        return super.addSlot(slot);
    }

    public void addBackpackToolSlots(BackpackWrapper wrapper) {
        for(int i = 0; i < wrapper.getTools().getSlots(); i++) {
            this.addSlot(new ToolSlotItemHandler(wrapper, i, -14, 18 + (i * 18)));
        }
    }

    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex) {
        int modifiedOffset = this.extendedScreenOffset;
        if(this.wrapper.isExtended()) {
            modifiedOffset += 18;
        }

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, modifiedOffset + 8 + x * 18, (this.wrapper.getRows() * 18 + 7 + 25) + y * 18));
            }
        }

        for(int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, x, modifiedOffset + 8 + x * 18, this.wrapper.getRows() * 18 + 7 + 83));
        }
    }

    public void addUpgradeSlots(BackpackWrapper wrapper) {
        for(Optional<UpgradeBase<?>> upgrade : wrapper.getUpgradeManager().mappedUpgrades.values()) {
            upgrade.ifPresent(upgradeLoaded -> {
                int x = upgradeSlot.get(wrapper.getUpgradeManager().mappedUpgrades.inverse().get(upgrade)).x - 4;
                int y = upgradeSlot.get(wrapper.getUpgradeManager().mappedUpgrades.inverse().get(upgrade)).y - 4;
                if(upgradeLoaded.isTabOpened()) {
                    for(var slot : upgradeLoaded.getUpgradeSlots(this, wrapper, x, y)) {
                        this.addSlot(slot);
                    }
                }
                //Update result slot on client
                if(upgradeLoaded instanceof CraftingUpgrade) {
                    this.broadcastChanges();
                }
            });
        }
    }

    @Override
    protected void doClick(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        //Trash slot logic
        if(pSlotId >= 0 && pSlotId < this.slots.size() && this.slots.get(pSlotId) instanceof TrashSlot trashSlot) {
            if(!getCarried().isEmpty() && trashSlot.hasItem() && pClickType == ClickType.PICKUP) {
                trashSlot.set(ItemStack.EMPTY.copy());
            }
        }
        if(pSlotId >= 0 && pSlotId < this.slots.size() && this.slots.get(pSlotId) instanceof FilterSlotItemHandler filterSlot) {
            if(getCarried().isEmpty() && pClickType == ClickType.PICKUP && pButton == 0) { //Remove item from filter slot
                super.doClick(pSlotId, pButton, pClickType, pPlayer);
            } else if(!getCarried().isEmpty() && filterSlot.mayPlace(getCarried())) { //Add item to filter slot
                if(!filterSlot.hasItem()) {
                    filterSlot.set(getCarried().copyWithCount(1));
                }
            }
        } else {
            super.doClick(pSlotId, pButton, pClickType, pPlayer);
        }
    }

    protected void canCraft(Level level, Player player) {
        this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(craftingUpgrade -> this.slotChangedCraftingGrid(craftingUpgrade, level, player));
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        canCraft(inventory.player.level(), inventory.player);
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();
        this.canCraft(inventory.player.level(), inventory.player);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if(this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).isPresent()) {
            return slot.container != this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).get().resultSlots;
        }
        if(slot instanceof FilterSlotItemHandler) {
            return false;
        }
        return super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;
        if(slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if(slot instanceof ResultSlotExt resultSlotExtNew) {
                return handleShiftCraft(this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).get(), player, resultSlotExtNew);
            }
            if(slot instanceof CraftingSlot) {
                if(!moveItemStackTo(stack, BACKPACK_INV_START, PLAYER_HOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if(index >= BACKPACK_INV_START && index < BACKPACK_INV_END) {
                if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if(index >= PLAYER_INV_START && index < PLAYER_HOT_END) {
                if(wrapper.showToolSlots() && ToolSlotItemHandler.isValid(stack)) {
                    if(!moveItemStackTo(stack, TOOL_START, TOOL_END, false)) {
                        if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }

                if(!checkMemorySlots(stack)) {
                    if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if(slot instanceof UpgradeSlotItemHandler<?> upgradeSlotItemHandler) {
                if(upgradeSlotItemHandler.shiftClickToBackpack()) {
                    if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                        if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true)) {
                        if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            if(slot instanceof ToolSlotItemHandler) {
                if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if(stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.set(stack);
                //slot.setChanged();
            }
            if(stack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return result;
    }

    public boolean checkMemorySlots(ItemStack stack) {
        if(!wrapper.getMemorySlots().isEmpty()) {
            for(Pair<Integer, Pair<ItemStack, Boolean>> memorizedStack : wrapper.getMemorySlots()) {
                if(stack.getItem() != memorizedStack.getSecond().getFirst().getItem()) {
                    continue;
                }

                if(memorizedStack.getSecond().getSecond()) {
                    if(ItemStackUtils.isSameItemSameComponents(memorizedStack.getSecond().getFirst(), stack)) {
                        if(moveItemStackTo(stack, memorizedStack.getFirst(), memorizedStack.getFirst() + 1, false)) {
                            return stack.isEmpty();
                        }
                    }
                } else {
                    if(ItemStack.isSameItem(memorizedStack.getSecond().getFirst(), stack)) {
                        if(moveItemStackTo(stack, memorizedStack.getFirst(), memorizedStack.getFirst() + 1, false)) {
                            return stack.isEmpty();
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean applyRespectedSlotLogic = startIndex == BACKPACK_INV_START && endIndex == BACKPACK_INV_END;
        boolean flag = false;
        int i = startIndex;
        if(reverseDirection) {
            i = endIndex - 1;
        }

        if(stack.isStackable()) {
            while(!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem().copy();
                if(!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int k = slot.getMaxStackSize(itemstack);
                    if(j <= k) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.set(itemstack);
                        //slot.setChanged();
                        flag = true;
                    } else if(itemstack.getCount() < k) {
                        stack.shrink(k - itemstack.getCount());
                        itemstack.setCount(k);
                        slot.set(itemstack);
                        //slot.setChanged();
                        flag = true;
                    }
                }

                if(reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if(!stack.isEmpty()) {
            if(reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(reverseDirection ? i >= startIndex : i < endIndex) {
                Slot slot1 = this.slots.get(i);
                boolean accept = true;
                Optional<Pair<Integer, Pair<ItemStack, Boolean>>> memorizedOptional = getWrapper().getMemorizedSlot(slot1.getSlotIndex());
                boolean isUnsortable = getWrapper().getUnsortableSlots().contains(slot1.getSlotIndex());
                if(memorizedOptional.isPresent()) {
                    ItemStack memorizedStack = memorizedOptional.get().getSecond().getFirst();
                    boolean matchComponents = memorizedOptional.get().getSecond().getSecond();
                    if(applyRespectedSlotLogic) {
                        accept = matchComponents ? ItemStackUtils.isSameItemSameComponents(memorizedStack, stack) : ItemStack.isSameItem(memorizedStack, stack);
                    }
                }
                if(isUnsortable) {
                    if(!memorizedOptional.isPresent() && accept) {
                        if(applyRespectedSlotLogic) {
                            accept = false;
                        }
                    }
                }

                ItemStack itemstack1 = slot1.getItem();
                if(itemstack1.isEmpty() && slot1.mayPlace(stack) && accept) {
                    int l = slot1.getMaxStackSize(stack);
                    slot1.setByPlayer(stack.split(Math.min(stack.getCount(), l)));
                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if(reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return flag;
    }

    public ItemStack handleShiftCraft(CraftingUpgrade upgrade, Player player, ResultSlotExt resultSlot) {
        ItemStack outputCopy = ItemStack.EMPTY;
        CraftingInput input = upgrade.craftSlots.asCraftInput();

        if(resultSlot != null && resultSlot.hasItem()) {
            upgrade.craftSlots.checkChanges = false;
            RecipeHolder<CraftingRecipe> recipe = (RecipeHolder<CraftingRecipe>)upgrade.resultSlots.getRecipeUsed();
            while(recipe != null && recipe.value().matches(input, player.level())) {
                ItemStack recipeOutput = recipe.value().assemble(input, player.level().registryAccess());
                if(recipeOutput.isEmpty()) {
                    throw new RuntimeException("A recipe matched but produced an empty output - Offending Recipe : " + recipe.id() + " - This is NOT a bug in Traveler's Backpack!");
                }
                outputCopy = recipeOutput.copy();

                recipeOutput.onCraftedBy(player.level(), player, 1);
                EventHooks.firePlayerCraftingEvent(player, recipeOutput, upgrade.craftSlots);

                if(!player.level().isClientSide) {
                    if(upgrade.shiftClickToBackpack(upgrade.getDataHolderStack())) {
                        if(!checkMemorySlots(recipeOutput)) {
                            if(!moveItemStackTo(recipeOutput, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                                upgrade.craftSlots.checkChanges = true;
                                return ItemStack.EMPTY;
                            }
                        }
                    } else {
                        if(!moveItemStackTo(recipeOutput, PLAYER_INV_START, PLAYER_HOT_END, true)) {
                            if(!moveItemStackTo(recipeOutput, BACKPACK_INV_START, BACKPACK_INV_END, false)) {
                                upgrade.craftSlots.checkChanges = true;
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                resultSlot.removeCount += outputCopy.getCount();
                // Handles the actual work of removing the input items.
                resultSlot.onTake(player, recipeOutput);
                resetStackedContents(input);
            }
            upgrade.craftSlots.checkChanges = true;
            slotChangedCraftingGrid(upgrade, player.level(), player);
        }
        return outputCopy;
    }

    public void resetStackedContents(CraftingInput input) {
        StackedContents contents = input.stackedContents();
        contents.clear();
        for(ItemStack i : input.items()) {
            if(!i.isEmpty()) {
                contents.accountStack(i, 1);
            }
        }
    }

    public void slotChangedCraftingGrid(CraftingUpgrade upgrade, Level world, Player player) {
        if(!world.isClientSide && upgrade.craftSlots.checkChanges) {
            ItemStack itemstack = ItemStack.EMPTY;
            CraftingInput input = upgrade.craftSlots.asCraftInput();

            RecipeHolder<CraftingRecipe> oldRecipe = (RecipeHolder<CraftingRecipe>)upgrade.resultSlots.getRecipeUsed();
            RecipeHolder<CraftingRecipe> recipe = oldRecipe;

            if(TravelersBackpack.polymorphLoaded) {
                if(PolymorphCompat.shouldResetRecipe(recipe, this, upgrade.craftSlots, world, player)) {
                    recipe = null;
                }
            }

            if(recipe == null || !recipe.value().matches(input, world)) {
                if(TravelersBackpack.polymorphLoaded) {
                    recipe = PolymorphCompat.getPolymorphedRecipe(this, upgrade.craftSlots, world, player);
                } else {
                    recipe = world.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, world).orElse(null);
                }
            }

            if(recipe != null) itemstack = recipe.value().assemble(input, world.registryAccess());

            // Need to check if the output is empty, because if the recipe book is being used, the recipe will already be set.
            if(oldRecipe != recipe || upgrade.resultSlots.getItem(0).isEmpty()) {
                for(Player user : getWrapper().getPlayersUsing().stream().filter(p -> p instanceof ServerPlayer).toList()) {
                    PacketDistributor.sendToPlayer((ServerPlayer)user, new ClientboundUpdateRecipePacket(recipe, itemstack));
                }
                //PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundUpdateRecipePacket(recipe, itemstack)); //(SeverPlayer)player
                upgrade.resultSlots.setItem(0, itemstack);
                upgrade.resultSlots.setRecipeUsed(recipe);
            } else if(recipe != null) {
                // https://github.com/Shadows-of-Fire/FastWorkbench/issues/72 - Some modded recipes may update the output and not mark themselves as special, moderately
                // annoying but... bleh
                if(recipe.value().isSpecial() || !recipe.getClass().getName().startsWith("net.minecraft") && !ItemStack.matches(itemstack, upgrade.resultSlots.getItem(0))) {
                    for(Player user : getWrapper().getPlayersUsing().stream().filter(p -> p instanceof ServerPlayer).toList()) {
                        PacketDistributor.sendToPlayer((ServerPlayer)user, new ClientboundUpdateRecipePacket(recipe, itemstack));
                    }
                    //PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundUpdateRecipePacket(recipe, itemstack)); //(SeverPlayer)player
                    upgrade.resultSlots.setItem(0, itemstack);
                    upgrade.resultSlots.setRecipeUsed(recipe);
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        this.wrapper.getUpgradeManager().getUpgrade(CraftingUpgrade.class).ifPresent(craftingUpgrade -> this.checkHandlerAndPlaySound(craftingUpgrade.crafting, player, craftingUpgrade.crafting.getSlots()));
        this.wrapper.getUpgradeManager().getUpgrade(TanksUpgrade.class).ifPresent(tanksUpgrade -> this.clearSlotsAndPlaySound(inventory.player, tanksUpgrade.getFluidSlotsHandler(), 4));
        this.wrapper.getUpgradeManager().getUpgrade(VoidUpgrade.class).ifPresent(this::voidTrashSlot);
        shiftTools(this.wrapper.getTools());
        super.removed(player);
    }

    public void clearSlotsAndPlaySound(Player player, ItemStackHandler handler, int size) {
        boolean playSound = false;
        for(int i = 0; i < size; i++) {
            boolean flag = clearSlot(player, handler, i);
            if(flag) playSound = true;
        }
        if(playSound) {
            this.playSound(player);
        }
    }

    public boolean clearSlot(Player player, ItemStackHandler handler, int index) {
        if(!handler.getStackInSlot(index).isEmpty()) {
            if(player == null) return false;
            if(!player.isAlive() || (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected())) {
                ItemStack stack = handler.getStackInSlot(index).copy();
                handler.setStackInSlot(index, ItemStack.EMPTY);
                player.drop(stack, false);
                return false;
            } else {
                ItemStack stack = handler.getStackInSlot(index);
                handler.setStackInSlot(index, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(stack);
                return true;
            }
        }
        return false;
    }

    public void playSound(Player player) {
        player.level().playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, (1.0F + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2F) * 0.7F);
    }

    public void shiftTools(ItemStackHandler toolSlotsHandler) {
        boolean foundEmptySlot = false;
        boolean needsShifting = false;
        for(int i = 0; i < toolSlotsHandler.getSlots(); i++) {
            if(foundEmptySlot) {
                if(!toolSlotsHandler.getStackInSlot(i).isEmpty()) {
                    needsShifting = true;
                }
            }
            if(toolSlotsHandler.getStackInSlot(i).isEmpty() && !foundEmptySlot) {
                foundEmptySlot = true;
            }
        }

        if(needsShifting) {
            NonNullList<ItemStack> tools = NonNullList.withSize(toolSlotsHandler.getSlots(), ItemStack.EMPTY);
            int j = 0;
            for(int i = 0; i < toolSlotsHandler.getSlots(); i++) {
                if(!toolSlotsHandler.getStackInSlot(i).isEmpty()) {
                    tools.set(j, toolSlotsHandler.getStackInSlot(i));
                    j++;
                }
            }
            j = 0;
            for(int i = 0; i < toolSlotsHandler.getSlots(); i++) {
                if(!tools.isEmpty()) {
                    toolSlotsHandler.setStackInSlot(i, tools.get(j));
                    j++;
                }
            }
        }
    }

    public void voidTrashSlot(VoidUpgrade upgrade) {
        upgrade.voidTrashSlotStack();
    }

    //Remove forbidden items from handler, if saving enabled
    public void checkHandlerAndPlaySound(ItemStackHandler handler, Player player, int size) {
        boolean playSound = false;
        for(int i = 0; i < size; i++) {
            boolean flag = clearSlot(handler, player, i);
            if(flag) playSound = true;
        }
        if(playSound) {
            this.playSound(player);
        }
    }

    public boolean clearSlot(ItemStackHandler handler, Player player, int index) {
        if(!BackpackSlotItemHandler.isItemValid(handler.getStackInSlot(index))) {
            if(player == null) return false;
            if(!player.isAlive()) {
                ItemStack stack = handler.getStackInSlot(index).copy();
                handler.setStackInSlot(index, ItemStack.EMPTY);

                if(player instanceof ServerPlayer serverPlayer && !serverPlayer.hasDisconnected()) {
                    player.drop(stack, false);
                }
                return false;
            } else {
                ItemStack stack = handler.getStackInSlot(index);
                handler.setStackInSlot(index, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(stack);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}