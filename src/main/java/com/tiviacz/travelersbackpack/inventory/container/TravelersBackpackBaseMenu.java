package com.tiviacz.travelersbackpack.inventory.container;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModTags;
import com.tiviacz.travelersbackpack.inventory.CraftingContainerImproved;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.container.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.container.slot.ResultSlotExt;
import com.tiviacz.travelersbackpack.inventory.container.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.network.UpdateRecipePacket;
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

public class TravelersBackpackBaseMenu extends AbstractContainerMenu
{
    public Inventory inventory;
    public Player player;
    public ITravelersBackpackContainer container;
    public CraftingContainerImproved craftSlots;
    public ResultContainer resultSlots = new ResultContainer();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END = 48;
    private final int TOOL_START = 49, TOOL_END = 50;
    private final int BUCKET_LEFT_IN = 51, BUCKET_LEFT_OUT = 52;
    private final int BUCKET_RIGHT_IN = 53, BUCKET_RIGHT_OUT = 54;
    private final int PLAYER_INV_START = 55, PLAYER_HOT_END = 90;

    public TravelersBackpackBaseMenu(final MenuType<?> type, final int windowID, final Inventory inventory, final ITravelersBackpackContainer container)
    {
        super(type, windowID);
        this.inventory = inventory;
        this.player = inventory.player;
        this.container = container;
        this.craftSlots = new CraftingContainerImproved(container, this);
        //this.access = access;
        int currentItemIndex = inventory.selected;

        //Craft Result
        this.addCraftResult();

        //Crafting Grid, Result Slot
        this.addCraftMatrix();

        //Backpack Inventory
        this.addBackpackInventory(container);

        //Functional Slots
        this.addToolSlots(container);
        this.addFluidSlots(container);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(inventory, currentItemIndex);

        this.slotsChanged(new RecipeWrapper(container.getCraftingGridHandler()));
        //this.onCraftMatrixChanged(new RecipeWrapper(inventory.getCraftingGridInventory()));
    }

    public void addCraftMatrix()
    {
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 152 + j * 18, 61 + i * 18)
                {
                    @Override
                    public boolean mayPlace(ItemStack stack)
                    {
                        //ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

                        return !(stack.getItem() instanceof TravelersBackpackItem) &&  !stack.is(ModTags.BLACKLISTED_ITEMS);
                    }
                });
            }
        }
    }

    public void addCraftResult()
    {
        this.addSlot(new ResultSlotExt(player, this.craftSlots, this.resultSlots, 0, 226, 97));
    }

    public void addBackpackInventory(ITravelersBackpackContainer container)
    {
        int slot = 0;

        //24 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 8; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 62 + j * 18, 7 + i * 18));
            }
        }

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 62 + j * 18, 61 + i * 18));
            }
        }
    }

    public void addPlayerInventoryAndHotbar(Inventory inventory, int currentItemIndex)
    {
        for(int y = 0; y < 3; y++)
        {
            for(int x = 0; x < 9; x++)
            {
                this.addSlot(new Slot(inventory, x + y * 9 + 9, 44 + x*18, 125 + y*18));
            }
        }

        for(int x = 0; x < 9; x++)
        {
            this.addSlot(new Slot(inventory, x, 44 + x*18, 183));
        }
    }

    public void addFluidSlots(ITravelersBackpackContainer container)
    {
        //Left In bucket
        this.addSlot(new FluidSlotItemHandler(container, Reference.BUCKET_IN_LEFT, 6, 7));

        //Left Out bucket
        this.addSlot(new FluidSlotItemHandler(container, Reference.BUCKET_OUT_LEFT, 6, 37));

        //Right In bucket
        this.addSlot(new FluidSlotItemHandler(container, Reference.BUCKET_IN_RIGHT, 226, 7));

        //Right Out bucket
        this.addSlot(new FluidSlotItemHandler(container, Reference.BUCKET_OUT_RIGHT, 226, 37));
    }

    public void addToolSlots(ITravelersBackpackContainer container)
    {
        //Upper Tool Slot
        this.addSlot(new ToolSlotItemHandler(player, container, Reference.TOOL_UPPER, 44, 79));

        //Lower Tool slot
        this.addSlot(new ToolSlotItemHandler(player, container, Reference.TOOL_LOWER, 44, 97));
    }

    protected void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftingContainer, ResultContainer resultContainer)
    {
        if(!TravelersBackpackConfig.SERVER.disableCrafting.get()) // && !level.isClientSide)
        {
            slotChangedCraftingGrid(level, player);
     /*       ServerPlayer serverPlayer = (ServerPlayer)player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<CraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingContainer, level);

            if(optional.isPresent())
            {
                CraftingRecipe recipe = optional.get();

                if(resultContainer.setRecipeUsed(level, serverPlayer, recipe))
                {
                    itemstack = recipe.assemble(craftingContainer);
                }
            }

            resultContainer.setItem(0, itemstack);
            menu.setRemoteSlot(0, itemstack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, itemstack)); */
        }
    }

    @Override
    public void slotsChanged(Container container)
    {
        slotChangedCraftingGrid(this, player.level, player, this.craftSlots, this.resultSlots);
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
                /*{
                    stack.getItem().onCraftedBy(stack, player.level, player);

                    if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                    {
                        return ItemStack.EMPTY;
                    }

                    slot.onQuickCraft(stack, result);
                    this.craftSlots.setChanged();
                } */

                else if(!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
                {
                    return ItemStack.EMPTY;
                }
            }

            if(index >= PLAYER_INV_START)
            {
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

                if(!player.level.isClientSide && !moveItemStackTo(recipeOutput, PLAYER_INV_START, PLAYER_HOT_END + 1, true))
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
                TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateRecipePacket(recipe, itemstack));
                resultSlots.setItem(0, itemstack);
                resultSlots.setRecipeUsed(recipe);
            }
            else if(recipe != null)
            {
                if(recipe.isSpecial() || !recipe.getClass().getName().startsWith("net.minecraft") && !ItemStack.matches(itemstack, resultSlots.getItem(0)))
                {
                    TravelersBackpack.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new UpdateRecipePacket(recipe, itemstack));
                    resultSlots.setItem(0, itemstack);
                    resultSlots.setRecipeUsed(recipe);
                }
            }
        }
    }

    @Override
    public void removed(Player player)
    {
        if(container.getScreenID() != Reference.TRAVELERS_BACKPACK_BLOCK_ENTITY_SCREEN_ID)
        {
            this.container.setChanged();
        }

        playSound(player, this.container);
        clearBucketSlots(player, this.container);

        super.removed(player);
    }

    public static void clearBucketSlots(Player player, ITravelersBackpackContainer container)
    {
        if((container.getScreenID() == Reference.TRAVELERS_BACKPACK_ITEM_SCREEN_ID && player.getMainHandItem().getItem() instanceof TravelersBackpackItem) || (container.getScreenID() == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID && CapabilityUtils.getWearingBackpack(player).getItem() instanceof TravelersBackpackItem))
        {
            for(int i = Reference.BUCKET_IN_LEFT; i <= Reference.BUCKET_OUT_RIGHT; i++)
            {
                clearBucketSlot(player, container, i);
            }
        }
    }

    public static void clearBucketSlot(Player player, ITravelersBackpackContainer container, int index)
    {
        if(!container.getHandler().getStackInSlot(index).isEmpty())
        {
            if(!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected())
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
        for(int i = Reference.BUCKET_IN_LEFT; i <= Reference.BUCKET_OUT_RIGHT; i++)
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
