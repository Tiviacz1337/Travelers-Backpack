package com.tiviacz.travelersbackpack.inventory.upgrades.tanks;

import com.mojang.datafixers.util.Pair;
import com.tiviacz.travelersbackpack.component.Fluids;
import com.tiviacz.travelersbackpack.component.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import com.tiviacz.travelersbackpack.inventory.UpgradeManager;
import com.tiviacz.travelersbackpack.inventory.menu.BackpackBaseMenu;
import com.tiviacz.travelersbackpack.inventory.menu.slot.FluidSlotItemHandler;
import com.tiviacz.travelersbackpack.inventory.upgrades.Point;
import com.tiviacz.travelersbackpack.inventory.upgrades.UpgradeBase;
import com.tiviacz.travelersbackpack.util.ContainerContentsHelper;
import com.tiviacz.travelersbackpack.util.StacksHandlerUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TanksUpgrade extends UpgradeBase<TanksUpgrade> {
    private final ItemStacksResourceHandler fluidSlotsHandler = createTemporaryHandler();
    protected final FluidStacksResourceHandler leftTank;
    protected final FluidStacksResourceHandler rightTank;

    public TanksUpgrade(UpgradeManager manager, int dataHolderSlot, Fluids fluids) {
        super(manager, dataHolderSlot, new Point(51, 72));
        this.leftTank = createFluidHandler(fluids.leftFluidStack(), getUpgradeManager().getWrapper().getBackpackTankCapacity());
        this.rightTank = createFluidHandler(fluids.rightFluidStack(), getUpgradeManager().getWrapper().getBackpackTankCapacity());

        //Update Render data
        getUpgradeManager().getWrapper().updateRenderInfo(this::writeToRenderData);
    }

    public FluidStacksResourceHandler getLeftTank() {
        return leftTank;
    }

    public FluidStacksResourceHandler getRightTank() {
        return rightTank;
    }

    public ItemStacksResourceHandler getFluidSlotsHandler() {
        return this.fluidSlotsHandler;
    }

    public void setFluids(Fluids tanks) {
        StacksHandlerUtils.setFluid(this.leftTank, tanks.leftFluidStack());
        StacksHandlerUtils.setFluid(this.rightTank, tanks.rightFluidStack());
    }

    public void syncClients(ItemStack backpack) {
        int slot = getDataHolderSlot();
        ItemContainerContents contents = backpack.get(ModDataComponents.UPGRADES);
        int upgradesSize = backpack.get(ModDataComponents.UPGRADE_SLOTS);
        if(contents == null) return;
        NonNullList<ItemStack> stacks = ContainerContentsHelper.getItems(contents, upgradesSize);
        if(slot >= stacks.size()) return;
        ItemStack stack = stacks.get(slot);
        setFluids(stack.getOrDefault(ModDataComponents.FLUIDS, Fluids.empty()));
    }

    private FluidStacksResourceHandler createFluidHandler(FluidStack fluidStack, int capacity) {
        return new FluidStacksResourceHandler(NonNullList.withSize(1, fluidStack), capacity) {
            @Override
            protected void onContentsChanged(int slot, FluidStack previousStack) {
                updateDataHolderUnchecked(ModDataComponents.FLUIDS.get(), new Fluids(StacksHandlerUtils.getFluid(leftTank), StacksHandlerUtils.getFluid(rightTank)));

                //Update Render data
                getUpgradeManager().getWrapper().updateRenderInfo(TanksUpgrade.this::writeToRenderData);

                //Update backpack attachment data on clients
                getUpgradeManager().getWrapper().sendDataToClients(ModDataComponents.RENDER_INFO.get(), ModDataComponents.UPGRADES.get());
            }
        };
    }

    public void writeToRenderData(CompoundTag tag) {
        Tag leftFluid = FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, StacksHandlerUtils.getFluid(leftTank)).result().orElseGet(CompoundTag::new);
        Tag rightFluid = FluidStack.CODEC.encodeStart(NbtOps.INSTANCE, StacksHandlerUtils.getFluid(rightTank)).result().orElseGet(CompoundTag::new);
        tag.put(RenderInfo.LEFT_TANK, leftFluid);
        tag.put(RenderInfo.RIGHT_TANK, rightFluid);
        tag.putInt(RenderInfo.CAPACITY, StacksHandlerUtils.getCapacity(leftTank));
    }

    @Override
    public void remove() {
        getUpgradeManager().getWrapper().updateRenderInfo(compoundTag -> {
            compoundTag.remove(RenderInfo.LEFT_TANK);
            compoundTag.remove(RenderInfo.RIGHT_TANK);
            compoundTag.remove(RenderInfo.CAPACITY);
        });
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
    public List<ResourceHandlerSlot> getUpgradeSlots(BackpackBaseMenu menu, BackpackWrapper wrapper, int x, int y) {
        List<ResourceHandlerSlot> slots = new ArrayList<>();
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 0, x + 7, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 1, x + 7, y + 49));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 2, x + 28, y + 23));
        slots.add(new FluidSlotItemHandler(menu.player, this, wrapper, getFluidSlotsHandler(), 3, x + 28, y + 49));
        return slots;
    }

    @Override
    public void onUpgradeRemoved(ItemStack removedStack, @Nullable Player player) {
        BackpackBaseMenu.clearSlotsAndPlaySound(player, fluidSlotsHandler, StacksHandlerUtils.getSlots(fluidSlotsHandler), false);
    }

    public ItemStacksResourceHandler createTemporaryHandler() {
        return new ItemStacksResourceHandler(4) {
            @Override
            public boolean isValid(int slot, ItemResource stack) {
                Optional<ResourceHandler<FluidResource>> handler = Optional.ofNullable(ItemAccess.forStack(stack.toStack()).getCapability(Capabilities.Fluid.ITEM));
                if(slot == 1 || slot == 3) {
                    return false;
                }
                if(stack.getItem() instanceof PotionItem || stack.getItem() == Items.GLASS_BOTTLE) {
                    return true;
                }
                return handler.isPresent();
            }
        };
    }
}