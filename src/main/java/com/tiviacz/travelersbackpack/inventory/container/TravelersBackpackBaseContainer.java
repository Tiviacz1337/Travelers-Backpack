package com.tiviacz.travelersbackpack.inventory.container;

import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public class TravelersBackpackBaseContainer extends Container
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftResultInventory craftResult = new CraftResultInventory();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END = 48;
    private final int TOOL_START = 49, TOOL_END = 50;
    private final int BUCKET_LEFT_IN = 51, BUCKET_LEFT_OUT = 52;
    private final int BUCKET_RIGHT_IN = 53, BUCKET_RIGHT_OUT = 54;
    private final int PLAYER_INV_START = 55, PLAYER_HOT_END = 90;

    public TravelersBackpackBaseContainer(final ContainerType<?> type, final int windowID, final PlayerInventory playerInventory, final ITravelersBackpackInventory inventory)
    {
        super(type, windowID);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.craftMatrix = new CraftingInventoryImproved(inventory, this);
        int currentItemIndex = playerInventory.currentItem;

        //Craft Result
        this.addCraftResult();

        //Crafting Grid, Result Slot
        this.addCraftMatrix();

        //Backpack Inventory
        this.addBackpackInventory(inventory);

        //Functional Slots
        this.addToolSlots(inventory);
        this.addFluidSlots(inventory);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(playerInventory, currentItemIndex);

        this.onCraftMatrixChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
    }

    public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftMatrix, j + i * 3, 152 + j * 18, 61 + i * 18)
                {
                    @Override
                    public boolean isItemValid(ItemStack stack)
                    {
                        return !(stack.getItem() instanceof TravelersBackpackItem);
                    }
                });
            }
        }
    }

    public void addCraftResult()
    {
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 226, 97));
    }

    public void addBackpackInventory(ITravelersBackpackInventory inventory)
    {
        int slot = 0;

        //24 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 8; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 62 + j * 18, 7 + i * 18));
            }
        }

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(inventory.getInventory(), slot++, 62 + j * 18, 61 + i * 18));
            }
        }
    }

    public void addPlayerInventoryAndHotbar(PlayerInventory playerInv, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(playerInv, x + y * 9 + 9, 44 + x*18, 125 + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(playerInv, x, 44 + x*18, 183));
        }
    }

    public void addFluidSlots(ITravelersBackpackInventory inventory)
    {
        //Left In bucket
        this.addSlot(new FluidSlotItemHandler(inventory, Reference.BUCKET_IN_LEFT, 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, Reference.BUCKET_OUT_LEFT, 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(inventory, Reference.BUCKET_IN_RIGHT, 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(inventory, Reference.BUCKET_OUT_RIGHT, 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlotItemHandler(playerInventory.player, inventory, Reference.TOOL_UPPER, 44, 79));

        //Lower Tool slot
        this.addSlot(new ToolSlotItemHandler(playerInventory.player, inventory, Reference.TOOL_LOWER, 44, 97));
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        if(!TravelersBackpackConfig.SERVER.disableCrafting.get())
        {
            CraftingInventory craftMatrix = this.craftMatrix;
            CraftResultInventory craftResult = this.craftResult;
            World world = playerInventory.player.world;

            if(!world.isRemote)
            {
                ServerPlayerEntity player = (ServerPlayerEntity) playerInventory.player;
                ItemStack itemstack = ItemStack.EMPTY;
                Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftMatrix, world);

                if(optional.isPresent())
                {
                    ICraftingRecipe icraftingrecipe = optional.get();

                    if(craftResult.canUseRecipe(world, player, icraftingrecipe))
                    {
                        itemstack = icraftingrecipe.getCraftingResult(craftMatrix);
                    }
                }
                craftResult.setInventorySlotContents(0, itemstack);
                player.connection.sendPacket(new SSetSlotPacket(windowId, 0, itemstack));
            }
        }
    }
    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn)
    {
        return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;

        if(slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if(index >= 0 && index <= BUCKET_RIGHT_OUT)
            {
                if(index == 0)
                {
                    stack.getItem().onCreated(stack, player.world, player);

                    if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                    {
                        return ItemStack.EMPTY;
                    }

                    slot.onSlotChange(stack, result);
                    this.craftMatrix.markDirty();
                }

                else if(!mergeItemStack(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
            {
                if(ToolSlotItemHandler.isValid(stack))
                {
                    if(!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            if(!mergeItemStack(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                if(!mergeItemStack(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                {
                    if(!mergeItemStack(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if(stack.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }

            else
            {
                slot.onSlotChanged();
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
    public boolean canInteractWith(PlayerEntity playerIn)
    {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);

        if(!this.inventory.getInventory().getStackInSlot(Reference.BUCKET_IN_LEFT).isEmpty() || !this.inventory.getInventory().getStackInSlot(Reference.BUCKET_OUT_LEFT).isEmpty() || !this.inventory.getInventory().getStackInSlot(Reference.BUCKET_IN_RIGHT).isEmpty() || !this.inventory.getInventory().getStackInSlot(Reference.BUCKET_OUT_RIGHT).isEmpty())
        {
            playerIn.world.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.world.rand.nextFloat() - playerIn.world.rand.nextFloat()) * 0.2F) * 0.7F);
        }

        this.clearBucketSlot(playerIn, playerIn.world, this.inventory, Reference.BUCKET_IN_LEFT);
        this.clearBucketSlot(playerIn, playerIn.world, this.inventory, Reference.BUCKET_IN_RIGHT);
        this.clearBucketSlot(playerIn, playerIn.world, this.inventory, Reference.BUCKET_OUT_LEFT);
        this.clearBucketSlot(playerIn, playerIn.world, this.inventory, Reference.BUCKET_OUT_RIGHT);
    }

    private void clearBucketSlot(PlayerEntity playerIn, World worldIn, ITravelersBackpack inventoryIn, int index)
    {
        if(!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).hasDisconnected())
        {
            ItemStack stack = inventoryIn.getInventory().getStackInSlot(index).copy();
            inventoryIn.getInventory().setStackInSlot(index, ItemStack.EMPTY);

            playerIn.dropItem(stack, false);
        }
        else
        {
            ItemStack stack = inventoryIn.getInventory().getStackInSlot(index);
            inventoryIn.getInventory().setStackInSlot(index, ItemStack.EMPTY);

            playerIn.inventory.placeItemBackInInventory(worldIn, stack);
        }
    }
}
