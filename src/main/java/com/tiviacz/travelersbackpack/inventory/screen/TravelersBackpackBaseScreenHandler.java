package com.tiviacz.travelersbackpack.inventory.screen;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.slot.BackpackSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.FluidSlot;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TravelersBackpackBaseScreenHandler extends ScreenHandler
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftingResultInventory craftResult = new CraftingResultInventory();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END = 48;
    private final int TOOL_START = 49, TOOL_END = 50;
    private final int BUCKET_LEFT_IN = 51, BUCKET_LEFT_OUT = 52;
    private final int BUCKET_RIGHT_IN = 53, BUCKET_RIGHT_OUT = 54;
    private final int PLAYER_INV_START = 55, PLAYER_HOT_END = 90;

    protected TravelersBackpackBaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.craftMatrix = new CraftingInventoryImproved(inventory, this);
        int currentItemIndex = playerInventory.selectedSlot;

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

        this.onContentChanged(inventory.getCraftingGridInventory());
    }

    public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new BackpackSlot(this.craftMatrix, j + i * 3, 152 + j * 18, 61 + i * 18));
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
                this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, 62 + j * 18, 7 + i * 18));
            }
        }

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, 62 + j * 18, 61 + i * 18));
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
        this.addSlot(new FluidSlot(inventory, Reference.BUCKET_IN_LEFT, 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlot(inventory, Reference.BUCKET_OUT_LEFT, 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlot(inventory, Reference.BUCKET_IN_RIGHT, 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlot(inventory, Reference.BUCKET_OUT_RIGHT, 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlot(playerInventory.player, inventory, Reference.TOOL_UPPER, 44, 79));

        //Lower Tool slot
        this.addSlot(new ToolSlot(playerInventory.player, inventory, Reference.TOOL_LOWER, 44, 97));
    }

    @Override
    public void onContentChanged(Inventory inventory)
    {
        CraftingInventoryImproved craftingInventory = this.craftMatrix;
        CraftingResultInventory resultInventory = this.craftResult;
        World world = playerInventory.player.world;

        if (!world.isClient)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)playerInventory.player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
            if(optional.isPresent())
            {
                CraftingRecipe craftingRecipe = optional.get();
                if(resultInventory.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe))
                {
                    itemStack = craftingRecipe.craft(craftingInventory);
                }
            }

            resultInventory.setStack(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(syncId, 0, itemStack));
        }
       /* if(!TravelersBackpackConfig.SERVER.disableCrafting.get())
        {
            CraftingInventoryImproved craftMatrix = this.craftMatrix;
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
        } */
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack stack, Slot slot)
    {
        return slot.inventory != this.craftResult && super.canInsertIntoSlot(stack, slot);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        Slot slot = getSlot(index);
        ItemStack result = ItemStack.EMPTY;

        if(slot != null && slot.hasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();

            if(index >= 0 && index <= BUCKET_RIGHT_OUT)
            {
                if(index == 0)
                {
                    stack.getItem().onCraft(stack, player.world, player);

                    if(!insertItem(stack, BACKPACK_INV_START, PLAYER_HOT_END + 1, true))
                    {
                        return ItemStack.EMPTY;
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
                if(ToolSlot.isValid(stack))
                {
                    if(!insertItem(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if(!insertItem(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                        {
                            if(!insertItem(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                            {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                }

                if(!insertItem(stack, BACKPACK_INV_START, BACKPACK_INV_END + 1, false))
                {
                    if(!insertItem(stack, CRAFTING_GRID_START, CRAFTING_GRID_END + 1, false))
                    {
                        return ItemStack.EMPTY;
                    }
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
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void close(PlayerEntity playerIn)
    {
        super.close(playerIn);

        if(inventory.getScreenID() != Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            this.inventory.markDataDirty(ITravelersBackpackInventory.ALL_DATA);
        }

        if(inventory.getScreenID() == Reference.BLOCK_ENTITY_SCREEN_ID)
        {
            this.inventory.setUsingPlayer(null);
        }

        playSound(playerIn, this.inventory);
        clearBucketSlots(playerIn, this.inventory);
    }

    public static void clearBucketSlots(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        if((inventoryIn.getScreenID() == Reference.ITEM_SCREEN_ID && playerIn.getMainHandStack().getItem() instanceof TravelersBackpackItem) || (inventoryIn.getScreenID() == Reference.WEARABLE_SCREEN_ID && ComponentUtils.getWearingBackpack(playerIn).getItem() instanceof TravelersBackpackItem))
        {
            for(int i = Reference.BUCKET_IN_LEFT; i <= Reference.BUCKET_OUT_RIGHT; i++)
            {
                clearBucketSlot(playerIn, inventoryIn, i);
            }
        }
    }

    public static void clearBucketSlot(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn, int index)
    {
        if(!inventoryIn.getInventory().getStack(index).isEmpty())
        {
            if(!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity && ((ServerPlayerEntity)playerIn).isDisconnected())
            {
                ItemStack stack = inventoryIn.getInventory().getStack(index).copy();
                inventoryIn.getInventory().setStack(index, ItemStack.EMPTY);

                playerIn.dropItem(stack, false);
            }
            else
            {
                ItemStack stack = inventoryIn.getInventory().getStack(index);
                inventoryIn.getInventory().setStack(index, ItemStack.EMPTY);

                playerIn.inventory.offerOrDrop(playerIn.world, stack);
            }
        }
    }

    public void playSound(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        for(int i = Reference.BUCKET_IN_LEFT; i <= Reference.BUCKET_OUT_RIGHT; i++)
        {
            if(!inventoryIn.getInventory().getStack(i).isEmpty())
            {
                playerIn.world.playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.world.random.nextFloat() - playerIn.world.random.nextFloat()) * 0.2F) * 0.7F);
                break;
            }
        }
    }
}