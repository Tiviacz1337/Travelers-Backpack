package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.components.BackpackContainerComponent;
import com.tiviacz.travelersbackpack.components.FluidTanks;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModComponentTypes;
import com.tiviacz.travelersbackpack.init.ModScreenHandlerTypes;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.CombinedInvWrapper;
import com.tiviacz.travelersbackpack.network.SyncItemStackPacket;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory
{
    private InventoryImproved inventory = createInventory(DefaultedList.ofSize(Tiers.LEATHER.getStorageSlots(), ItemStack.EMPTY), true);
    private InventoryImproved craftingInventory = createInventory(DefaultedList.ofSize(9, ItemStack.EMPTY), false);
    private InventoryImproved toolSlots = createToolsInventory(DefaultedList.ofSize(Tiers.LEATHER.getToolSlots(), ItemStack.EMPTY));
    private final InventoryImproved fluidSlots = createTemporaryInventory();
    private final FluidTank leftTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidTank(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final SettingsManager settingsManager = new SettingsManager(this);
    private final PlayerEntity player;
    private ItemStack stack;
    private Tiers.Tier tier;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    public TravelersBackpackInventory(@Nullable ItemStack stack, PlayerEntity player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        if(stack != null)
        {
            this.readAllData();
        }
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void readTier()
    {
        this.tier = Tiers.of(this.stack.getOrDefault(ModComponentTypes.TIER, 0));
    }

    @Override
    public InventoryImproved getInventory()
    {
        return this.inventory;
    }

    @Override
    public InventoryImproved getToolSlotsInventory()
    {
        return this.toolSlots;
    }

    @Override
    public InventoryImproved getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public InventoryImproved getFluidSlotsInventory()
    {
        return this.fluidSlots;
    }

    @Override
    public Inventory getCombinedInventory()
    {
        return new CombinedInvWrapper(this, getInventory(), getToolSlotsInventory(), getFluidSlotsInventory(), getCraftingGridInventory());
    }

    @Override
    public FluidTank getLeftTank()
    {
        return leftTank;
    }

    @Override
    public FluidTank getRightTank() {
        return rightTank;
    }

    public void writeAllData()
    {
        this.writeItems();
        this.writeTanks();
        this.writeAbility();
        this.writeTime();
        this.slotManager.writeUnsortableSlots(this.stack);
        this.slotManager.writeMemorySlots(this.stack);
        this.settingsManager.writeSettings(this.stack);
    }

    public void readAllData()
    {
        this.readTier();
        this.readItems();
        this.readTanks();
        this.readAbility();
        this.readTime();
        this.slotManager.readUnsortableSlots(this.stack);
        this.slotManager.readMemorySlots(this.stack);
        this.settingsManager.readSettings(this.stack);
    }

    public void writeItems()
    {
        this.stack.set(ModComponentTypes.BACKPACK_CONTAINER, itemsToList(this.stack.contains(ModComponentTypes.BACKPACK_CONTAINER) ? this.inventory.size() : this.tier.getStorageSlots(), this.inventory));
        this.stack.set(ModComponentTypes.CRAFTING_CONTAINER, itemsToList(9, this.craftingInventory));
        this.stack.set(ModComponentTypes.TOOLS_CONTAINER, itemsToList(this.stack.contains(ModComponentTypes.TOOLS_CONTAINER) ? this.toolSlots.size() : this.tier.getToolSlots(), this.toolSlots));
    }

    public void readItems()
    {
        this.inventory = createInventory(this.stack.getOrDefault(ModComponentTypes.BACKPACK_CONTAINER, BackpackContainerComponent.fromStacks(this.tier.getStorageSlots(), DefaultedList.ofSize(this.tier.getStorageSlots(), ItemStack.EMPTY))).getStacks(), true);
        this.toolSlots = createToolsInventory(this.stack.getOrDefault(ModComponentTypes.TOOLS_CONTAINER, BackpackContainerComponent.fromStacks(this.tier.getToolSlots(), DefaultedList.ofSize(this.tier.getToolSlots(), ItemStack.EMPTY))).getStacks());
        this.craftingInventory = createInventory(this.stack.getOrDefault(ModComponentTypes.CRAFTING_CONTAINER, BackpackContainerComponent.fromStacks(9, DefaultedList.ofSize(9, ItemStack.EMPTY))).getStacks(), false);

        //if(this.stack.contains(ModComponentTypes.CRAFTING_CONTAINER))
        //{
            //this.craftingInventory = createInventory(this.stack.get(ModComponentTypes.CRAFTING_CONTAINER).getStacks(), false);
       // }
    }

    public void writeTanks()
    {
        this.stack.set(ModComponentTypes.FLUID_TANKS, new FluidTanks(this.leftTank.getCapacity(), new FluidTanks.Tank(this.leftTank.getResource(), this.leftTank.getAmount()), new FluidTanks.Tank(this.rightTank.getResource(), this.rightTank.getAmount())));
    }

    public void readTanks()
    {
        FluidTanks tanks = this.stack.getOrDefault(ModComponentTypes.FLUID_TANKS, FluidTanks.createTanks(this.tier.getTankCapacity()));

        //Left Tank
        this.leftTank.setCapacity(tanks.capacity());
        this.leftTank.variant = tanks.leftTank().fluidVariant();
        this.leftTank.amount = tanks.leftTank().amount();

        //Right Tank
        this.rightTank.setCapacity(tanks.capacity());
        this.rightTank.variant = tanks.rightTank().fluidVariant();
        this.rightTank.amount = tanks.rightTank().amount();
    }

    public void writeAbility()
    {
        this.stack.set(ModComponentTypes.ABILITY_SWITCH, this.ability);
    }

    public void readAbility()
    {
        this.ability = this.stack.getOrDefault(ModComponentTypes.ABILITY_SWITCH, TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled);
    }

    public void writeTime()
    {
        this.stack.set(ModComponentTypes.LAST_TIME, this.lastTime);
    }

    public void readTime()
    {
        this.lastTime = this.stack.getOrDefault(ModComponentTypes.LAST_TIME, 0);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), 0, this.player) || InventoryActions.transferContainerTank(this, getRightTank(), 2, this.player);
    }

    public BackpackContainerComponent itemsToList(int size, InventoryImproved inventory)
    {
        return BackpackContainerComponent.fromStacks(size, inventory.getStacks().stream().toList());
    }

    @Override
    public boolean hasColor()
    {
        return this.stack.contains(DataComponentTypes.DYED_COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return this.stack.get(DataComponentTypes.DYED_COLOR).rgb();
        }
        return 0;
    }

    @Override
    public boolean hasSleepingBagColor()
    {
        return this.stack.contains(ModComponentTypes.SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrDefault(ModComponentTypes.SLEEPING_BAG_COLOR, DyeColor.RED.getId());
        }
        return DyeColor.RED.getId();
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities ? (TravelersBackpackConfig.isAbilityAllowed(getItemStack()) ? this.ability : false) : false;
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
        return (int)Math.ceil((double)getInventory().size() / 9);
    }

    @Override
    public int getYOffset()
    {
        return 18 * Math.max(0, getRows() - 3);
    }

    @Override
    public boolean hasTileEntity()
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
    public World world()
    {
        return this.player.getWorld();
    }

    @Override
    public BlockPos getPosition()
    {
        return this.player.getBlockPos();
    }

    @Override
    public byte getScreenID()
    {
        return this.screenID;
    }

    @Override
    public ItemStack getItemStack()
    {
        return this.stack == null ? ItemStack.EMPTY : this.stack;
        //return this.stack;
    }

    @Override
    public void setUsingPlayer(@Nullable PlayerEntity player) {}

    @Override
    public void markDataDirty(byte... dataIds)
    {
        if(world().isClient || stack == null) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                //case INVENTORY_DATA: stack.getOrCreateNbt().put(INVENTORY, inventory.writeNbt());
              //  case INVENTORY_DATA: this.stack.set(ModComponentTypes.BACKPACK_CONTAINER, itemsToList(this.stack.contains(ModComponentTypes.BACKPACK_CONTAINER) ? this.inventory.size() : this.tier.getStorageSlots(), this.inventory)); break;
                //case TOOLS_DATA: stack.getOrCreateNbt().put(TOOLS_INVENTORY, toolSlots.writeNbt());
              //  case TOOLS_DATA: this.stack.set(ModComponentTypes.TOOLS_CONTAINER, itemsToList(this.toolSlots.size(), this.toolSlots)); break;
                //case CRAFTING_INVENTORY_DATA: stack.getOrCreateNbt().put(CRAFTING_INVENTORY, craftingInventory.writeNbt());
              //  case CRAFTING_INVENTORY_DATA: this.stack.set(ModComponentTypes.CRAFTING_CONTAINER, itemsToList(9, this.craftingInventory)); break;
                case TANKS_DATA: writeTanks(); break;
                case ABILITY_DATA: writeAbility(); break;
                case LAST_TIME_DATA: writeTime(); break;
                case SLOT_DATA: slotManager.writeUnsortableSlots(this.stack);
                    slotManager.writeMemorySlots(this.stack); sendMemorySlotsToClient(); break;
                case SETTINGS_DATA: settingsManager.writeSettings(this.stack); break;
                case ALL_DATA: writeAllData(); break;
            }
        }
        sendPackets();
    }

    public void markSlotDirty(int index, ItemStack stack, byte dataId)
    {
        switch(dataId)
        {
            case INVENTORY_DATA: this.stack.apply(ModComponentTypes.BACKPACK_CONTAINER, new BackpackContainerComponent(this.getTier().getStorageSlots()), new BackpackContainerComponent.Slot(index, stack), BackpackContainerComponent::updateSlot); break;
            case CRAFTING_INVENTORY_DATA: this.stack.apply(ModComponentTypes.CRAFTING_CONTAINER, new BackpackContainerComponent(9), new BackpackContainerComponent.Slot(index, stack), BackpackContainerComponent::updateSlot); break;
            case TOOLS_DATA: this.stack.apply(ModComponentTypes.TOOLS_CONTAINER, new BackpackContainerComponent(this.tier.getToolSlots()), new BackpackContainerComponent.Slot(index, stack), BackpackContainerComponent::updateSlot); break;
        }
        sendPackets();
    }

    public void sendMemorySlotsToClient()
    {
        if(this.player != null && !world().isClient && this.screenID == Reference.ITEM_SCREEN_ID)
        {
            this.player.currentScreenHandler.syncState();
        }
    }

    //Sync ItemStack Components on client
    public void sendPackets()
    {
        if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            //Stop updating stack if player is changing settings
            if(this.slotManager.isSelectorActive(SlotManager.MEMORY) || this.slotManager.isSelectorActive(SlotManager.UNSORTABLE)) return;

            ComponentUtils.sync(this.player);
        }

        if(screenID == Reference.ITEM_SCREEN_ID && player instanceof ServerPlayerEntity serverPlayer)
        {
            ServerPlayNetworking.send(serverPlayer, new SyncItemStackPacket(serverPlayer.getId(), this.stack));
        }
    }

    @Override
    public void markDirty() {}

    public static void abilityTick(PlayerEntity player)
    {
        if(player.isAlive() && ComponentUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

            if(!inv.world().isClient)
            {
                if(inv.getLastTime() > 0)
                {
                    inv.setLastTime(inv.getLastTime() - 1);
                    inv.markDataDirty(LAST_TIME_DATA);
                }
            }

            if(inv.getAbilityValue())
            {
                BackpackAbilities.ABILITIES.abilityTick(ComponentUtils.getWearingBackpack(player), player, null);
            }
        }
    }

    public static void openHandledScreen(PlayerEntity player, ItemStack stack, byte screenID)
    {
        if(!player.getWorld().isClient)
        {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<ModScreenHandlerTypes.ItemScreenData>()
            {
                @Override
                public ModScreenHandlerTypes.ItemScreenData getScreenOpeningData(ServerPlayerEntity player)
                {
                    return new ModScreenHandlerTypes.ItemScreenData(screenID, -1);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("screen.travelersbackpack.item");
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
                {
                    if(screenID == Reference.WEARABLE_SCREEN_ID)
                    {
                        return new TravelersBackpackItemScreenHandler(syncId, inv, ComponentUtils.getBackpackInv(player));
                    }
                    else
                    {
                        return new TravelersBackpackItemScreenHandler(syncId, inv, new TravelersBackpackInventory(stack, player, screenID));
                    }
                }
            });
        }
    }

    public InventoryImproved createInventory(DefaultedList<ItemStack> stacks, boolean isInventory)
    {
        return new InventoryImproved(stacks)
        {
            @Override
            public void markDirty() { }

            @Override
            public void onContentsChanged(int index)
            {
                if(isInventory)
                {
                    markSlotDirty(index, getStack(index), INVENTORY_DATA);
                }
                else
                {
                    markSlotDirty(index, getStack(index), CRAFTING_INVENTORY_DATA);
                }
            }
        };
    }

    private InventoryImproved createToolsInventory(DefaultedList<ItemStack> stacks)
    {
        return new InventoryImproved(stacks)
        {
            @Override
            public void markDirty() {}

            @Override
            public void onContentsChanged(int index)
            {
                markSlotDirty(index, getStack(index), TOOLS_DATA);
            }

            @Override
            public boolean isValid(int slot, ItemStack stack)
            {
                return ToolSlot.isValid(stack);
            }
        };
    }

    public FluidTank createFluidTank(long tankCapacity)
    {
        return new FluidTank(tankCapacity)
        {
            @Override
            protected void onFinalCommit()
            {
                markDataDirty(TANKS_DATA);
            }
        };
    }
}