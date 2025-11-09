package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.tiviacz.travelersbackpack.components.BackpackContainerContents;
import com.tiviacz.travelersbackpack.components.Fluids;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.transfer.BackpackResourceHandler;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.InventoryActions;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TanksUpgrade extends UpgradeBase<TanksUpgrade> {
    private final BackpackResourceHandler fluidSlotsHandler = createTemporaryHandler();
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

    public BackpackResourceHandler getFluidSlotsHandler() {
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
        if(getUpgradeManager().getWrapper().getRegistriesAccess() != null) {
            Tag leftFluid = FluidStack.CODEC.encodeStart(getUpgradeManager().getWrapper().getRegistriesAccess().createSerializationContext(NbtOps.INSTANCE), leftTank.getFluid()).result().orElseGet(CompoundTag::new);
            Tag rightFluid = FluidStack.CODEC.encodeStart(getUpgradeManager().getWrapper().getRegistriesAccess().createSerializationContext(NbtOps.INSTANCE), rightTank.getFluid()).result().orElseGet(CompoundTag::new);
            tag.put("LeftTank", leftFluid);
            tag.put("RightTank", rightFluid);
        }
        tag.putInt("Capacity", leftTank.getCapacity());
        return tag;
    }

    @Override
    public void remove() {
        getUpgradeManager().getWrapper().removeRenderInfo();
    }

    @Override
    public List<ResourceHandlerSlot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<ResourceHandlerSlot> slots = new ArrayList<>();
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 0, x + 7, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 1, x + 7, y + 49));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 2, x + 28, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 3, x + 28, y + 49));
        return slots;
    }

    public BackpackResourceHandler createTemporaryHandler() {
        return new BackpackResourceHandler(4) {
            @Override
            protected void onContentsChanged(int slot, ItemStack previousStack) {
                if(ItemStack.isSameItemSameComponents(previousStack, getStackInSlot(slot))) {
                    if(slot == 0) {
                        InventoryActions.transferContainerTank(TanksUpgrade.this, getLeftTank(), 0);
                    }
                    if(slot == 2) {
                        InventoryActions.transferContainerTank(TanksUpgrade.this, getRightTank(), 2);
                    }
                }
            }

            @Override
            public boolean isValid(int slot, ItemResource stack) {
                Optional<IFluidHandlerItem> container = FluidUtil.getFluidHandler(stack.toStack());
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