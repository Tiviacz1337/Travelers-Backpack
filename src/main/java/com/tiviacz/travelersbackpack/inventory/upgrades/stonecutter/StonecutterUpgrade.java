package com.tiviacz.travelersbackpack.inventory.upgrades.stonecutter;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.component.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.handler.ItemStackHandler;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.UpgradeSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.IMoveSelector;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class StonecutterUpgrade extends UpgradeBase<StonecutterUpgrade> implements IMoveSelector {
    private final ItemStackHandler stonecutterHandler;
    private final Supplier<Level> level;
    private final ResultContainer resultContainer;
    private SelectableRecipe.SingleInputSet<StonecutterRecipe> recipesForInput;
    private final Slot resultSlot;
    private ItemStack input = ItemStack.EMPTY;
    private int selectedRecipeIndex = -1;
    private long lastSoundTime;
    private Runnable slotUpdateListener = () -> {
    };

    public StonecutterUpgrade(UpgradeManager manager, int dataHolderSlot, NonNullList<ItemStack> stonecutterHandler, int selectedIndex) {
        super(manager, dataHolderSlot, new Point(93, 105));
        this.stonecutterHandler = createHandler(stonecutterHandler);
        this.level = () -> getWrapper().getLevel();
        this.selectedRecipeIndex = selectedIndex;
        this.recipesForInput = SelectableRecipe.SingleInputSet.empty();
        this.input = ItemStack.EMPTY;
        this.slotUpdateListener = () -> {};
        this.resultContainer = new ResultContainer();
        this.resultSlot = new StonecutterResultSlot<>(this, this.resultContainer, 0, 55, 23) {
            @Override
            public void onTake(final Player player, final ItemStack carried) {
                carried.onCraftedBy(player, carried.getCount());
                StonecutterUpgrade.this.resultContainer.awardUsedRecipes(player, this.getRelevantItems());
                ItemStack remaining = StonecutterUpgrade.this.stonecutterHandler.extractItem(0, 1, false);
                if(!remaining.isEmpty()) {
                    StonecutterUpgrade.this.setupResultSlot(StonecutterUpgrade.this.selectedRecipeIndex);
                }
                getWrapper().getContainerLevelAccess().ifPresent(access -> access.execute((level, pos) -> {
                    long gameTime = level.getGameTime();
                    if(lastSoundTime != gameTime) {
                        level.playSound(null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                        lastSoundTime = gameTime;
                    }
                }));
                super.onTake(player, carried);
            }

            private List<ItemStack> getRelevantItems() {
                return List.of(StonecutterUpgrade.this.stonecutterHandler.getItem(0));
            }
        };

        SlotAccess slot = this.stonecutterHandler.getSlot(0);
        if(/*level.get() != null &&*/ !slot.get().isEmpty()) {
            this.slotsChanged(slot);
            if(this.isValidRecipeIndex(this.selectedRecipeIndex)) {
                this.setupResultSlot(this.selectedRecipeIndex);
            }
        }
    }

    public void registerUpdateListener(final Runnable slotUpdateListener) {
        this.slotUpdateListener = slotUpdateListener;
    }

    public void syncClient(ItemStack backpack) {
        int slot = getDataHolderSlot();
        ItemContainerContents contents = backpack.get(ModDataComponents.UPGRADES);
        int upgradesSize = backpack.get(ModDataComponents.UPGRADE_SLOTS);
        if(contents == null) return;
        NonNullList<ItemStack> stacks = ContainerContentsHelper.getItems(contents, upgradesSize);
        if(slot >= stacks.size()) return;
        ItemStack stack = stacks.get(slot);
        this.selectedRecipeIndex = stack.getOrDefault(ModDataComponents.INDEX, -1);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new StonecutterWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(Pair.of(x + 7, y + 23));
        positions.add(Pair.of(x + 55, y + 23));
        return positions;
    }

    @Override
    public List<? extends Slot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new UpgradeSlotItemHandler<>(this, this.stonecutterHandler, 0, x + 7, y + 23));
        this.resultSlot.x = x + 55;
        this.resultSlot.y = y + 23;
        slots.add(this.resultSlot);
        return slots;
    }

    public void setSlotChanged(ItemStack dataHolderStack, int index, ItemStack stack) {
        dataHolderStack.update(ModDataComponents.BACKPACK_CONTAINER, ItemContainerContents.EMPTY, currentContents -> ContainerContentsHelper.updateStack(currentContents, 1, stack, index));
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks) {
        return new ItemStackHandler(stacks) {
            @Override
            protected void onContentsChanged(int slot) {
                updateDataHolderUnchecked(dataHolderStack -> setSlotChanged(dataHolderStack, slot, getStackInSlot(slot)));

                StonecutterUpgrade.this.slotsChanged(this.getSlot(slot));
                StonecutterUpgrade.this.slotUpdateListener.run();
            }
        };
    }

    public void slotsChanged(SlotAccess slotAccess) {
        ItemStack input = slotAccess.get();
        if(!input.is(this.input.getItem())) {
            this.input = input.copy();
            this.setupRecipeList(input);
        }
    }

    public boolean hasInputItem() {
        return !this.stonecutterHandler.getItem(0).isEmpty() && !this.recipesForInput.isEmpty();
    }

    private void setupRecipeList(ItemStack item) {
        this.resultSlot.set(ItemStack.EMPTY);
        if(!item.isEmpty()) {
            this.recipesForInput = this.level.get().recipeAccess().stonecutterRecipes().selectByInput(item);

            //Display result of selected recipe from data holder
            if(this.isValidRecipeIndex(this.selectedRecipeIndex) && this.resultSlot.getItem().isEmpty()) {
                this.setupResultSlot(this.selectedRecipeIndex);
            }
        } else {
            this.recipesForInput = SelectableRecipe.SingleInputSet.empty();
        }
    }

    public SelectableRecipe.SingleInputSet<StonecutterRecipe> getVisibleRecipes() {
        return this.recipesForInput;
    }

    public int getNumberOfVisibleRecipes() {
        return this.recipesForInput.size();
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex;
    }

    @Override
    public boolean clickMenuButton(final Player player, final int buttonId) {
        if(this.selectedRecipeIndex == buttonId) {
            return false;
        }

        if(this.isValidRecipeIndex(buttonId)) {
            this.selectedRecipeIndex = buttonId;
            this.setupResultSlot(buttonId);
            updateDataHolderUnchecked(ModDataComponents.INDEX, buttonId);
        }
        return true;
    }

    private boolean isValidRecipeIndex(final int buttonId) {
        return buttonId >= 0 && buttonId < this.recipesForInput.size();
    }

    public void setupResultSlot(final int index) {
        Optional<RecipeHolder<StonecutterRecipe>> usedRecipe;
        if(!this.recipesForInput.isEmpty() && this.isValidRecipeIndex(index)) {
            SelectableRecipe.SingleInputEntry<StonecutterRecipe> entry = this.recipesForInput.entries().get(index);
            usedRecipe = entry.recipe().recipe();
        } else {
            usedRecipe = Optional.empty();
        }

        usedRecipe.ifPresentOrElse(recipe -> {
            this.resultContainer.setRecipeUsed(recipe);
            this.resultSlot.set(recipe.value().assemble(new SingleRecipeInput(this.stonecutterHandler.getItem(0))));
        }, () -> {
            this.resultSlot.set(ItemStack.EMPTY);
            this.resultContainer.setRecipeUsed(null);
        });
        //this.broadcastChanges();
    }
}