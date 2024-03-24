package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackContainer implements ITravelersBackpackContainer, MenuProvider, Nameable
{
    private final ItemStackHandler inventory = createHandler(Tiers.LEATHER.getStorageSlots(), true);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE, false);
    private final ItemStackHandler toolSlots = createToolsHandler(Tiers.LEATHER.getToolSlots());
    private final ItemStackHandler fluidSlots = createTemporaryHandler();
    private final FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private final Player player;
    private ItemStack stack;
    private Tiers.Tier tier;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    public TravelersBackpackContainer(ItemStack stack, Player player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        this.loadAllData(stack.getOrCreateTag());
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void loadTier(CompoundTag compound)
    {
        if(!compound.contains(TIER))
        {
            compound.putInt(TIER, TravelersBackpackConfig.enableTierUpgrades ? Tiers.LEATHER.getOrdinal() : Tiers.DIAMOND.getOrdinal());
        }
        if(compound.contains(TIER, Tag.TAG_STRING))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(TIER));
            compound.remove(TIER);
            compound.putInt(TIER, tier.getOrdinal());
        }
        this.tier = Tiers.of(compound.getInt(TIER));
    }

    @Override
    public ItemStackHandler getHandler()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getCraftingGridHandler()
    {
        return this.craftingInventory;
    }

    @Override
    public ItemStackHandler getToolSlotsHandler()
    {
        return this.toolSlots;
    }

    @Override
    public ItemStackHandler getFluidSlotsHandler()
    {
        return this.fluidSlots;
    }

    @Override
    public IItemHandlerModifiable getCombinedHandler()
    {
        return new CombinedInvWrapper(getHandler(), getToolSlotsHandler(), getFluidSlotsHandler(), getCraftingGridHandler());
    }

    @Override
    public FluidTank getLeftTank()
    {
        return this.leftTank;
    }

    @Override
    public FluidTank getRightTank()
    {
        return this.rightTank;
    }

    @Override
    public void saveAllData(CompoundTag compound)
    {
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveAbility(compound);
        this.saveTime(compound);
        this.slotManager.saveUnsortableSlots(compound);
        this.slotManager.saveMemorySlots(compound);
        this.settingsManager.saveSettings(compound);
    }

    @Override
    public void loadAllData(CompoundTag compound)
    {
        this.loadTier(compound);
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadAbility(compound);
        this.loadTime(compound);
        this.slotManager.loadUnsortableSlots(compound);
        this.slotManager.loadMemorySlots(compound);
        this.settingsManager.loadSettings(compound);
    }

    @Override
    public void saveItems(CompoundTag compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(TOOLS_INVENTORY, this.toolSlots.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundTag compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.toolSlots.deserializeNBT(compound.getCompound(TOOLS_INVENTORY));
        this.craftingInventory.deserializeNBT(compound.getCompound(CRAFTING_INVENTORY));
    }

    @Override
    public void saveTanks(CompoundTag compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNBT(new CompoundTag()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void loadTanks(CompoundTag compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    @Override
    public void saveColor(CompoundTag compound) {}
    @Override
    public void loadColor(CompoundTag compound) {}

    @Override
    public void saveSleepingBagColor(CompoundTag compound) {}

    @Override
    public void loadSleepingBagColor(CompoundTag compound) {}

    @Override
    public void saveAbility(CompoundTag compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundTag compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.forceAbilityEnabled || compound.getBoolean(ABILITY);
    }

    @Override
    public void saveTime(CompoundTag compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void loadTime(CompoundTag compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), 0, player) || InventoryActions.transferContainerTank(this, getRightTank(), 2, player);
    }

    private void sendPackets()
    {
        if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            CapabilityUtils.synchronise(player);
            CapabilityUtils.synchroniseToOthers(player);
        }
    }

    @Override
    public boolean hasColor()
    {
        return stack.getOrCreateTag().contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return stack.getOrCreateTag().getInt(COLOR);
        }
        return 0;
    }

    public boolean hasSleepingBagColor()
    {
        return this.stack.getOrCreateTag().contains(SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrCreateTag().getInt(SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId();
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.enableBackpackAbilities ? (BackpackAbilities.ALLOWED_ABILITIES.contains(getItemStack().getItem()) ? this.ability : false) : false;
    }

    @Override
    public void setAbility(boolean value)
    {
        this.ability = value;
    }

    @Override
    public int getLastTime()
    {
        return this.lastTime;
    }

    @Override
    public void setLastTime(int time)
    {
        this.lastTime = time;
    }

    @Override
    public boolean hasBlockEntity()
    {
        return false;
    }

    @Override
    public int getRows()
    {
        return (int)Math.ceil((double)getHandler().getSlots() / 9);
    }

    @Override
    public int getYOffset()
    {
        return 18 * Math.max(0, getRows() - 3);
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return false;
    }

    @Override
    public SlotManager getSlotManager()
    {
        return slotManager;
    }

    @Override
    public SettingsManager getSettingsManager()
    {
        return settingsManager;
    }

    @Override
    public Tiers.Tier getTier()
    {
        return this.tier;
    }
    
    @Override
    public Level getLevel()
    {
        return this.player.level;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.player.blockPosition();
    }

    @Override
    public byte getScreenID()
    {
        return this.screenID;
    }

    @Override
    public ItemStack getItemStack()
    {
        return this.stack;
    }

    @Override
    public void setUsingPlayer(@Nullable Player player) {}

    @Override
    public void setDataChanged(byte... dataIds)
    {
        if(getLevel().isClientSide) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                case INVENTORY_DATA: stack.getOrCreateTag().put(INVENTORY, this.inventory.serializeNBT());
                case TOOLS_DATA: this.stack.getOrCreateTag().put(TOOLS_INVENTORY, this.toolSlots.serializeNBT());
                case CRAFTING_INVENTORY_DATA: stack.getOrCreateTag().put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
                case COMBINED_INVENTORY_DATA: saveItems(stack.getOrCreateTag());
                case TANKS_DATA: saveTanks(stack.getOrCreateTag());
                case COLOR_DATA: saveColor(stack.getOrCreateTag());
                case SLEEPING_BAG_COLOR_DATA: saveSleepingBagColor(this.stack.getOrCreateTag());
                case ABILITY_DATA: saveAbility(stack.getOrCreateTag());
                case LAST_TIME_DATA: saveTime(stack.getOrCreateTag());
                case SLOT_DATA: slotManager.saveUnsortableSlots(stack.getOrCreateTag());
                                slotManager.saveMemorySlots(stack.getOrCreateTag());
                case SETTINGS_DATA: settingsManager.saveSettings(stack.getOrCreateTag());
                case ALL_DATA: saveAllData(stack.getOrCreateTag());
            }
        }
        sendPackets();
    }

    @Override
    public void setDataChanged() {}

    @Override
    public Component getName()
    {
        return new TranslatableComponent("screen.travelersbackpack.item");
    }

    @Override
    public Component getDisplayName()
    {
        return new TranslatableComponent("screen.travelersbackpack.item");
    }

    public static void abilityTick(Player player)
    {
        if(player.isAlive() && CapabilityUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(player)))
        {
            TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);

            if(!player.level.isClientSide)
            {
                if(container.getLastTime() > 0)
                {
                    container.setLastTime(container.getLastTime() - 1);
                    container.setDataChanged(LAST_TIME_DATA);
                }
            }

            if(container.getAbilityValue())
            {
                BackpackAbilities.ABILITIES.abilityTick(CapabilityUtils.getWearingBackpack(player), player, null);
            }
        }
    }

    public static void openGUI(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID)
    {
        if(!serverPlayerEntity.level.isClientSide)
        {
            if(screenID == Reference.ITEM_SCREEN_ID)
            {
                NetworkHooks.openGui(serverPlayerEntity, new TravelersBackpackContainer(stack, serverPlayerEntity, screenID), packetBuffer -> packetBuffer.writeByte(screenID));
            }

            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                NetworkHooks.openGui(serverPlayerEntity, CapabilityUtils.getBackpackInv(serverPlayerEntity), packetBuffer -> packetBuffer.writeByte(screenID));
            }
        }
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inventory, Player player)
    {
        return new TravelersBackpackItemMenu(windowID, inventory, this);
    }

    private ItemStackHandler createHandler(int size, boolean isInventory)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setDataChanged(COMBINED_INVENTORY_DATA);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return BackpackSlotItemHandler.isItemValid(stack);
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                if(isInventory)
                {
                    setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackContainer.this.tier.getStorageSlots());
                    ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
                    for (int i = 0; i < tagList.size(); i++)
                    {
                        CompoundTag itemTags = tagList.getCompound(i);
                        int slot = itemTags.getInt("Slot");

                        if (slot >= 0 && slot < stacks.size())
                        {
                            stacks.set(slot, ItemStack.of(itemTags));
                        }
                    }
                    onLoad();
                }
                else
                {
                    super.deserializeNBT(nbt);
                }
            }
        };
    }

    private ItemStackHandler createToolsHandler(int size)
    {
        return new ItemStackHandler(size)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setDataChanged(TOOLS_DATA);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return ToolSlotItemHandler.isValid(stack);
            }

            @Override
            public void deserializeNBT(CompoundTag nbt)
            {
                setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackContainer.this.tier.getToolSlots());
                ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
                for(int i = 0; i < tagList.size(); i++)
                {
                    CompoundTag itemTags = tagList.getCompound(i);
                    int slot = itemTags.getInt("Slot");

                    if(slot >= 0 && slot < stacks.size())
                    {
                        stacks.set(slot, ItemStack.of(itemTags));
                    }
                }
                onLoad();
            }
        };
    }

    private FluidTank createFluidHandler(int capacity)
    {
        return new FluidTank(capacity)
        {
            @Override
            protected void onContentsChanged()
            {
                setDataChanged(TANKS_DATA);
            }

            @Override
            public FluidTank readFromNBT(CompoundTag nbt)
            {
                setCapacity(nbt.contains("Capacity", 3) ? nbt.getInt("Capacity") : TravelersBackpackContainer.this.tier.getTankCapacity());
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setFluid(fluid);
                return this;
            }

            @Override
            public CompoundTag writeToNBT(CompoundTag nbt)
            {
                if(!nbt.contains("Capacity", 3)) nbt.putInt("Capacity", TravelersBackpackContainer.this.tier.getTankCapacity());
                fluid.writeToNBT(nbt);
                return nbt;
            }
        };
    }
}