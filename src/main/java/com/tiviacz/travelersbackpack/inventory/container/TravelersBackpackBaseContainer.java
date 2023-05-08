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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.stream.IntStream;

public class TravelersBackpackBaseContainer extends Container
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftResultInventory craftResult = new CraftResultInventory();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END;
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

        this.BACKPACK_INV_END = BACKPACK_INV_START + inventory.getTier().getStorageSlots() - 7;
        this.TOOL_START = BACKPACK_INV_END + 1;
        this.TOOL_END = TOOL_START + 1;
        this.BUCKET_LEFT_IN = TOOL_END + 1;
        this.BUCKET_LEFT_OUT = BUCKET_LEFT_IN + 1;
        this.BUCKET_RIGHT_IN = TOOL_END + 1;
        this.BUCKET_RIGHT_OUT = BUCKET_LEFT_IN + 1;
        this.PLAYER_INV_START = BUCKET_RIGHT_OUT + 1;
        this.PLAYER_HOT_END = PLAYER_INV_START + 35;

        //Craft Result
        this.addCraftResult();

        //Crafting Grid
        this.addCraftMatrix();

        //Backpack Inventory
        this.addBackpackInventory(inventory);

        //Functional Slots
        this.addToolSlots(inventory);
        this.addFluidSlots(inventory);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(playerInventory, currentItemIndex);

        this.slotsChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
    }

    public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 152 + j * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18)
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
        }
    }

    public void addCraftResult()
    {
        this.addSlot(new CraftResultSlotExt(inventory, playerInventory.player, this.craftMatrix, this.craftResult, 0, 226, 43 + this.inventory.getTier().getMenuSlotPlacementFactor()));
    }

    public void addBackpackInventory(ITravelersBackpackInventory inventory)
    {
        int slot = 0;

        if(this.inventory.getTier().getOrdinal() > Tiers.LEATHER.getOrdinal())
        {
            //8 * Ordinal Slots

            for(int i = 0; i < this.inventory.getTier().getOrdinal(); ++i)
            {
                for(int j = 0; j < (this.inventory.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal() ? 9 : 8); ++j)
                {
                    this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, (this.inventory.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal() ? 44 : 62) + j * 18, 7 + i * 18));
                }
            }
        }

        // 1 Slot

        if(this.inventory.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal())
        {
            this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 44, 79));
        }

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 62 + j * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18));
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
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT), 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT), 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlotItemHandler(playerInventory.player, inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER), 44, 25 + inventory.getTier().getMenuSlotPlacementFactor()));

        //Lower Tool slot
        this.addSlot(new ToolSlotItemHandler(playerInventory.player, inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER), 44, 43 + inventory.getTier().getMenuSlotPlacementFactor()));
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

                if(!player.level.isClientSide && !moveItemStackTo(recipeOutput, BACKPACK_INV_START, PLAYER_HOT_END + 1, true))
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
            //slotChangedCraftingGrid(player.level, player);
            slotsChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
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
                if(!inventory.getSlotManager().getMemorySlots().isEmpty())
                {
                    for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
                    {
                        if(ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack) && getSlot(pair.getFirst() + 10).getItem().getCount() != getSlot(pair.getFirst() + 10).getItem().getMaxStackSize())
                        {
                            if(!moveItemStackTo(stack, pair.getFirst() + 10, pair.getFirst() + 11, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                if(ToolSlotItemHandler.isValid(stack))
                {
                    if(!moveItemStackTo(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            if(!moveItemStackTo(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                if(!moveItemStackTo(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                {
                    if(!moveItemStackTo(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
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

    @Override
    protected boolean moveItemStackTo(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
        boolean flag = false;
        int i = p_75135_2_;
        if (p_75135_4_) {
            i = p_75135_3_ - 1;
        }

        if (p_75135_1_.isStackable()) {
            while(!p_75135_1_.isEmpty()) {
                if (p_75135_4_) {
                    if (i < p_75135_2_) {
                        break;
                    }
                } else if (i >= p_75135_3_) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && consideredTheSameItem(p_75135_1_, itemstack)) {
                    int j = itemstack.getCount() + p_75135_1_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_75135_1_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_75135_1_.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_75135_1_.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (p_75135_4_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!p_75135_1_.isEmpty()) {
            if (p_75135_4_) {
                i = p_75135_3_ - 1;
            } else {
                i = p_75135_2_;
            }

            while(true) {
                if (p_75135_4_) {
                    if (i < p_75135_2_) {
                        break;
                    }
                } else if (i >= p_75135_3_) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(p_75135_1_) && canPutStackInSlot(p_75135_1_, i)) {
                    if (p_75135_1_.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(p_75135_1_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(p_75135_1_.split(p_75135_1_.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (p_75135_4_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    public boolean canPutStackInSlot(ItemStack stack, int slot)
    {
        if(inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot - 10))
        {
            return inventory.getSlotManager().getMemorySlots().stream().anyMatch(pair -> pair.getFirst() + 10 == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack));
        }
        return true;
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
    }

    public static void clearBucketSlots(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        if(inventoryIn.getScreenID() == Reference.ITEM_SCREEN_ID || inventoryIn.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            IntStream.range(inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT) + 1).forEach(i -> clearBucketSlot(playerIn, inventoryIn, i));
        }
    }

    public static void clearBucketSlot(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn, int index)
    {
        if(!inventoryIn.getInventory().getStackInSlot(index).isEmpty())
        {
            if(!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected())
            {
                ItemStack stack = inventoryIn.getInventory().getStackInSlot(index).copy();
                inventoryIn.getInventory().setStackInSlot(index, ItemStack.EMPTY);

                playerIn.drop(stack, false);
            }
            else
            {
                ItemStack stack = inventoryIn.getInventory().getStackInSlot(index);
                inventoryIn.getInventory().setStackInSlot(index, ItemStack.EMPTY);

                playerIn.inventory.placeItemBackInInventory(playerIn.level, stack);
            }
        }
    }

    public void playSound(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        for(int i = inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT); i <= inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT); i++)
        {
            if(!inventoryIn.getInventory().getStackInSlot(i).isEmpty())
            {
                playerIn.level.playSound(playerIn, playerIn.blockPosition(), SoundEvents.ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.level.random.nextFloat() - playerIn.level.random.nextFloat()) * 0.2F) * 0.7F);
                break;
            }
        }
    }
}