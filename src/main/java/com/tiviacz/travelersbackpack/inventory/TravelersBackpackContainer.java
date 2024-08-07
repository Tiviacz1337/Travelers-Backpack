package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.BackpackSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.menu.slot.ToolSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.network.ClientboundSyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TravelersBackpackContainer implements ITravelersBackpackContainer, MenuProvider, Nameable
{
    private ItemStackHandler inventory = createHandler(NonNullList.withSize(Tiers.LEATHER.getStorageSlots(), ItemStack.EMPTY), true);
    private ItemStackHandler craftingInventory = createHandler(NonNullList.withSize(9, ItemStack.EMPTY), false);
    private ItemStackHandler toolSlots = createToolsHandler(NonNullList.withSize(Tiers.LEATHER.getToolSlots(), ItemStack.EMPTY));
    private final ItemStackHandler fluidSlots = createTemporaryHandler();
    private final FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private Player player;
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

        if(!this.stack.isEmpty())
        {
            this.loadAllData();
        }
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void loadTier()
    {
        this.tier = Tiers.of(this.stack.getOrDefault(ModDataComponents.TIER, 0));
    }

    @Override
    public ItemStackHandler getHandler()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getToolSlotsHandler()
    {
        return this.toolSlots;
    }

    @Override
    public ItemStackHandler getCraftingGridHandler()
    {
        return this.craftingInventory;
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

    public void saveAllData()
    {
        this.saveTanks();
        this.saveItems();
        this.saveTime();
        this.saveAbility();
        this.slotManager.saveUnsortableSlots(this.stack);
        this.slotManager.saveMemorySlots(this.stack);
        this.settingsManager.saveSettings(this.stack);
    }

    public void loadAllData()
    {
        this.loadTier();
        this.loadTanks();
        this.loadItems();
        this.loadTime();
        this.loadAbility();
        this.slotManager.loadUnsortableSlots(this.stack);
        this.slotManager.loadMemorySlots(this.stack);
        this.settingsManager.loadSettings(this.stack);
    }

    public void saveItems()
    {
        this.stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), itemsToList(this.stack.has(ModDataComponents.BACKPACK_CONTAINER.get()) ? this.inventory.getSlots() : this.tier.getStorageSlots(), this.inventory));
        this.stack.set(ModDataComponents.CRAFTING_CONTAINER.get(), itemsToList(9, this.craftingInventory));
        this.stack.set(ModDataComponents.TOOLS_CONTAINER.get(), itemsToList(this.stack.has(ModDataComponents.TOOLS_CONTAINER.get()) ? this.toolSlots.getSlots() : this.tier.getToolSlots(), this.toolSlots));
    }

    public void loadItems()
    {
        this.inventory = createHandler(this.stack.getOrDefault(ModDataComponents.BACKPACK_CONTAINER, BackpackContainerContents.fromItems(this.tier.getStorageSlots(), NonNullList.withSize(this.tier.getStorageSlots(), ItemStack.EMPTY))).getItems(), true);
        this.toolSlots = createToolsHandler(this.stack.getOrDefault(ModDataComponents.TOOLS_CONTAINER, BackpackContainerContents.fromItems(this.tier.getToolSlots(), NonNullList.withSize(this.tier.getToolSlots(), ItemStack.EMPTY))).getItems());
        this.craftingInventory = createHandler(this.stack.getOrDefault(ModDataComponents.CRAFTING_CONTAINER, BackpackContainerContents.fromItems(9, NonNullList.withSize(9, ItemStack.EMPTY))).getItems(), false);
        //if(this.stack.has(ModDataComponents.CRAFTING_CONTAINER))
    }

    public void saveTanks()
    {
        this.stack.set(ModDataComponents.FLUID_TANKS.get(), new FluidTanks(this.leftTank.getCapacity(), this.leftTank.getFluid(), this.rightTank.getFluid()));
    }

    public void loadTanks()
    {
        FluidTanks tanks = this.stack.getOrDefault(ModDataComponents.FLUID_TANKS.get(), FluidTanks.createTanks(this.tier.getTankCapacity()));

        //Left Tank
        this.leftTank.setCapacity(tanks.capacity());
        this.leftTank.setFluid(tanks.leftFluidStack());

        //Right Tank
        this.rightTank.setCapacity(tanks.capacity());
        this.rightTank.setFluid(tanks.rightFluidStack());
    }

    public void saveAbility()
    {
        this.stack.set(ModDataComponents.ABILITY.get(), this.ability);
    }

    public void loadAbility()
    {
        this.ability = this.stack.getOrDefault(ModDataComponents.ABILITY, TravelersBackpackConfig.SERVER.backpackAbilities.forceAbilityEnabled.get());
    }

    public void saveTime()
    {
        this.stack.set(ModDataComponents.LAST_TIME.get(), this.lastTime);
    }

    public void loadTime()
    {
        this.lastTime = this.stack.getOrDefault(ModDataComponents.LAST_TIME.get(), 0);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), 0, player) || InventoryActions.transferContainerTank(this, getRightTank(), 2, player);
    }

    @Override
    public boolean hasColor()
    {
        return this.stack.has(DataComponents.DYED_COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return this.stack.get(DataComponents.DYED_COLOR).rgb();
        }
        return 0;
    }

    public boolean hasSleepingBagColor()
    {
        return this.stack.has(ModDataComponents.SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrDefault(ModDataComponents.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
        }
        return DyeColor.RED.getId();
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.SERVER.backpackAbilities.enableBackpackAbilities.get() ? (BackpackAbilities.ALLOWED_ABILITIES.contains(getItemStack().getItem()) ? this.ability : false) : false;
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
    public boolean hasBlockEntity()
    {
        return false;
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
        return this.player.level();
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
    public void setUsingPlayer(@Nullable Player player)
    {
        this.player = player;
    }

    @Override
    public void setDataChanged(byte... dataIds)
    {
        if(getLevel().isClientSide) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                //case INVENTORY_DATA: this.stack.set(ModDataComponents.BACKPACK_CONTAINER.get(), itemsToList(this.stack.has(ModDataComponents.BACKPACK_CONTAINER.get()) ? this.inventory.getSlots() : this.tier.getStorageSlots(), this.inventory)); break;
                //case TOOLS_DATA: this.stack.set(ModDataComponents.TOOLS_CONTAINER.get(), itemsToList(this.toolSlots.getSlots(), this.toolSlots)); break;
                //case CRAFTING_INVENTORY_DATA: this.stack.set(ModDataComponents.CRAFTING_CONTAINER.get(), itemsToList(9, this.craftingInventory)); break;
                case TANKS_DATA: saveTanks(); break;
                case ABILITY_DATA: saveAbility(); break;
                case LAST_TIME_DATA: saveTime(); break;
                case SLOT_DATA: slotManager.saveUnsortableSlots(this.stack);
                                slotManager.saveMemorySlots(this.stack); sendMemorySlotsToClient(); break;
                case SETTINGS_DATA: settingsManager.saveSettings(this.stack); break;
                case ALL_DATA: saveAllData(); break;
            }
        }
        sendPackets();
    }

    public void setSlotChanged(int index, ItemStack stack, byte dataId)
    {
        switch(dataId)
        {
            case INVENTORY_DATA: this.stack.update(ModDataComponents.BACKPACK_CONTAINER, new BackpackContainerContents(this.getTier().getStorageSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot); break;
            case CRAFTING_INVENTORY_DATA: this.stack.update(ModDataComponents.CRAFTING_CONTAINER, new BackpackContainerContents(9), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot); break;
            case TOOLS_DATA: this.stack.update(ModDataComponents.TOOLS_CONTAINER, new BackpackContainerContents(this.tier.getToolSlots()), new BackpackContainerContents.Slot(index, stack), BackpackContainerContents::updateSlot); break;
        }
        sendPackets();
    }

    public void sendMemorySlotsToClient()
    {
        if(this.player != null && !getLevel().isClientSide && this.screenID == Reference.ITEM_SCREEN_ID)
        {
            this.player.containerMenu.sendAllDataToRemote();
        }
    }

    //Sync ItemStack Components on client
    public void sendPackets()
    {
        if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            //Stop updating stack if player is changing settings
            if(this.slotManager.isSelectorActive(SlotManager.MEMORY) || this.slotManager.isSelectorActive(SlotManager.UNSORTABLE)) return;

            AttachmentUtils.synchronise(player);
            AttachmentUtils.synchroniseToOthers(player);
        }

        if(screenID == Reference.ITEM_SCREEN_ID && player instanceof ServerPlayer serverPlayer)
        {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new ClientboundSyncItemStackPacket(serverPlayer.getId(), this.stack));
        }
    }

    public BackpackContainerContents itemsToList(int size, ItemStackHandler handler)
    {
        List<ItemStack> list = new ArrayList<>(size);

        for(int i = 0; i < handler.getSlots(); i++)
        {
            list.add(handler.getStackInSlot(i));
        }
        for(int i = handler.getSlots(); i < size; i++)
        {
            list.add(ItemStack.EMPTY);
        }
        return BackpackContainerContents.fromItems(size, list);
    }

    @Override
    public void setDataChanged() {}

    @Override
    public Component getName()
    {
        return Component.translatable("screen.travelersbackpack.item");
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable("screen.travelersbackpack.item");
    }

    public static void abilityTick(Player player)
    {
        if(player.isAlive() && AttachmentUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, AttachmentUtils.getWearingBackpack(player)))
        {
            TravelersBackpackContainer container = AttachmentUtils.getBackpackInv(player);

            if(!player.level().isClientSide)
            {
                if(container.getLastTime() > 0)
                {
                    container.setLastTime(container.getLastTime() - 1);
                    container.setDataChanged(LAST_TIME_DATA);
                }
            }

            if(container.getAbilityValue())
            {
                BackpackAbilities.ABILITIES.abilityTick(AttachmentUtils.getWearingBackpack(player), player, null);
            }
        }
    }

    public static void openGUI(ServerPlayer serverPlayerEntity, ItemStack stack, byte screenID)
    {
        if(!serverPlayerEntity.level().isClientSide)
        {
            if(screenID == Reference.ITEM_SCREEN_ID)
            {
                serverPlayerEntity.openMenu(new TravelersBackpackContainer(stack, serverPlayerEntity, screenID), packetBuffer -> packetBuffer.writeByte(screenID));
            }

            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                serverPlayerEntity.openMenu(AttachmentUtils.getBackpackInv(serverPlayerEntity), packetBuffer -> packetBuffer.writeByte(screenID).writeBoolean(false));
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inventory, Player player)
    {
        return new TravelersBackpackItemMenu(windowID, inventory, this);
    }

    private ItemStackHandler createHandler(NonNullList<ItemStack> stacks, boolean isInventory)
    {
        return new ItemStackHandler(stacks)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                if(isInventory)
                {
                    setSlotChanged(slot, getStackInSlot(slot), INVENTORY_DATA);
                }
                else
                {
                    setSlotChanged(slot, getStackInSlot(slot), CRAFTING_INVENTORY_DATA);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return BackpackSlotItemHandler.isItemValid(stack);
            }
        };
    }

    private ItemStackHandler createToolsHandler(NonNullList<ItemStack> items)
    {
        return new ItemStackHandler(items)
        {
            @Override
            protected void onContentsChanged(int slot)
            {
                setSlotChanged(slot, getStackInSlot(slot), TOOLS_DATA);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return ToolSlotItemHandler.isValid(stack);
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
        };
    }
}