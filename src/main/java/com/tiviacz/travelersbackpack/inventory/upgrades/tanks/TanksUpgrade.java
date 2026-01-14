package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataHelper;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TanksUpgrade extends UpgradeBase<TanksUpgrade> {
    private final ItemStackHandler fluidSlotsHandler = createTemporaryHandler();
    protected final FluidTank leftTank = createFluidHandler(1000);
    protected final FluidTank rightTank = createFluidHandler(1000);

    public TanksUpgrade(UpgradeManager manager, int dataHolderSlot, Fluids fluids) {
        super(manager, dataHolderSlot, new Point(51, 72));
        this.setTanksCapacity();
        this.setFluids(fluids);

        //Update Render data
        getUpgradeManager().getWrapper().setRenderInfo(writeToRenderData());
    }

    public FluidTank getLeftTank() {
        return leftTank;
    }

    public FluidTank getRightTank() {
        return rightTank;
    }

    public ItemStackHandler getFluidSlotsHandler() {
        return this.fluidSlotsHandler;
    }

    public void setTanksCapacity() {
        this.leftTank.setCapacity(getUpgradeManager().getWrapper().getBackpackTankCapacity());
        this.rightTank.setCapacity(getUpgradeManager().getWrapper().getBackpackTankCapacity());
    }

    public void setFluids(Fluids tanks) {
        this.leftTank.setFluid(tanks.leftFluidStack());
        this.rightTank.setFluid(tanks.rightFluidStack());
    }

    public void syncClients(ItemStack backpack) {
        int slot = getDataHolderSlot();
        NonNullList<ItemStack> contents = NbtHelper.get(backpack, ModDataHelper.UPGRADES);
        if(contents == null) return;
        if(slot >= contents.size()) return;
        ItemStack stack = contents.get(slot);
        setFluids(NbtHelper.getOrDefault(stack, ModDataHelper.FLUIDS, Fluids.empty()));
    }

    private FluidTank createFluidHandler(int capacity) {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                updateDataHolderUnchecked(ModDataHelper.FLUIDS, new Fluids(leftTank.getFluid(), rightTank.getFluid()));

                //Update Render data
                getUpgradeManager().getWrapper().setRenderInfo(writeToRenderData());

                //Update backpack attachment data on clients
                getUpgradeManager().getWrapper().sendDataToClients(ModDataHelper.UPGRADES);
            }
        };
    }

    public CompoundTag writeToRenderData() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", leftTank.getFluid().writeToNBT(new CompoundTag()));
        tag.put("RightTank", rightTank.getFluid().writeToNBT(new CompoundTag()));
        tag.putInt("Capacity", leftTank.getCapacity());
        return tag;
    }

    @Override
    public void remove() {
        getUpgradeManager().getWrapper().removeRenderInfo();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public WidgetBase<BackpackScreen> createWidget(BackpackScreen screen, int x, int y) {
        return new TankWidget(screen, this, new Point(screen.getGuiLeft() + x, screen.getGuiTop() + y));
    }

    @Override
    public List<Pair<Integer, Integer>> getUpgradeSlotsPosition(int x, int y) {
        List<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(Pair.of(x + 7, y + 23));
        positions.add(Pair.of(x + 7, y + 49));
        positions.add(Pair.of(x + 28, y + 23));
        positions.add(Pair.of(x + 28, y + 49));
        return positions;
    }

    @Override
    public List<SlotItemHandler> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<SlotItemHandler> slots = new ArrayList<>();
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 0, x + 7, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 1, x + 7, y + 49));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 2, x + 28, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 3, x + 28, y + 49));
        return slots;
    }

    public ItemStackHandler createTemporaryHandler() {
        return new ItemStackHandler(4) {
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                LazyOptional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);
                if(slot == 1 || slot == 3) {
                    return false;
                }
                if(stack.getItem() instanceof PotionItem || stack.getItem() == Items.GLASS_BOTTLE) {
                    return true;
                }
                return container.isPresent();
            }
        };
    }
}
