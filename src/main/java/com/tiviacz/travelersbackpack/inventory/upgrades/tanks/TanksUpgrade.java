package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;
import com.tiviacz.travelersbackpack.client.screens.widgets.WidgetBase;
import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TanksUpgrade extends UpgradeBase<TanksUpgrade> {
    private final ItemStackHandler fluidSlotsHandler = createTemporaryHandler();
    protected final FluidTank leftTank = createFluidHandler(1000);
    protected final FluidTank rightTank = createFluidHandler(1000);
    public final Point leftTankPos;
    public final Point rightTankPos;

    public TanksUpgrade(UpgradeManager manager, int dataHolderSlot, Fluids fluids) {
        super(manager, dataHolderSlot, new Point(51, 72));
        this.setTanksCapacity();
        this.setFluids(fluids);

        this.leftTankPos = new Point(7, 15);
        this.rightTankPos = new Point(195 + (manager.getWrapper().isExtended() ? 36 : 0), 15);

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
        BackpackContainerContents contents = backpack.get(ModDataComponents.UPGRADES);
        if(contents == null) return;
        if(slot >= contents.getItems().size()) return;
        ItemStack stack = contents.getItems().get(slot);
        setFluids(stack.getOrDefault(ModDataComponents.FLUIDS, Fluids.empty()));
    }

    private FluidTank createFluidHandler(int capacity) {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                updateDataHolderUnchecked(ModDataComponents.FLUIDS.get(), new Fluids(leftTank.getFluid(), rightTank.getFluid()));

                //Update Render data
                getUpgradeManager().getWrapper().setRenderInfo(writeToRenderData());

                //Update backpack attachment data on clients
                getUpgradeManager().getWrapper().sendDataToClients(ModDataComponents.RENDER_INFO.get(), ModDataComponents.UPGRADES.get());
            }
        };
    }

    public CompoundTag writeToRenderData() {
        CompoundTag tag = new CompoundTag();
        tag.put("LeftTank", leftTank.getFluid().saveOptional(getUpgradeManager().getWrapper().getRegistriesAccess()));
        tag.put("RightTank", rightTank.getFluid().saveOptional(getUpgradeManager().getWrapper().getRegistriesAccess()));
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
            /*@Override
            protected void onContentsChanged(int slot) {
                if(slot == 0) {
                    InventoryActions.transferContainerTank(TanksUpgrade.this, getLeftTank(), 0);
                }
                if(slot == 2) {
                    InventoryActions.transferContainerTank(TanksUpgrade.this, getRightTank(), 2);
                }
            }*/

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                Optional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack);
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
