package com.tiviacz.travelersbackpack.inventory.screen;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.CraftingInventoryImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.Tiers;
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
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

public class TravelersBackpackBaseScreenHandler extends ScreenHandler
{
    public PlayerInventory playerInventory;
    public ITravelersBackpackInventory inventory;
    public CraftingInventoryImproved craftMatrix;
    public CraftingResultInventory craftResult = new CraftingResultInventory();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END;
    private final int TOOL_START, TOOL_END;
    private final int BUCKET_LEFT_IN, BUCKET_LEFT_OUT;
    private final int BUCKET_RIGHT_IN, BUCKET_RIGHT_OUT;
    private final int PLAYER_INV_START, PLAYER_HOT_END;

    protected TravelersBackpackBaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ITravelersBackpackInventory inventory)
    {
        super(type, syncId);
        this.playerInventory = playerInventory;
        this.inventory = inventory;
        this.craftMatrix = new CraftingInventoryImproved(inventory, this);
        int currentItemIndex = playerInventory.selectedSlot;

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

        this.onContentChanged(inventory.getCraftingGridInventory());
    }

    public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new BackpackSlot(this.craftMatrix, j + i * 3, 152 + j * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18));
            }
        }
    }

    public void addCraftResult()
    {
        this.addSlot(new CraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 226, 43 + this.inventory.getTier().getMenuSlotPlacementFactor())
        {
            @Override
            public boolean isEnabled()
            {
                if(TravelersBackpackBaseScreenHandler.this.inventory.getTier().getOrdinal() <= 0)
                {
                    return TravelersBackpackBaseScreenHandler.this.inventory.getInventory().getStack(TravelersBackpackBaseScreenHandler.this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT)).isEmpty() && TravelersBackpackBaseScreenHandler.this.inventory.getInventory().getStack(TravelersBackpackBaseScreenHandler.this.inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT)).isEmpty();
                }
                return true;
            }
        });
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
                    this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, (this.inventory.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal() ? 44 : 62) + j * 18, 7 + i * 18));
                }
            }
        }

        // 1 Slot

        if(this.inventory.getTier().getOrdinal() == Tiers.NETHERITE.getOrdinal())
        {
            this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, 44, 79));
        }

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlot(inventory.getInventory(), slot++, 62 + j * 18, (7 + this.inventory.getTier().getMenuSlotPlacementFactor()) + i * 18));
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
        this.addSlot(new FluidSlot(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlot(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_LEFT), 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlot(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlot(inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT), 226, 37));
    }

    public void addToolSlots(ITravelersBackpackInventory inventory)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlot(playerInventory.player, inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_UPPER), 44, 25 + inventory.getTier().getMenuSlotPlacementFactor()));

        //Lower Tool slot
        this.addSlot(new ToolSlot(playerInventory.player, inventory, inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_LOWER), 44, 43 + inventory.getTier().getMenuSlotPlacementFactor()));
    }

    protected static void slotChangedCraftingGrid(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftMatrix, CraftingResultInventory craftResult)
    {
        if (!world.isClient)
        {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)player;
            ItemStack itemStack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftMatrix, world);
            if(optional.isPresent())
            {
                CraftingRecipe craftingRecipe = optional.get();
                if(craftResult.shouldCraftRecipe(world, serverPlayerEntity, craftingRecipe))
                {
                    itemStack = craftingRecipe.craft(craftMatrix, world.getRegistryManager());
                }
            }

            craftResult.setStack(0, itemStack);
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.getRevision(), 0, itemStack));
        }
    }
    @Override
    public void onContentChanged(Inventory inventory)
    {
        slotChangedCraftingGrid(this, playerInventory.player.world, playerInventory.player, this.craftMatrix, this.craftResult);
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
    public ItemStack quickMove(PlayerEntity player, int index)
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
                if(!inventory.getSlotManager().getMemorySlots().isEmpty())
                {
                    for(Pair<Integer, ItemStack> pair : inventory.getSlotManager().getMemorySlots())
                    {
                        if(ItemStackUtils.canCombine(pair.getSecond(), stack) && getSlot(pair.getFirst() + 10).getStack().getCount() != getSlot(pair.getFirst() + 10).getStack().getMaxCount())
                        {
                            if(!insertItem(stack, pair.getFirst() + 10, pair.getFirst() + 11, false))
                            {
                                return ItemStack.EMPTY;
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
    protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
        boolean bl = false;
        int i = startIndex;
        if (fromLast) {
            i = endIndex - 1;
        }

        Slot slot;
        ItemStack itemStack;
        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot = (Slot)this.slots.get(i);
                itemStack = slot.getStack();
                if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        slot.markDirty();
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        slot.markDirty();
                        bl = true;
                    }
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (fromLast) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (fromLast) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                slot = (Slot)this.slots.get(i);
                itemStack = slot.getStack();
                if (itemStack.isEmpty() && slot.canInsert(stack) && canPutStackInSlot(stack, i)) {
                    if (stack.getCount() > slot.getMaxItemCount()) {
                        slot.setStack(stack.split(slot.getMaxItemCount()));
                    } else {
                        slot.setStack(stack.split(stack.getCount()));
                    }

                    slot.markDirty();
                    bl = true;
                    break;
                }

                if (fromLast) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return bl;
    }

    public boolean canPutStackInSlot(ItemStack stack, int slot)
    {
        if(inventory.getSlotManager().isSlot(SlotManager.MEMORY, slot - 10))
        {
            return inventory.getSlotManager().getMemorySlots().stream().anyMatch(pair -> pair.getFirst() + 10 == slot && ItemStackUtils.canCombine(pair.getSecond(), stack));
        }
        return true;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
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

        playSound(playerIn, this.inventory);
        clearBucketSlots(playerIn, this.inventory);

        if(!playerIn.world.isClient && TravelersBackpack.enableTrinkets() && ComponentUtils.isWearingBackpack(playerIn) && this.inventory.getScreenID() == Reference.WEARABLE_SCREEN_ID)
        {
            ItemStack componentStack = ComponentUtils.getWearingBackpack(playerIn);

            if(componentStack.getNbt() != null && this.inventory.getItemStack().getNbt() != null)
            {
                if(!ItemStack.canCombine(componentStack, this.inventory.getItemStack()))
                {
                    ComponentUtils.getWearingBackpack(playerIn).setNbt(this.inventory.getItemStack().getNbt());
                }
            }
        }
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
        if(!inventoryIn.getInventory().getStack(index).isEmpty())
        {
            if(!playerIn.isAlive() || playerIn instanceof ServerPlayerEntity serverPlayer && serverPlayer.isDisconnected())
            {
                ItemStack stack = inventoryIn.getInventory().getStack(index).copy();
                inventoryIn.getInventory().setStack(index, ItemStack.EMPTY);

                playerIn.dropItem(stack, false);
            }
            else
            {
                ItemStack stack = inventoryIn.getInventory().getStack(index);
                inventoryIn.getInventory().setStack(index, ItemStack.EMPTY);

                playerIn.getInventory().offerOrDrop(stack);
            }
        }
    }

    public void playSound(PlayerEntity playerIn, ITravelersBackpackInventory inventoryIn)
    {
        for(int i = inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT); i <= inventoryIn.getTier().getSlotIndex(Tiers.SlotType.BUCKET_OUT_RIGHT); i++)
        {
            if(!inventoryIn.getInventory().getStack(i).isEmpty())
            {
                playerIn.world.playSound(playerIn, playerIn.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1.0F, (1.0F + (playerIn.world.random.nextFloat() - playerIn.world.random.nextFloat()) * 0.2F) * 0.7F);
                break;
            }
        }
    }
}