package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.BackpackAbilities;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.ContainerUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TravelersBackpackContainer implements ITravelersBackpackContainer, MenuProvider, Nameable
{
    private final ItemStackHandler inventory = createHandler(Reference.INVENTORY_SIZE);
    private final ItemStackHandler craftingInventory = createHandler(Reference.CRAFTING_GRID_SIZE);
    private final FluidTank leftTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private final FluidTank rightTank = createFluidHandler(TravelersBackpackConfig.tanksCapacity);
    private final Player player;
    private ItemStack stack;
    private boolean ability;
    private int lastTime;
    private final byte screenID;

    private final String INVENTORY = "Inventory";
    private final String CRAFTING_INVENTORY = "CraftingInventory";
    private final String LEFT_TANK = "LeftTank";
    private final String RIGHT_TANK = "RightTank";
    private final String COLOR = "Color";
    private final String ABILITY = "Ability";
    private final String LAST_TIME = "LastTime";

    public TravelersBackpackContainer(ItemStack stack, Player player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        this.loadAllData(getTagCompound(stack));
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
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
        this.saveTime(compound);
        this.saveAbility(compound);
    }

    @Override
    public void loadAllData(CompoundTag compound)
    {
        this.loadTanks(compound);
        this.loadItems(compound);
        this.loadTime(compound);
        this.loadAbility(compound);
    }

    @Override
    public void saveItems(CompoundTag compound)
    {
        compound.put(INVENTORY, this.inventory.serializeNBT());
        compound.put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
    }

    @Override
    public void loadItems(CompoundTag compound)
    {
        this.inventory.deserializeNBT(compound.getCompound(INVENTORY));
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
    public void saveAbility(CompoundTag compound)
    {
        compound.putBoolean(ABILITY, this.ability);
    }

    @Override
    public void loadAbility(CompoundTag compound)
    {
        this.ability = compound.getBoolean(ABILITY);
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
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, player) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, player);
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
        return getTagCompound(this.stack).contains(COLOR);
    }

    @Override
    public int getColor()
    {
        if(hasColor())
        {
            return getTagCompound(this.stack).getInt(COLOR);
        }
        return 0;
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
    public CompoundTag getTagCompound(ItemStack stack)
    {
        if(stack.getTag() == null)
        {
            CompoundTag tag = new CompoundTag();
            stack.setTag(tag);
        }

        return stack.getTag();
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
    public ItemStack removeItem(int index, int count)
    {
        ItemStack stack = ContainerUtils.removeItem(getHandler(), index, count);
        if(!stack.isEmpty())
        {
            setDataChanged(COMBINED_INVENTORY_DATA);
        }
        return stack;
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
                case INVENTORY_DATA: getTagCompound(stack).put(INVENTORY, this.inventory.serializeNBT());
                case CRAFTING_INVENTORY_DATA: getTagCompound(stack).put(CRAFTING_INVENTORY, this.craftingInventory.serializeNBT());
                case COMBINED_INVENTORY_DATA: saveItems(getTagCompound(stack));
                case TANKS_DATA: saveTanks(getTagCompound(stack));
                case COLOR_DATA: saveColor(getTagCompound(stack));
                case ABILITY_DATA: saveAbility(getTagCompound(stack));
                case LAST_TIME_DATA: saveTime(getTagCompound(stack));
                case ALL_DATA: saveAllData(getTagCompound(stack));
            }
        }
        sendPackets();
    }

    @Override
    public void setChanged() {}

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
        if(player.isAlive() && CapabilityUtils.isWearingBackpack(player))
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
                NetworkHooks.openScreen(serverPlayerEntity, new TravelersBackpackContainer(stack, serverPlayerEntity, screenID), packetBuffer -> packetBuffer.writeByte(screenID));
            }

            if(screenID == Reference.WEARABLE_SCREEN_ID)
            {
                NetworkHooks.openScreen(serverPlayerEntity, CapabilityUtils.getBackpackInv(serverPlayerEntity), packetBuffer -> packetBuffer.writeByte(screenID));
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inventory, Player player)
    {
        return new TravelersBackpackItemMenu(windowID, inventory, this);
    }

    private ItemStackHandler createHandler(int size)
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