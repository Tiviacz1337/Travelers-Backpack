package com.tiviacz.travelersbackpack.inventory.screen;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.InventoryImproved;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.FluidSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TravelersBackpackBaseScreenHandler extends ScreenHandler
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftingResultInventory craftResult = new CraftingResultInventory();

    private final int BACKPACK_INV_START = 1, BACKPACK_INV_END;
    private final int TOOL_START, TOOL_END;
    private final int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    private final int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    private final int PLAYER_INV_START, PLAYER_HOT_END;
    private final int CRAFTING_GRID_START, CRAFTING_GRID_END;

    protected TravelersBackpackBaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.craftMatrix = new CraftingInventoryImproved(inventory, this);

        //Craft result = 0;
        this.BACKPACK_INV_END = BACKPACK_INV_START + inventory.getInventory().size() - 1;
        this.TOOL_START = BACKPACK_INV_END + 1;
        this.TOOL_END = TOOL_START + inventory.getToolSlotsInventory().size() - 1;
        this.BUCKET_LEFT_IN = TOOL_END + 1;
        this.BUCKET_LEFT_OUT = BUCKET_LEFT_IN + 1;
        this.BUCKET_RIGHT_IN = BUCKET_LEFT_OUT + 1;
        this.BUCKET_RIGHT_OUT = BUCKET_RIGHT_IN + 1;
        this.CRAFTING_GRID_START = BUCKET_RIGHT_OUT + 1;
        this.CRAFTING_GRID_END = CRAFTING_GRID_START + 8;
        this.PLAYER_INV_START = CRAFTING_GRID_END + 1;
        this.PLAYER_HOT_END =  PLAYER_INV_START + 35;

        //Craft Result
        this.addCraftResult();

        //Backpack Inventory
        this.addBackpackInventory(inventory);

        //Functional Slots
        this.addToolSlots(inventory);
        this.addFluidSlots(inventory);

        //Crafting Widget
        this.addCraftingSlots();

        //Player Inventory
        this.addPlayerInventoryAndHotbar(playerInventory, playerInventory.selectedSlot);
    }

    public void addCraftResult()
    {
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 270, 113)
        {
            @Override
            public boolean canTakeItems(PlayerEntity playerEntity)
            {
                return TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().hasCraftingGrid();
            }

            @Override
            public boolean isEnabled()
            {
                return TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().hasCraftingGrid() && TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().showCraftingGrid();
            }
        });
    }

    public void addBackpackInventory(ITravelersBackpackInventory inventory)
    {
        int slot = 0;

        for(int i = 0; i < inventory.getRows(); i++)
        {
            int lastRowSlots = inventory.getInventory().size() % 9;
            if(lastRowSlots == 0) lastRowSlots = 9;

            int slotsInRow = i == inventory.getRows() - 1 ? lastRowSlots : 9;

            for(int j = 0; j < slotsInRow; j++)
            {
                this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, 44 + j * 18, 7 + i * 18));
            }
        }
    }

    public void addFluidSlots(ITravelersBackpackInventory inventory)
    {
        //Left In bucket
        this.addSlot(new FluidSlot(inventory, 0, 6, 7)
        {
            @Override
            public boolean isEnabled()
            {
                return !TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().showToolSlots() && super.isEnabled();
            }
        });

        //Left Out bucket
        this.addSlot(new FluidSlot(inventory, 1, 6, 37)
        {
            @Override
            public boolean isEnabled()
            {
                return !TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().showToolSlots() && super.isEnabled();
            }
        });

        //Right In bucket
        this.addSlot(new FluidSlot(inventory, 2, 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlot(inventory, 3, 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        for(int i = 0; i < inventory.getToolSlotsInventory().size(); i++)
        {
            this.addSlot(new ToolSlot(playerInventory.player, inventory, i, 6, 7 + 18 * i));
        }
    }

    public void addCraftingSlots()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 252 + j * 18, 47 + i * 18)
                {
                    @Override
                    public boolean isEnabled()
                    {
                        return TravelersBackpackBaseScreenHandler.this.inventory.getSettingsManager().showCraftingGrid();
                    }
                });
            }
        }
    }

    public void addPlayerInventoryAndHotbar(PlayerInventory playerInv, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, (71 + this.inventory.getYOffset()) + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInv, x, 44 + x*18, 129 + this.inventory.getYOffset()));
        }
    }

    protected void canCraft(World world, PlayerEntity player)
    {
        if(inventory.getSettingsManager().hasCraftingGrid())
        {
            slotChangedCraftingGrid(this, world, player, this.craftMatrix, this.craftResult);
        }
    }

    @Override
    public void onContentChanged(Inventory inventory)
    {
        super.onContentChanged(inventory);
        canCraft(playerInventory.player.getWorld(), playerInventory.player);
    }

    @Override
    public void syncState()
    {
        super.syncState();

        //Sync on opening
        this.onContentChanged(inventory.getCraftingGridInventory());
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot)
    {
        return slot.inventory != this.craftResult && super.canInsertIntoSlot(stack, slot);
    }

    protected static void slotChangedCraftingGrid(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftMatrix, CraftingResultInventory craftResult)
    {
        if(!world.isClient)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<RecipeEntry<CraftingRecipe>> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftMatrix, world);

            if(optional.isPresent())
            {
                RecipeEntry<CraftingRecipe> craftingRecipeEntry = optional.get();

                if(craftResult.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipeEntry))
                {
                    itemStack = craftingRecipeEntry.value().craft(craftMatrix, world.getRegistryManager());
                }
            }

            craftResult.setStack(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.getRevision(), 0, itemStack));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;

        if(slot != null && slot.hasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if(index >= 0 && index <= CRAFTING_GRID_END) //BUCKETRIGHTOUT??
            {
                if(index == 0)
                {
                    stack.getItem().onCraftByPlayer(stack, player.getWorld(), player);

                    if(inventory.getSettingsManager().shiftClickToBackpack())
                    {
                        if(!insertItem(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            return ItemStack.EMPTY;

                        }
                    }
                    else
                    {
                        if(!insertItem(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                        {
                            return ItemStack.EMPTY;
                        }
                    }

                    slot.onQuickTransfer(stack, result);
                    this.craftMatrix.markDirty();
                }

                else if(!insertItem(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
            {
                //Check Memory Slots
                if(!inventory.getSlotManager().getMemorySlots().isEmpty())
                {
                    for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
                    {
                        if(ItemStackUtils.canCombine(pair.getSecond(), stack) && getSlot(pair.getFirst() + 1).getStack().getCount() != getSlot(pair.getFirst() + 1).getStack().getMaxCount())
                        {
                            if(insertItem(stack, pair.getFirst() + 1, pair.getFirst() + 2, false))
                            {
                                break;
                            }
                        }
                    }
                }

                if(ToolSlot.isValid(stack))
                {
                    if(!insertItem(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!insertItem(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            return ItemStack.EMPTY;
                        }
                    }
                }

                if(!insertItem(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(stack.isEmpty())
            {
                slot.setStack(ItemStack.EMPTY);
            }

            else
            {
                slot.markDirty();
            }

            if(stack.getCount() == result.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, stack);
        }
        return result;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY))
        {
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public void onClosed(PlayerEntity playerIn)
    {
        super.onClosed(playerIn);

        if(inventory.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            this.inventory.markDataDirty(ITravelersBackpackInventory.ALL_DATA);
        }

        if(inventory.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE) || inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY)) inventory.getSlotManager().setChanged();

            this.inventory.setUsingPlayer(null);
        }

        if(inventory.getSlotManager().isSelectorActive(SlotManager.UNSORTABLE)) inventory.getSlotManager().setSelectorActive(SlotManager.UNSORTABLE, false);
        if(inventory.getSlotManager().isSelectorActive(SlotManager.MEMORY)) inventory.getSlotManager().setSelectorActive(SlotManager.MEMORY, false);

        clearSlotsAndPlaySound(playerIn, this.inventory.getFluidSlotsInventory(), 4);
        shiftTools(this.inventory);

        if(!TravelersBackpackConfig.getConfig().backpackSettings.craftingSavesItems)
        {
            clearSlotsAndPlaySound(playerIn, this.inventory.getCraftingGridInventory(), 9);
        }
        else
        {
            checkCraftingGridAndPlaySound(playerIn);
        }
    }

    public void clearSlotsAndPlaySound(PlayerEntity player, InventoryImproved inventoryImproved, int size)
    {
        boolean playSound = false;

        for(int i = 0; i < size; i++)
        {
            boolean flag = clearSlot(player, inventoryImproved, i);
            if(flag) playSound = true;
        }

        if(playSound)
        {
            this.playSound(player);
        }
    }

    public boolean clearSlot(PlayerEntity player, InventoryImproved inventoryImproved, int index)
    {
        if(!inventoryImproved.getStack(index).isEmpty())
        {
            if(player == null) return false;

            if(!player.isAlive() || player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isDisconnected())
            {
                ItemStack stack = inventoryImproved.getStack(index).copy();
                inventoryImproved.setStack(index, ItemStack.EMPTY);

                player.dropItem(stack, false);
                return false;
            }
            else
            {
                ItemStack stack = inventoryImproved.getStack(index);
                inventoryImproved.setStack(index, ItemStack.EMPTY);

                player.getInventory().offerOrDrop(stack);
                return true;
            }
        }
        return false;
    }

    public void shiftTools(ITravelersBackpackInventory inventory)
    {
        boolean foundEmptySlot = false;
        boolean needsShifting = false;

        for(int i = 0; i < inventory.getToolSlotsInventory().size(); i++)
        {
            if(foundEmptySlot)
            {
                if(!inventory.getToolSlotsInventory().getStack(i).isEmpty())
                {
                    needsShifting = true;
                }
            }

            if(inventory.getToolSlotsInventory().getStack(i).isEmpty() && !foundEmptySlot)
            {
                foundEmptySlot = true;
            }
        }

        if(needsShifting)
        {
            DefaultedList<ItemStack> tools = DefaultedList.ofSize(inventory.getToolSlotsInventory().size(), ItemStack.EMPTY);
            int j = 0;

            for(int i = 0; i < inventory.getToolSlotsInventory().size(); i++)
            {
                if(!inventory.getToolSlotsInventory().getStack(i).isEmpty())
                {
                    tools.set(j, inventory.getToolSlotsInventory().getStack(i));
                    j++;
                }
            }

            j = 0;

            for(int i = 0; i < inventory.getToolSlotsInventory().size(); i++)
            {
                if(!tools.isEmpty())
                {
                    inventory.getToolSlotsInventory().setStack(i, tools.get(j));
                    j++;
                }
            }
        }
    }

    public void playSound(PlayerEntity playerIn)
    {
        playerIn.getWorld().playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.getWorld().random.nextFloat() - playerIn.getWorld().random.nextFloat()) * 0.2F) * 0.7F);
    }

    //Remove forbidden items from crafting grid, if saving enabled
    public void checkCraftingGridAndPlaySound(PlayerEntity player)
    {
        boolean playSound = false;

        for(int i = 0; i < inventory.getCraftingGridInventory().size(); i++)
        {
            boolean flag = clearCraftingGridSlot(player, i);
            if(flag) playSound = true;
        }

        if(playSound)
        {
            this.playSound(player);
        }
    }

    public boolean clearCraftingGridSlot(PlayerEntity player, int index)
    {
        if(!BackpackSlot.isValid(inventory.getCraftingGridInventory().getStack(index)))
        {
            if(player == null) return false;

            if(!player.isAlive() || player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isDisconnected())
            {
                ItemStack stack = inventory.getCraftingGridInventory().getStack(index).copy();
                inventory.getCraftingGridInventory().setStack(index, ItemStack.EMPTY);

                player.dropItem(stack, false);
            }
            else
            {
                ItemStack stack = inventory.getCraftingGridInventory().getStack(index);
                inventory.getCraftingGridInventory().setStack(index, ItemStack.EMPTY);

                player.getInventory().offerOrDrop(stack);
            }
        }
        return false;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}