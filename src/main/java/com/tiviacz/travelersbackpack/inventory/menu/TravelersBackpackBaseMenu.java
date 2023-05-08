package com.tiviacz.travelersbackpack.inventory.menu;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.stream.IntStream;

public class TravelersBackpackBaseMenu extends AbstractContainerMenu
{
    public Inventory inventory;
    public ITravelersBackpackContainer container;
    public CraftingContainerImproved craftSlots;
    public ResultContainer resultSlots = new ResultContainer();

    private final int CRAFTING_GRID_START = 1, CRAFTING_GRID_END = 9;
    private final int BACKPACK_INV_START = 10, BACKPACK_INV_END;
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
        //this.access = access;
        int currentItemIndex = inventory.selected;

        this.BACKPACK_INV_END = BACKPACK_INV_START + container.getTier().getStorageSlots() - 7;
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
        this.addBackpackInventory(container);

        //Functional Slots
        this.addToolSlots(container);
        this.addFluidSlots(container);

        //Player Inventory
        this.addPlayerInventoryAndHotbar(inventory, currentItemIndex);

        this.slotsChanged(new RecipeWrapper(container.getCraftingGridHandler()));
    }

    public void addCraftMatrix()
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
                        ResourceLocation blacklistedItems = new ResourceLocation(TravelersBackpack.MODID, "blacklisted_items");

                        return !(stack.getItem() instanceof TravelersBackpackItem) && !stack.is(ItemTags.getAllTags().getTag(blacklistedItems));
                    }
                });
            }
        }
    }

    public void addCraftResult()
    {
        this.addSlot(new ResultSlotExt(container, inventory.player, this.craftSlots, this.resultSlots, 0, 226, 43 + this.container.getTier().getMenuSlotPlacementFactor()));
    }

    public void addBackpackInventory(ITravelersBackpackContainer container)
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

        //15 Slots

        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 5; ++j)
            {
                this.addSlot(new BackpackSlotItemHandler(container.getHandler(), slot++, 62 + j * 18, (7 + this.container.getTier().getMenuSlotPlacementFactor()) + i * 18));
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
                if(!container.getSlotManager().getMemorySlots().isEmpty())
                {
                    for(Pair<Integer, ItemStack> pair : container.getSlotManager().getMemorySlots())
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
    protected boolean moveItemStackTo(ItemStack p_38904_, int p_38905_, int p_38906_, boolean p_38907_)
    {
        boolean flag = false;
        int i = p_38905_;
        if (p_38907_) {
            i = p_38906_ - 1;
        }

        if (p_38904_.isStackable()) {
            while(!p_38904_.isEmpty()) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_38904_, itemstack)) {
                    int j = itemstack.getCount() + p_38904_.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), p_38904_.getMaxStackSize());
                    if (j <= maxSize) {
                        p_38904_.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        p_38904_.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (p_38907_) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!p_38904_.isEmpty()) {
            if (p_38907_) {
                i = p_38906_ - 1;
            } else {
                i = p_38905_;
            }

            while(true) {
                if (p_38907_) {
                    if (i < p_38905_) {
                        break;
                    }
                } else if (i >= p_38906_) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                if (itemstack1.isEmpty() && slot1.mayPlace(p_38904_) && canPutStackInSlot(p_38904_, i)) {
                    if (p_38904_.getCount() > slot1.getMaxStackSize()) {
                        slot1.set(p_38904_.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.set(p_38904_.split(p_38904_.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (p_38907_) {
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
        if(container.getSlotManager().isSlot(SlotManager.MEMORY, slot - 10))
        {
            return container.getSlotManager().getMemorySlots().stream().anyMatch(pair -> pair.getFirst() + 10 == slot && ItemStackUtils.isSameItemSameTags(pair.getSecond(), stack));
        }
        return true;
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

                if(!player.level.isClientSide && !moveItemStackTo(recipeOutput, BACKPACK_INV_START, PLAYER_HOT_END + 1, true))
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
