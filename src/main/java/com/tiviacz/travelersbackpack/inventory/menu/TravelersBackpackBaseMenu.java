package com.tiviacz.travelersbackpack.inventory.menu;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.ClientboundUpdateRecipePacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TravelersBackpackBaseMenu extends AbstractContainerMenu
{
    public Inventory inventory;
    public ITravelersBackpackContainer container;
    public CraftingContainerImproved craftSlots;
    public ResultContainer resultSlots = new ResultContainer();

    private final int BACKPACK_INV_START = 1, BACKPACK_INV_END;
    private final int TOOL_START, TOOL_END;
    private final int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    private final int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    private final int PLAYER_INV_START, PLAYER_HOT_END;

    public TravelersBackpackBaseMenu(final MenuType<?> type, final int windowID, final Inventory inventory, final ITravelersBackpackContainer container)
    {
        super(type, windowID);
        this.inventory = inventory;
        this.container = container;
        this.craftSlots = new CraftingContainerImproved(container, this);

        this.BACKPACK_INV_END = BACKPACK_INV_START + container.getTier().getStorageSlotsWithCrafting() - 1;
        this.TOOL_START = BACKPACK_INV_END + 1;
        this.TOOL_END = TOOL_START + 1;
        this.BUCKET_LEFT_IN = TOOL_END + 1;
        this.BUCKET_LEFT_OUT = BUCKET_LEFT_IN + 1;
        this.BUCKET_RIGHT_IN = BUCKET_LEFT_OUT + 1;
        this.BUCKET_RIGHT_OUT = BUCKET_RIGHT_IN + 1;
        this.PLAYER_INV_START = BUCKET_RIGHT_OUT + 1;
        this.PLAYER_HOT_END = PLAYER_INV_START + 35;

        //Craft Result
        this.addCraftResult();

        //Crafting Grid
        //this.addCraftMatrix();

        //Backpack Inventory merged with Crafting Grid
        this.addBackpackInventoryAndCraftingGrid(container);

        //Functional Slots
        this.addToolSlots(container);
        this.addFluidSlots(container);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(inventory, inventory.selected);

        this.slotsChanged(new RecipeWrapper(container.getCraftingGridHandler()));
    }

  /*  public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 152 + j * 18, (7 + this.container.getTier().getMenuSlotPlacementFactor()) + i * 18)
                {
                    @Override
                    public boolean mayPlace(ItemStack stack)
                    {
                        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS);
                    }
                });
            }
        }
    } */

    public void addCraftResult()
    {
        this.addSlot(new ResultSlotExt(container, inventory.player, this.craftSlots, this.resultSlots, container.getScreenID(), 0, 226, 43 + this.container.getTier().getMenuSlotPlacementFactor()));
    }

    public void addBackpackInventoryAndCraftingGrid(ITravelersBackpackContainer container)
    {
        int slot = 0;

        if(this.container.getTier().getOrdinal() > Tiers.LEATHER.getOrdinal())
        {
            //8 * Ordinal Slots

            for(int i = 0; i < this.container.getTier().getOrdinal(); ++i)
            {
                for(int j = 0; j < (this.container.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal() ? 9 : 8); ++j)
                {
                    this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, (this.container.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal() ? 44 : 62) + j * 18, 7 + i * 18));
                }
            }
        }

        // 1 Slot

        if(this.container.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal())
        {
            this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 44, 79));
        }

        //15 Slots + Optional Crafting Grid

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 8; ++j)
            {
                if(j >= 5)
                {
                    this.addSlot(new Slot(this.craftSlots, (j - 5) + i * 3, 152 + (j - 5) * 18, (7 + this.container.getTier().getMenuSlotPlacementFactor()) + i * 18)
                    {
                        @Override
                        public boolean mayPlace(ItemStack stack)
                        {
                            if(BackpackSlotItemHandler.BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

                            return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ModTags.BLACKLISTED_ITEMS);
                        }
                    });
                }
                else
                {
                    this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 62 + j * 18, (7 + this.container.getTier().getMenuSlotPlacementFactor()) + i * 18));
                }
            }
        }
    }

    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 44 + x*18, (71 + this.container.getTier().getMenuSlotPlacementFactor()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(inventory, x, 44 + x*18, 129 + this.container.getTier().getMenuSlotPlacementFactor()));
        }
    }

    public void addFluidSlots(ITravelersBackpackContainer container)
    {
        //Left In bucket
        this.addSlot(new FluidSlotItemHandler(container, container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(container, container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT), 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(container, container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(container, container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT), 226, 37));
    }

    public void addToolSlots(ITravelersBackpackContainer container)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlotItemHandler(inventory.player, container, container.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER), 44, 25 + container.getTier().getMenuSlotPlacementFactor()));

        //Lower Tool slot
        this.addSlot(new ToolSlotItemHandler(inventory.player, container, container.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER), 44, 43 + container.getTier().getMenuSlotPlacementFactor()));
    }

    protected void canCraft(Level level, Player player)
    {
        if(!TravelersBackpackConfig.disableCrafting)
        {
            slotChangedCraftingGrid(level, player);
        }
    }

    @Override
    public void slotsChanged(Container container)
    {
        super.slotsChanged(container);
        canCraft(inventory.player.level, inventory.player);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;

        if(slot != null && slot.hasItem())
        {
            ItemStack stack = slot.getItem();
            result = stack.copy();

            if(index >= 0 && index <= BUCKET_RIGHT_OUT)
            {
                if(index == 0)
                {
                    return handleShiftCraft(player, slot);
                }

                else if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
            {
                //Check Memory Slots
                if(!container.getSlotManager().getMemorySlots().isEmpty())
                {
                    boolean isCraftingLocked = container.getSettingsManager().isCraftingGridLocked();
                    List<Pair<Integer, ItemStack>> craftingSlots = new ArrayList<>();

                    for(Pair<Integer, ItemStack> pair : container.getSlotManager().getMemorySlots())
                    {
                        if(isCraftingLocked)
                        {
                            Slot slot1 = getSlot(pair.getFirst() + 1);
                            if(slot1.container instanceof CraftingContainerImproved)
                            {
                                craftingSlots.add(pair);
                                continue;
                            }
                        }

                        if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack) && getSlot(pair.getFirst() + 1).getItem().getCount() != getSlot(pair.getFirst() + 1).getItem().getMaxStackSize())
                        {
                            if(moveItemStackTo(stack, pair.getFirst() + 1, pair.getFirst() + 2, false))
                            {
                                break;
                            }
                        }
                    }

                    if(!craftingSlots.isEmpty())
                    {
                        for(Pair<Integer, ItemStack> pair : craftingSlots)
                        {
                            if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack) && getSlot(pair.getFirst() + 1).getItem().getCount() != getSlot(pair.getFirst() + 1).getItem().getMaxStackSize())
                            {
                                if(moveItemStackTo(stack, pair.getFirst() + 1, pair.getFirst() + 2, false))
                                {
                                    break;
                                }
                            }
                        }
                    }
                }

                if(ToolSlotItemHandler.isValid(stack))
                {
                    if(!moveItemStackTo(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false, true, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                }

                if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false, true, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(stack.isEmpty())
            {
                slot.set(ItemStack.EMPTY);
            }

            else
            {
                slot.setChanged();
            }

            if(stack.getCount() == result.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }
        return result;
    }

    //Custom implementation for quick stacking
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean fromLast, boolean bool, boolean isResult)
    {
        boolean skippedCrafting = false;
        boolean flag = false;
        int i = startIndex;
        if(fromLast)
        {
            i = endIndex - 1;
        }

        if(stack.isStackable())
        {
            while(!stack.isEmpty())
            {
                if(fromLast)
                {
                    if(i < startIndex)
                    {
                        break;
                    }
                }
                else if(i >= endIndex)
                {
                    break;
                }

                Slot slot = this.slots.get(i);

                if(bool)
                {
                    if(container.getSettingsManager().isCraftingGridLocked() || isResult)
                    {
                        if(slot.container instanceof CraftingContainerImproved)
                        {
                            if(fromLast)
                            {
                                --i;
                            }
                            else
                            {
                                ++i;
                            }
                            skippedCrafting = true;
                            continue;
                        }
                    }
                }

                ItemStack itemstack = slot.getItem();

                if(!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());

                    if(j <= maxSize)
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    }
                    else if(itemstack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if(fromLast)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if(!stack.isEmpty())
        {
            if(fromLast)
            {
                i = endIndex - 1;
            }
            else
            {
                i = startIndex;
            }

            while(true)
            {
                if(fromLast)
                {
                    if(i < startIndex)
                    {
                        break;
                    }
                }
                else if(i >= endIndex)
                {
                    break;
                }

                Slot slot1 = this.slots.get(i);

                if(bool)
                {
                    if(container.getSettingsManager().isCraftingGridLocked() || isResult)
                    {
                        if(slot1.container instanceof CraftingContainerImproved)
                        {
                            if(fromLast)
                            {
                                --i;
                            }
                            else
                            {
                                ++i;
                            }
                            skippedCrafting = true;
                            continue;
                        }
                    }
                }

                ItemStack itemstack1 = slot1.getItem();

                if(itemstack1.isEmpty() && mayPlace(slot1, stack))
                {
                    if(stack.getCount() > slot1.getMaxStackSize())
                    {
                        slot1.set(stack.split(slot1.getMaxStackSize()));
                    }
                    else
                    {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if(fromLast)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }
        if(skippedCrafting && !isResult)
        {
            i = 43;
            bool = false;
            moveItemStackTo(stack, i, endIndex, fromLast, bool, false);
        }
        return flag;
    }

    public boolean mayPlace(Slot slot, ItemStack stack)
    {
        if(container.getSlotManager().isSlot(SlotManager.MEMORY, slot.index - 1))
        {
            return slot.mayPlace(stack) && container.getSlotManager().getMemorySlots().stream().anyMatch(p -> p.getFirst() + 1 == slot.index && ItemStackUtils.isSameItemSameTags(p.getSecond(), stack));
        }
        return slot.mayPlace(stack);
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean fromLast)
    {
        boolean flag = false;
        int i = startIndex;
        if(fromLast)
        {
            i = endIndex - 1;
        }

        if(stack.isStackable())
        {
            while(!stack.isEmpty())
            {
                if(fromLast)
                {
                    if(i < startIndex)
                    {
                        break;
                    }
                }
                else if(i >= endIndex)
                {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();

                if(!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());

                    if(j <= maxSize)
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    }
                    else if(itemstack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if(fromLast)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if(!stack.isEmpty())
        {
            if(fromLast)
            {
                i = endIndex - 1;
            }
            else
            {
                i = startIndex;
            }

            while(true)
            {
                if(fromLast)
                {
                    if(i < startIndex)
                    {
                        break;
                    }
                }
                else if(i >= endIndex)
                {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();

                if(itemstack1.isEmpty() && mayPlace(slot1, stack))
                {
                    if(stack.getCount() > slot1.getMaxStackSize())
                    {
                        slot1.set(stack.split(slot1.getMaxStackSize()));
                    }
                    else
                    {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if(fromLast)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }
        return flag;
    }

    public ItemStack handleShiftCraft(Player player, Slot resultSlot)
    {
        ItemStack outputCopy = ItemStack.EMPTY;

        if(resultSlot != null && resultSlot.hasItem())
        {
            craftSlots.checkChanges = false;
            Recipe<CraftingContainer> recipe = (Recipe<CraftingContainer>)resultSlots.getRecipeUsed();
            while(recipe != null && recipe.matches(craftSlots, player.level))
            {
                ItemStack recipeOutput = resultSlot.getItem().copy();
                outputCopy = recipeOutput.copy();

                recipeOutput.getItem().onCraftedBy(recipeOutput, player.level, player);

                if(!player.level.isClientSide && !moveItemStackTo(recipeOutput, BACKPACK_INV_START, PLAYER_HOT_END + 1, true, true, true))
                {
                    craftSlots.checkChanges = true;
                    return ItemStack.EMPTY;
                }

                resultSlot.onQuickCraft(recipeOutput, outputCopy);
                resultSlot.setChanged();

                if(!player.level.isClientSide && recipeOutput.getCount() == outputCopy.getCount())
                {
                    craftSlots.checkChanges = true;
                    return ItemStack.EMPTY;
                }

                resultSlots.setRecipeUsed(recipe);
                resultSlot.onTake(player, recipeOutput);
            }
            craftSlots.checkChanges = true;
            slotChangedCraftingGrid(player.level, player);
            container.setDataChanged(ITravelersBackpackContainer.CRAFTING_INVENTORY_DATA);
        }
        craftSlots.checkChanges = true;
        return resultSlots.getRecipeUsed() == null ? ItemStack.EMPTY : outputCopy;
    }

    public void slotChangedCraftingGrid(Level level, Player player)
    {
        if(!level.isClientSide)
        {
            ItemStack itemstack = ItemStack.EMPTY;

            Recipe<CraftingContainer> oldRecipe = (Recipe<CraftingContainer>)resultSlots.getRecipeUsed();
            Recipe<CraftingContainer> recipe = oldRecipe;

            if(recipe == null || !recipe.matches(craftSlots, level))
            {
                recipe = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftSlots, level).orElse(null);
            }

            if(recipe != null)
            {
                itemstack = recipe.assemble(craftSlots);
            }

            if(oldRecipe != recipe)
            {
                TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new ClientboundUpdateRecipePacket(recipe, itemstack));
                resultSlots.setItem(0, itemstack);
                resultSlots.setRecipeUsed(recipe);
            }
            else if(recipe != null)
            {
                if(recipe.isSpecial() || !recipe.getClass().getName().startsWith("net.minecraft") && !ItemStack.matches(itemstack, resultSlots.getItem(0)))
                {
                    TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new ClientboundUpdateRecipePacket(recipe, itemstack));
                    resultSlots.setItem(0, itemstack);
                    resultSlots.setRecipeUsed(recipe);
                }
            }
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player)
    {
        if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || container.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return;
        }
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(Player player)
    {
        if(container.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            this.container.setDataChanged(ITravelersBackpackContainer.ALL_DATA);
        }

        if(container.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || container.getSlotManager().isSelectorActive(SlotManager.MEMORY)) container.getSlotManager().setChanged();

            this.container.setUsingPlayer(null);
        }

        if(container.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE)) container.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, false);
        if(container.getSlotManager().isSelectorActive(SlotManager.MEMORY)) container.getSlotManager().setSelectorActive(SlotManager.MEMORY, false);

        playSound(player, this.container);
        clearBucketSlots(player, this.container);

        super.removed(player);
    }

    public static void clearBucketSlots(Player player, ITravelersBackpackContainer container)
    {
        if(container.getScreenID() == Reference.ITEM_SCREEN_ID || container.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            IntStream.range(container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT) + 1).forEach(i -> clearBucketSlot(player, container, i));
        }
    }

    public static void clearBucketSlot(Player player, ITravelersBackpackContainer container, int index)
    {
        if(!container.getHandler().getStackInSlot(index).isEmpty())
        {
            if(!player.isAlive() || player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected())
            {
                ItemStack stack = container.getHandler().getStackInSlot(index).copy();
                container.getHandler().setStackInSlot(index, ItemStack.EMPTY);

                player.drop(stack, false);
            }
            else
            {
                ItemStack stack = container.getHandler().getStackInSlot(index);
                container.getHandler().setStackInSlot(index, ItemStack.EMPTY);

                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    public void playSound(Player player, ITravelersBackpackContainer container)
    {
        for(int i = container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT); i <= container.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT); i++)
        {
            if(!container.getHandler().getStackInSlot(i).isEmpty())
            {
                player.level.playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, (1.0F + (player.level.getRandom().nextFloat() - player.level.getRandom().nextFloat()) * 0.2F) * 0.7F);
                break;
            }
        }
    }

    @Override
    public boolean stillValid(Player player)
    {
        return true;
    }
}