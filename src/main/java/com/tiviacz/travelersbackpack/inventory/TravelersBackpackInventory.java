package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.inventory.screen.slot.ToolSlot;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.inventory.sorter.wrappers.CombinedInvWrapper;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory
{
    private final InventoryImproved inventory = createInventory(Tiers.LEATHER.getStorageSlots(), true);
    private final InventoryImproved craftingInventory = createInventory(9, false);
    private final InventoryImproved toolSlots = createToolsInventory(Tiers.LEATHER.getToolSlots());
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
            this.readAllData(stack.getOrCreateNbt());
        }
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }

    public void readTier(NbtCompound compound)
    {
        if(!compound.contains(TIER))
        {
            compound.putInt(TIER, TravelersBackpackConfig.getConfig().backpackSettings.enableTierUpgrades ? Tiers.LEATHER.getOrdinal() : Tiers.DIAMOND.getOrdinal());
        }
        if(compound.contains(TIER, NbtElement.STRING_TYPE))
        {
            Tiers.Tier tier = Tiers.of(compound.getString(TIER));
            compound.remove(TIER);
            compound.putInt(TIER, tier.getOrdinal());
        }
        this.tier = Tiers.of(compound.getInt(TIER));
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

    @Override
    public void writeItems(NbtCompound compound)
    {
        compound.put(INVENTORY, this.inventory.writeNbt());
        compound.put(TOOLS_INVENTORY, this.toolSlots.writeNbt());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.writeNbt());
    }

    @Override
    public void readItems(NbtCompound compound)
    {
        this.inventory.readNbt(compound.getCompound(INVENTORY));
        this.toolSlots.readNbt(compound.getCompound(TOOLS_INVENTORY));
        this.craftingInventory.readNbt(compound.getCompound(CRAFTING_INVENTORY));

        //#TODO clean up in 1.21, also remove from readNBT in InventoryImproved
        //Read from old NBT
        if(compound.contains(INVENTORY, NbtElement.LIST_TYPE))
        {
            this.inventory.readNbtOld(compound, true);
        }

        //Read from old NBT
        if(compound.contains(CRAFTING_INVENTORY, NbtElement.LIST_TYPE))
        {
            this.craftingInventory.readNbtOld(compound, false);
        }
    }

    @Override
    public void writeTanks(NbtCompound compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNbt(new NbtCompound()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNbt(new NbtCompound()));

        //#TODO clean up in 1.21, old data converter
        if(compound.contains(LEFT_TANK_AMOUNT)) compound.remove(LEFT_TANK_AMOUNT);
        if(compound.contains(RIGHT_TANK_AMOUNT)) compound.remove(RIGHT_TANK_AMOUNT);
    }

    @Override
    public void readTanks(NbtCompound compound)
    {
        this.leftTank.readNbt(compound.getCompound(LEFT_TANK));
        this.rightTank.readNbt(compound.getCompound(RIGHT_TANK));

        //#TODO clean up in 1.21, also remove from readNBT in FluidTank
        //Read from old NBT
        if(compound.contains(LEFT_TANK_AMOUNT, NbtElement.LONG_TYPE))
        {
            this.leftTank.readOldNbt(compound, true);
        }

        //Read from old NBT
        if(compound.contains(RIGHT_TANK_AMOUNT, NbtElement.LONG_TYPE))
        {
            this.rightTank.readOldNbt(compound, false);
        }
    }

    @Override
    public void writeColor(NbtCompound compound) {}

    @Override
    public void readColor(NbtCompound compound) {}

    @Override
    public void writeSleepingBagColor(NbtCompound compound) {}

    @Override
    public void readSleepingBagColor(NbtCompound compound) {}

    @Override
    public void writeAbility(NbtCompound compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void readAbility(NbtCompound compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.getConfig().backpackAbilities.forceAbilityEnabled || compound.getBoolean(ABILITY);
    }
    @Override
    public void writeTime(NbtCompound compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void readTime(NbtCompound compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    @Override
    public void writeAllData(NbtCompound compound)
    {
        writeItems(compound);
        writeTanks(compound);
        writeAbility(compound);
        writeTime(compound);
        this.slotManager.writeUnsortableSlots(compound);
        this.slotManager.writeMemorySlots(compound);
        this.settingsManager.writeSettings(compound);
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readTier(compound);
        readItems(compound);
        readTanks(compound);
        readAbility(compound);
        readTime(compound);
        this.slotManager.readUnsortableSlots(compound);
        this.slotManager.readMemorySlots(compound);
        this.settingsManager.readSettings(compound);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), 0, this.player) || InventoryActions.transferContainerTank(this, getRightTank(), 2, this.player);
    }

    public void sendPackets()
    {
        if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            ComponentUtils.sync(this.player);
        }
    }

    @Override
    public boolean hasColor()
    {
        return stack.getOrCreateNbt().contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return stack.getOrCreateNbt().getInt(COLOR);
        }
        return 0;
    }

    @Override
    public boolean hasSleepingBagColor()
    {
        return this.stack.getOrCreateNbt().contains(SLEEPING_BAG_COLOR);
    }

    @Override
    public int getSleepingBagColor()
    {
        if(hasSleepingBagColor())
        {
            return this.stack.getOrCreateNbt().getInt(SLEEPING_BAG_COLOR);
        }
        return DyeColor.RED.getId();
    }

    @Override
    public boolean getAbilityValue()
    {
        return TravelersBackpackConfig.getConfig().backpackAbilities.enableBackpackAbilities ? (BackpackAbilities.ALLOWED_ABILITIES.contains(getItemStack().getItem()) ? this.ability : false) : false;
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
    public World getWorld()
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
    }

    @Override
    public void setUsingPlayer(@Nullable PlayerEntity player) {}

    @Override
    public void markDataDirty(byte... dataIds)
    {
        if(getWorld().isClient || stack == null) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                case INVENTORY_DATA: stack.getOrCreateNbt().put(INVENTORY, inventory.writeNbt());
                case TOOLS_DATA: stack.getOrCreateNbt().put(TOOLS_INVENTORY, toolSlots.writeNbt());
                case CRAFTING_INVENTORY_DATA: stack.getOrCreateNbt().put(CRAFTING_INVENTORY, craftingInventory.writeNbt());
                case COMBINED_INVENTORY_DATA: writeItems(stack.getOrCreateNbt());
                case TANKS_DATA: writeTanks(stack.getOrCreateNbt());
                case COLOR_DATA: writeColor(stack.getOrCreateNbt());
                case SLEEPING_BAG_COLOR_DATA: writeSleepingBagColor(stack.getOrCreateNbt());
                case ABILITY_DATA: writeAbility(stack.getOrCreateNbt());
                case LAST_TIME_DATA: writeTime(stack.getOrCreateNbt());
                case SLOT_DATA: slotManager.writeUnsortableSlots(stack.getOrCreateNbt());
                                slotManager.writeMemorySlots(stack.getOrCreateNbt());
                case SETTINGS_DATA: settingsManager.writeSettings(stack.getOrCreateNbt());
                case ALL_DATA: writeAllData(stack.getOrCreateNbt());
            }
        }
        sendPackets();
    }

    @Override
    public void markDirty() {}

    public static void abilityTick(PlayerEntity player)
    {
        if(player.isAlive() && ComponentUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, ComponentUtils.getWearingBackpack(player)))
        {
            TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);

            if(!inv.getWorld().isClient)
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
            player.openHandledScreen(new ExtendedScreenHandlerFactory()
            {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                {
                    buf.writeByte(screenID);
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

    public InventoryImproved createInventory(int size, boolean isInventory)
    {
        return new InventoryImproved(size)
        {
            @Override
            public void markDirty()
            {
                if(isInventory)
                {
                    markDataDirty(INVENTORY_DATA);
                }
                else
                {
                    markDataDirty(CRAFTING_INVENTORY_DATA);
                }
            }

            @Override
            public void readNbt(NbtCompound nbt)
            {
                if(isInventory)
                {
                    this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackInventory.this.tier.getStorageSlots());
                    NbtList tagList = nbt.getList("Items", 10);

                    for(int i = 0; i < tagList.size(); ++i)
                    {
                        NbtCompound itemTags = tagList.getCompound(i);
                        int slot = itemTags.getInt("Slot");
                        if(slot >= 0 && slot < this.stacks.size())
                        {
                            this.stacks.set(slot, ItemStack.fromNbt(itemTags));
                        }
                    }
                }
                else
                {
                    super.readNbt(nbt);
                }
            }
        };
    }

    private InventoryImproved createToolsInventory(int size)
    {
        return new InventoryImproved(size)
        {
            @Override
            public void markDirty()
            {
                markDataDirty(TOOLS_DATA);
            }

            @Override
            public boolean isValid(int slot, ItemStack stack)
            {
                return ToolSlot.isValid(stack);
            }

            @Override
            public void readNbt(NbtCompound nbt)
            {
                this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : TravelersBackpackInventory.this.tier.getToolSlots());
                NbtList tagList = nbt.getList("Items", 10);

                for(int i = 0; i < tagList.size(); ++i)
                {
                    NbtCompound itemTags = tagList.getCompound(i);
                    int slot = itemTags.getInt("Slot");
                    if(slot >= 0 && slot < this.stacks.size())
                    {
                        this.stacks.set(slot, ItemStack.fromNbt(itemTags));
                    }
                }
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

            @Override
            public FluidTank readNbt(NbtCompound nbt)
            {
                setCapacity(nbt.contains("capacity") ? nbt.getLong("capacity") : TravelersBackpackInventory.this.tier.getTankCapacity());
                this.variant = FluidVariantImpl.fromNbt(nbt.getCompound("variant"));
                this.amount = nbt.getLong("amount");
                return this;
            }
        };
    }
}