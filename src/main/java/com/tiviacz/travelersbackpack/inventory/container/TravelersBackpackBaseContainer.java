package com.tiviacz.travelersbackpack.inventory.container;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.container.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.CraftResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.container.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.CUpdateRecipePacket;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TravelersBackpackBaseContainer extends Container
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftResultInventory craftResult = new CraftResultInventory();

    private final int BACKPACK_INV_START = 1, BACKPACK_INV_END;
    private final int TOOL_START, TOOL_END;
    private final int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    private final int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    private final int PLAYER_INV_START, PLAYER_HOT_END;

    public TravelersBackpackBaseContainer(final ContainerType<?> type, final int windowID, final PlayerInventory playerInventory, final ITravelersBackpackInventory inventory)
    {
        super(type, windowID);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.craftMatrix = new CraftingInventoryImproved(inventory, this);
        int currentItemIndex = playerInventory.selected;

        this.BACKPACK_INV_END = BACKPACK_INV_START + inventory.getTier().getStorageSlotsWithCrafting() - 1;
        this.TOOL_START = BACKPACK_INV_END + 1;
        this.TOOL_END = TOOL_START + inventory.getTier().getToolSlots() - 1;
        this.BUCKET_LEFT_IN = TOOL_END + 1;
        this.BUCKET_LEFT_OUT = BUCKET_LEFT_IN + 1;
        this.BUCKET_RIGHT_IN = BUCKET_LEFT_OUT + 1;
        this.BUCKET_RIGHT_OUT = BUCKET_RIGHT_IN + 1;
        this.PLAYER_INV_START = BUCKET_RIGHT_OUT + 1;
        this.PLAYER_HOT_END = PLAYER_INV_START + 35;

        //Craft Result
        this.addCraftResult();

        //Backpack Inventory merged with Crafting Grid
        this.addBackpackInventoryAndCraftingGrid(inventory);

        //Functional Slots
        this.addToolSlots(inventory);
        this.addFluidSlots(inventory);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(playerInventory, currentItemIndex);

        this.slotsChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
    }

    public void addCraftResult()
    {
        this.addSlot(new CraftResultSlotExt(inventory, playerInventory.player, this.craftMatrix, this.craftResult, 0, 226, 43 + this.inventory.getTier().getMenuSlotPlacementFactor()));
    }

    public void addBackpackInventoryAndCraftingGrid(ITravelersBackpackInventory inventory)
    {
        int slot = 0;

        if(inventory.getTier().getOrdinal() > Tiers.LEATHER.getOrdinal())
        {
            //8 * Ordinal Slots

            for(int i = 0; i < inventory.getTier().getOrdinal(); ++i)
            {
                for(int j = 0; j < 9; ++j)
                {
                    this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 44 + j * 18, 7 + i * 18));
                }
            }
        }

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 9; ++j)
            {
                if(j >= 6)
                {
                    this.addSlot(new Slot(this.craftMatrix, (j - 6) + i * 3, 152 + (j - 6) * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18)
                    {
                        @Override
                        public boolean mayPlace(ItemStack stack)
                        {
                            ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

                            if(BackpackSlotItemHandler.BLACKLISTED_ITEMS.contains(stack.getItem())) return false;

                            return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.getItem().is(ItemTags.getAllTags().getTag(blacklistedItems));
                        }
                    });
                }
                else
                {
                    this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 44 + j * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18));
                }
            }
        }
    }

    public void addPlayerInventoryAndHotbar(PlayerInventory playerInv, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, (71 + this.inventory.getTier().getMenuSlotPlacementFactor()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInv, x, 44 + x*18, 129 + this.inventory.getTier().getMenuSlotPlacementFactor()));
        }
    }

    public void addFluidSlots(ITravelersBackpackInventory inventory)
    {
        //Left In bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), 6, 7)
        {
            @Override
            public boolean isActive()
            {
                return !TravelersBackpackBaseContainer.this.inventory.getSettingsManager().showToolSlots() && super.isActive();
            }
        });

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT), 6, 37)
        {
            @Override
            public boolean isActive()
            {
                return !TravelersBackpackBaseContainer.this.inventory.getSettingsManager().showToolSlots() && super.isActive();
            }
        });

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT), 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        for(int i = 0; i < inventory.getTier().getToolSlots(); i++)
        {
            this.addSlot(new ToolSlotItemHandler(playerInventory.player, inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST) + i, 6, 7 + 18 * i));
        }
    }

    @Override
    public void slotsChanged(IInventory inventory)
    {
        super.slotsChanged(inventory);

        if(!TravelersBackpackConfig.disableCrafting)
        {
            slotChangedCraftingGrid(playerInventory.player.level, playerInventory.player);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn)
    {
        return slotIn.container != this.craftResult && super.canTakeItemForPickAll(stack, slotIn);
    }

    public ItemStack handleShiftCraft(PlayerEntity player, Slot resultSlot)
    {
        ItemStack outputCopy = ItemStack.EMPTY;

        if(resultSlot != null && resultSlot.hasItem())
        {
            craftMatrix.checkChanges = false;
            IRecipe<CraftingInventory> recipe = (IRecipe<CraftingInventory>)craftResult.getRecipeUsed();
            while(recipe != null && recipe.matches(craftMatrix, player.level))
            {
                ItemStack recipeOutput = resultSlot.getItem().copy();
                outputCopy = recipeOutput.copy();

                recipeOutput.getItem().onCraftedBy(recipeOutput, player.level, player);

                if(!player.level.isClientSide && !moveItemStackTo(recipeOutput, BACKPACK_INV_START, PLAYER_HOT_END + 1, true, true, true))
                {
                    craftMatrix.checkChanges = true;
                    return ItemStack.EMPTY;
                }

                resultSlot.onQuickCraft(recipeOutput, outputCopy);
                resultSlot.setChanged();

                if(!player.level.isClientSide && recipeOutput.getCount() == outputCopy.getCount())
                {
                    craftMatrix.checkChanges = true;
                    return ItemStack.EMPTY;
                }

                craftResult.setRecipeUsed(recipe);
                resultSlot.onTake(player, recipeOutput);
            }
            craftMatrix.checkChanges = true;
            slotsChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
            inventory.setDataChanged(ITravelersBackpackInventory.CRAFTING_INVENTORY_DATA);
        }
        craftMatrix.checkChanges = true;
        return craftResult.getRecipeUsed() == null ? ItemStack.EMPTY : outputCopy;
    }

    public void slotChangedCraftingGrid(World world, PlayerEntity player)
    {
        if(!world.isClientSide)
        {
            ItemStack itemstack = ItemStack.EMPTY;

            IRecipe<CraftingInventory> oldRecipe = (IRecipe<CraftingInventory>) craftResult.getRecipeUsed();
            IRecipe<CraftingInventory> recipe = oldRecipe;

            if(recipe == null || !recipe.matches(craftMatrix, world))
            {
                recipe = world.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftMatrix, world).orElse(null);
            }

            if(recipe != null)
            {
                itemstack = recipe.assemble(craftMatrix);
            }

            if(oldRecipe != recipe)
            {
                TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new CUpdateRecipePacket(recipe, itemstack));
                craftResult.setItem(0, itemstack);
                craftResult.setRecipeUsed(recipe);
            }
            else if(recipe != null && recipe.isSpecial())
            {
                TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new CUpdateRecipePacket(recipe, itemstack));
                craftResult.setItem(0, itemstack);
                craftResult.setRecipeUsed(recipe);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index)
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
                if(!inventory.getSlotManager().getMemorySlots().isEmpty())
                {
                    boolean isCraftingLocked = inventory.getSettingsManager().isCraftingGridLocked();
                    List<Pair<Integer, ItemStack>> craftingSlots = new ArrayList<>();

                    for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
                    {
                        if(isCraftingLocked)
                        {
                            Slot slot1 = getSlot(pair.getFirst() + 1);

                            if(slot1.container instanceof CraftingInventoryImproved)
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
                    if(inventory.getSettingsManager().isCraftingGridLocked() || isResult)
                    {
                        if(slot.container instanceof CraftingInventoryImproved)
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

                if(!itemstack.isEmpty() && consideredTheSameItem(stack, itemstack))
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
                    if(inventory.getSettingsManager().isCraftingGridLocked() || isResult)
                    {
                        if(slot1.container instanceof CraftingInventoryImproved)
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
        if(inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot.index - 1))
        {
            return slot.mayPlace(stack) && inventory.getSlotManager().getMemorySlots().stream().anyMatch(p -> p.getFirst() + 1 == slot.index && ItemStackUtils.isSameItemSameTags(p.getSecond(), stack));
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

                if(!itemstack.isEmpty() && consideredTheSameItem(stack, itemstack))
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

    @Override
    public boolean stillValid(PlayerEntity playerIn)
    {
        return true;
    }

    @Override
    public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player)
    {
        if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return ItemStack.EMPTY;
        }
        return super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public void removed(PlayerEntity playerIn)
    {
        super.removed(playerIn);

        if(inventory.getScreenID() != Reference.TILE_SCREEN_ID)
        {
            this.inventory.setDataChanged(ITravelersBackpackInventory.ALL_DATA);
        }

        if(inventory.getScreenID() == Reference.TILE_SCREEN_ID)
        {
            if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY)) inventory.getSlotManager().setChanged();

            this.inventory.setUsingPlayer(null);
        }

        if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE)) inventory.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, false);
        if(inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY)) inventory.getSlotManager().setSelectorActive(SlotManager.MEMORY, false);

        playSound(playerIn, this.inventory);
        clearBucketSlots(playerIn, this.inventory);
        shiftTools(this.inventory);
    }

    public static void clearBucketSlots(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        IntStream.range(inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT) + 1).forEach(i -> clearBucketSlot(playerIn, inventoryIn, i));
    }

    public static void clearBucketSlot(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn, int index)
    {
        if(!inventoryIn.getFluidSlotsInventory().getStackInSlot(index).isEmpty())
        {
            if(playerIn == null) return;

            if(!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected())
            {
                ItemStack stack = inventoryIn.getFluidSlotsInventory().getStackInSlot(index).copy();
                inventoryIn.getFluidSlotsInventory().setStackInSlot(index, ItemStack.EMPTY);

                playerIn.drop(stack, false);
            }
            else
            {
                ItemStack stack = inventoryIn.getFluidSlotsInventory().getStackInSlot(index);
                inventoryIn.getFluidSlotsInventory().setStackInSlot(index, ItemStack.EMPTY);

                playerIn.inventory.placeItemBackInInventory(playerIn.level, stack);
            }
        }
    }

    public void playSound(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        for(int i = inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT); i <= inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT); i++)
        {
            if(!inventoryIn.getFluidSlotsInventory().getStackInSlot(i).isEmpty())
            {
                playerIn.level.playSound(playerIn, playerIn.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.level.random.nextFloat() - playerIn.level.random.nextFloat()) * 0.2F) * 0.7F);
                break;
            }
        }
    }

    public void shiftTools(ITravelersBackpackInventory inventory)
    {
        boolean foundEmptySlot = false;
        boolean needsShifting = false;

        int toolIndex = inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST);

        for(int i = toolIndex; i < toolIndex + inventory.getTier().getToolSlots(); i++)
        {
            if(foundEmptySlot)
            {
                if(!inventory.getInventory().getStackInSlot(i).isEmpty())
                {
                    needsShifting = true;
                }
            }

            if(inventory.getInventory().getStackInSlot(i).isEmpty() && !foundEmptySlot)
            {
                foundEmptySlot = true;
            }
        }

        if(needsShifting)
        {
            NonNullList<ItemStack> tools = NonNullList.withSize(inventory.getTier().getToolSlots(), ItemStack.EMPTY);
            int j = 0;

            for(int i = toolIndex; i < toolIndex + inventory.getTier().getToolSlots(); i++)
            {
                if(!inventory.getInventory().getStackInSlot(i).isEmpty())
                {
                    tools.set(j, inventory.getInventory().getStackInSlot(i));
                    j++;
                }
            }

            j = 0;

            for(int i = toolIndex; i < toolIndex + inventory.getTier().getToolSlots(); i++)
            {
                if(!tools.isEmpty())
                {
                    inventory.getInventory().setStackInSlot(i, tools.get(j));
                    j++;
                }
            }
        }
    }
}