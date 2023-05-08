package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.SlotManager;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ItemStackUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory, INamedContainerProvider
{
    private final ItemStackHandler inventory = createHandler(Tiers.LEATHER.getStorageSlots(), true);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE, false);
    private final FluidTank leftTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final FluidTank rightTank = createFluidHandler(Tiers.LEATHER.getTankCapacity());
    private final SlotManager slotManager = new SlotManager(this);
    private final PlayerEntity player;
    private ItemStack stack;
    private Tiers.Tier tier;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String COLOR = "Color";
    private final String SLEEPING_BAG_COLOR = "SleepingBagColor";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";

    public TravelersBackpackInventory(ItemStack stack, PlayerEntity player, byte screenID)
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

    public void loadTier(CompoundNBT compound)
    {
        if(!compound.contains(Tiers.TIER))
        {
            compound.putString(Tiers.TIER, Tiers.LEATHER.getName());
        }
        this.tier = Tiers.of(compound.getString(Tiers.TIER));
    }

    @Override
    public ItemStackHandler getInventory()
    {
        return this.inventory;
    }

    @Override
    public ItemStackHandler getCraftingGridInventory()
    {
        return this.craftingInventory;
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
    public void saveAllData(CompoundNBT compound)
    {
        this.saveTanks(compound);
        this.saveItems(compound);
        this.saveAbility(compound);
        this.saveTime(compound);
        this.slotManager.saveUnsortableSlots(compound);
        this.slotManager.saveMemorySlots(compound);
    }

    @Override
    public void loadAllData(CompoundNBT compound)
    {
        this.loadTier(compound);
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadAbility(compound);
        this.loadTime(compound);
        this.slotManager.loadUnsortableSlots(compound);
        this.slotManager.loadMemorySlots(compound);
    }

    @Override
    public void saveItems(CompoundNBT compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundNBT compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
        this.craftingInventory.deserializeNBT(compound.getCompound(CRAFTING_INVENTORY));
    }

    @Override
    public void saveTanks(CompoundNBT compound)
    {
        compound.put(LEFT_TANK, this.leftTank.writeToNBT(new CompoundNBT()));
        compound.put(RIGHT_TANK, this.rightTank.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void loadTanks(CompoundNBT compound)
    {
        this.leftTank.readFromNBT(compound.getCompound(LEFT_TANK));
        this.rightTank.readFromNBT(compound.getCompound(RIGHT_TANK));
    }

    @Override
    public void saveColor(CompoundNBT compound) {}
    @Override
    public void loadColor(CompoundNBT compound) {}

    @Override
    public void saveSleepingBagColor(CompoundNBT compound) {}

    @Override
    public void loadSleepingBagColor(CompoundNBT compound) {}

    @Override
    public void saveAbility(CompoundNBT compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundNBT compound)
    {
        this.ability = !compound.contains(ABILITY) && TravelersBackpackConfig.forceAbilityEnabled || compound.getBoolean(ABILITY);
    }

    @Override
    public void saveTime(CompoundNBT compound)
    {
        compound.putInt(LAST_TIME, this.lastTime);
    }

    @Override
    public void loadTime(CompoundNBT compound)
    {
        this.lastTime = compound.getInt(LAST_TIME);
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_LEFT), player) || InventoryActions.transferContainerTank(this, getRightTank(), this.tier.getSlotIndex(Tiers.SlotType.BUCKET_IN_RIGHT), player);
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

    @Override
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
        return TravelersBackpackConfig.enableBackpackAbilities ? this.ability : false;
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
    public Tiers.Tier getTier()
    {
        return this.tier;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = ItemStackUtils.getAndSplit(getInventory(), index, count);

        if(!itemstack.isEmpty())
        {
            setDataChanged(COMBINED_INVENTORY_DATA);
        }
        return itemstack;
    }

    @Override
    public World getLevel()
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
    public void setUsingPlayer(@Nullable PlayerEntity player) {}

    @Override
    public void setDataChanged(byte... dataIds)
    {
        if(getLevel().isClientSide) return;

        for(byte data : dataIds)
        {
            switch(data)
            {
                case INVENTORY_DATA: stack.getOrCreateTag().put(INVENTORY, this.inventory.serializeNBT());
                case CRAFTING_INVENTORY_DATA: stack.getOrCreateTag().put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
                case COMBINED_INVENTORY_DATA: saveItems(stack.getOrCreateTag());
                case TANKS_DATA: saveTanks(stack.getOrCreateTag());
                case COLOR_DATA: saveColor(stack.getOrCreateTag());
                case SLEEPING_BAG_COLOR_DATA: saveSleepingBagColor(this.stack.getOrCreateTag());
                case ABILITY_DATA: saveAbility(stack.getOrCreateTag());
                case LAST_TIME_DATA: saveTime(stack.getOrCreateTag());
                case SLOT_DATA: slotManager.saveUnsortableSlots(stack.getOrCreateTag());
                                slotManager.saveMemorySlots(this.stack.getOrCreateTag());
                case ALL_DATA: saveAllData(stack.getOrCreateTag());
            }
        }
        sendPackets();
    }

    @Override
    public void setDataChanged() {}

    @Override
    public ITextComponent getDisplayName()
    {
        return new TranslationTextComponent("screen.travelersbackpack.item");
    }

    public static void abilityTick(PlayerEntity player)
    {
        if(player.isAlive() && CapabilityUtils.isWearingBackpack(player) && BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_LIST, CapabilityUtils.getWearingBackpack(player)))
        {
            TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);

            if(!player.level.isClientSide)
            {
                if(inv.getLastTime() > 0)
                {
                    inv.setLastTime(inv.getLastTime() - 1);
                    inv.setDataChanged(LAST_TIME_DATA);
                }
            }

            if(inv.getAbilityValue())
            {
                BackpackAbilities.ABILITIES.abilityTick(CapabilityUtils.getWearingBackpack(player), player, null);
            }
        }
    }

    public static void openGUI(ServerPlayerEntity serverPlayerEntity, ItemStack stack, byte screenID)
    {
        if(!serverPlayerEntity.level.isClientSide)
        {
            if(screenID == Reference.ITEM_SCREEN_ID)
            {
                NetworkHooks.openGui(serverPlayerEntity, new TravelersBackpackInventory(stack, serverPlayerEntity, screenID), packetBuffer -> packetBuffer.writeByte(screenID));
            }

            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                NetworkHooks.openGui(serverPlayerEntity, CapabilityUtils.getBackpackInv(serverPlayerEntity), packetBuffer -> packetBuffer.writeByte(screenID));
            }
        }
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity)
    {
        return new TravelersBackpackItemContainer(windowID, playerInventory, this);
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
                return !(stack.getItem() instanceof TravelersBackpackItem);
            }

            @Override
            public void deserializeNBT(CompoundNBT nbt)
            {
                if(isInventory)
                {
                    //Prevents losing items if updated from previous version
                    if(TravelersBackpackInventory.this.getTier() == Tiers.LEATHER)
                    {
                        int size = nbt.contains("Size", Constants.NBT.TAG_INT) ? nbt.getInt("Size") : stacks.size();

                        if(size == Reference.INVENTORY_SIZE)
                        {
                            TravelersBackpackInventory.this.tier = Tiers.DIAMOND;
                            CompoundNBT tag = TravelersBackpackInventory.this.stack.getOrCreateTag().copy();
                            tag.putString(Tiers.TIER, Tiers.DIAMOND.getName());
                            TravelersBackpackInventory.this.stack.setTag(tag);
                        }
                    }

                    setSize(TravelersBackpackInventory.this.tier.getStorageSlots());
                    ListNBT tagList = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < tagList.size(); i++)
                    {
                        CompoundNBT itemTags = tagList.getCompound(i);
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
            public FluidTank readFromNBT(CompoundNBT nbt)
            {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
                setCapacity(TravelersBackpackInventory.this.tier.getTankCapacity());
                setFluid(fluid);
                return this;
            }
        };
    }
}