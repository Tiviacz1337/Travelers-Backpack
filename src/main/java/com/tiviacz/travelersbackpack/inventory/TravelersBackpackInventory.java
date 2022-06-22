package com.tiviacz.travelersbackpack.inventory;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.util.InventoryUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TravelersBackpackInventory implements ITravelersBackpackInventory
{
    private InventoryImproved inventory = createInventory(Reference.INVENTORY_SIZE);
    private InventoryImproved craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
    public SingleVariantStorage<FluidVariant> leftTank = createFluidTank(TravelersBackpackConfig.tanksCapacity);
    public SingleVariantStorage<FluidVariant> rightTank = createFluidTank(TravelersBackpackConfig.tanksCapacity);
    private final PlayerEntity player;
    private final ItemStack stack;
    private int lastTime;
    private final byte screenID;
    private final String LEFT_TANK = "LeftTank";
    private final String LEFT_TANK_AMOUNT = "LeftTankAmount";
    private final String RIGHT_TANK = "RightTank";
    private final String RIGHT_TANK_AMOUNT = "RightTankAmount";
    private final String LAST_TIME = "LastTime";
    private final String COLOR = "Color";

    public TravelersBackpackInventory(ItemStack stack, PlayerEntity player, byte screenID)
    {
        this.player = player;
        this.stack = stack;
        this.screenID = screenID;

        this.readAllData(getTagCompound(stack));
    }

    @Override
    public void writeItems(NbtCompound compound)
    {
        InventoryUtils.writeNbt(compound, this.inventory.getStacks(), true, false);
        InventoryUtils.writeNbt(compound, this.craftingInventory.getStacks(), true, true);
    }

    @Override
    public void readItems(NbtCompound compound)
    {
        this.inventory = createInventory(Reference.INVENTORY_SIZE);
        this.craftingInventory = createInventory(Reference.CRAFTING_GRID_SIZE);
        InventoryUtils.readNbt(compound, this.inventory.getStacks(), false);
        InventoryUtils.readNbt(compound, this.craftingInventory.getStacks(), true);
    }

    @Override
    public void writeTanks(NbtCompound compound)
    {
        compound.put(LEFT_TANK, getLeftTank().variant.toNbt());
        compound.put(RIGHT_TANK, getRightTank().variant.toNbt());
        compound.putLong(LEFT_TANK_AMOUNT, getLeftTank().amount);
        compound.putLong(RIGHT_TANK_AMOUNT, getRightTank().amount);
    }

    @Override
    public void readTanks(NbtCompound compound)
    {
        this.leftTank.variant = FluidVariantImpl.fromNbt(compound.getCompound(LEFT_TANK));
        this.rightTank.variant = FluidVariantImpl.fromNbt(compound.getCompound(RIGHT_TANK));
        this.leftTank.amount = compound.getLong(LEFT_TANK_AMOUNT);
        this.rightTank.amount = compound.getLong(RIGHT_TANK_AMOUNT);
    }

    @Override
    public void writeColor(NbtCompound compound) {}
    @Override
    public void readColor(NbtCompound compound) {}
    @Override
    public void writeTime(NbtCompound compound) {}
    @Override
    public void readTime(NbtCompound compound) {}

    @Override
    public void writeAllData(NbtCompound compound)
    {
        writeItems(compound);
        markTankDirty();
    }

    @Override
    public void readAllData(NbtCompound compound)
    {
        readItems(compound);
        readTanks(compound);
    }

    @Override
    public SingleVariantStorage<FluidVariant> getLeftTank()
    {
        return leftTank;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getRightTank() {
        return rightTank;
    }

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), Reference.BUCKET_IN_LEFT, this.player) || InventoryActions.transferContainerTank(this, getRightTank(), Reference.BUCKET_IN_RIGHT, this.player);
    }

    @Override
    public void markTankDirty()
    {
        this.writeTanks(this.getTagCompound(this.stack));
        this.sendPackets();
    }

    public void sendPackets()
    {
        if(screenID == Reference.TRAVELERS_BACKPACK_WEARABLE_SCREEN_ID)
        {
            ComponentUtils.sync(this.player);
            ComponentUtils.syncToTracking(player);
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
    public InventoryImproved getInventory()
    {
        return this.inventory;
    }

    @Override
    public InventoryImproved getCraftingGridInventory()
    {
        return this.craftingInventory;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        ItemStack itemstack = Inventories.splitStack(this.inventory.getStacks(), index, count);

        if(!itemstack.isEmpty())
        {
            this.markDirty();
        }
        return itemstack;
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
    public byte getScreenID()
    {
        return this.screenID;
    }

    @Override
    public World getWorld()
    {
        return this.player.world;
    }

    @Override
    public BlockPos getPosition()
    {
        return this.player.getBlockPos();
    }

    @Override
    public ItemStack getItemStack()
    {
        return this.stack;
    }

    @Override
    public NbtCompound getTagCompound(ItemStack stack)
    {
        if(stack.getNbt() == null)
        {
            NbtCompound tag = new NbtCompound();
            stack.setNbt(tag);
        }

        return stack.getNbt();
    }

    @Override
    public void markDirty()
    {
        this.writeAllData(this.getTagCompound(this.stack));
    }

    public static void openGUI(PlayerEntity player, ItemStack stack, byte screenID)
    {
        if(!player.world.isClient)
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
                    return new TranslatableText("screen.travelersbackpack.item");
                }

                @Nullable
                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
                {
                    return new TravelersBackpackItemScreenHandler(syncId, inv, new TravelersBackpackInventory(stack, player, screenID));
                }
            });
        }
    }

    public InventoryImproved createInventory(int size)
    {
        return new InventoryImproved(DefaultedList.ofSize(size, ItemStack.EMPTY))
        {
            @Override
            public void markDirty()
            {
                writeItems(getTagCompound(stack));
            }
        };
    }

    public SingleVariantStorage<FluidVariant> createFluidTank(long capacity)
    {
        return new SingleVariantStorage<FluidVariant>()
        {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant)
            {
                return capacity;
            }

            @Override
            protected void onFinalCommit()
            {
                TravelersBackpackInventory.this.markTankDirty();
            }
        };
    }
}
