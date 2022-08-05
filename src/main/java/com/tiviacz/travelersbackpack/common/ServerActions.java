package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackItemContainer;
import com.tiviacz.travelersbackpack.inventory.container.TravelersBackpackTileContainer;
import com.tiviacz.travelersbackpack.inventory.sorter.InventorySorter;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class ServerActions
{
    public static void swapTool(PlayerEntity player, double scrollDelta)
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            TravelersBackpackInventory inventory = CapabilityUtils.getBackpackInv(player);
            ItemStackHandler inv = inventory.getInventory();
            ItemStack heldItem = player.getMainHandItem();

            if(!inv.getStackInSlot(Reference.TOOL_UPPER).isEmpty() && inv.getStackInSlot(Reference.TOOL_LOWER).isEmpty() || !inv.getStackInSlot(Reference.TOOL_LOWER).isEmpty() && inv.getStackInSlot(Reference.TOOL_UPPER).isEmpty())
            {
                boolean isUpperEmpty = inv.getStackInSlot(Reference.TOOL_UPPER).isEmpty();
                player.setItemInHand(Hand.MAIN_HAND, isUpperEmpty ? inv.getStackInSlot(Reference.TOOL_LOWER) : inv.getStackInSlot(Reference.TOOL_UPPER));
                inv.setStackInSlot(isUpperEmpty ? Reference.TOOL_LOWER : Reference.TOOL_UPPER, heldItem);
            }

            if(!inv.getStackInSlot(Reference.TOOL_UPPER).isEmpty() && !inv.getStackInSlot(Reference.TOOL_LOWER).isEmpty())
            {
                if(scrollDelta < 0)
                {
                    player.setItemInHand(Hand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_UPPER));
                    inv.setStackInSlot(Reference.TOOL_UPPER, inv.getStackInSlot(Reference.TOOL_LOWER));
                    inv.setStackInSlot(Reference.TOOL_LOWER, heldItem);
                }

                else if(scrollDelta > 0)
                {
                    player.setItemInHand(Hand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_LOWER));
                    inv.setStackInSlot(Reference.TOOL_LOWER, inv.getStackInSlot(Reference.TOOL_UPPER));
                    inv.setStackInSlot(Reference.TOOL_UPPER, heldItem);
                }
            }
            inventory.setDataChanged(ITravelersBackpackInventory.INVENTORY_DATA);
        }
    }

    public static void equipBackpack(PlayerEntity player)
    {
        LazyOptional<ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        World world = player.level;

        if(!world.isClientSide)
        {
            if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
            {
                if(player.containerMenu instanceof TravelersBackpackItemContainer) player.containerMenu.removed(player);

                ItemStack stack = player.getMainHandItem().copy();

                cap.ifPresent(inv -> inv.setWearable(stack));
                cap.ifPresent(inv -> inv.setContents(stack));

                player.getMainHandItem().shrink(1);
                world.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }
            player.closeContainer();
        }
    }

    public static void unequipBackpack(PlayerEntity player)
    {
        LazyOptional<ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        World world = player.level;

        if(!world.isClientSide)
        {
            if(player.containerMenu instanceof TravelersBackpackItemContainer) player.containerMenu.removed(player);

            ItemStack wearable = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY).copy();

            if(!player.inventory.add(wearable))
            {
                player.sendMessage(new TranslationTextComponent(Reference.NO_SPACE), player.getUUID());
                player.closeContainer();

                return;
            }

            if(cap.map(ITravelersBackpack::hasWearable).orElse(false))
            {
                cap.ifPresent(ITravelersBackpack::removeWearable);
                world.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }
            player.closeContainer();
        }
    }

    public static void switchAbilitySlider(PlayerEntity player, boolean sliderValue)
    {
        TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
        inv.setAbility(sliderValue);
        inv.setDataChanged(ITravelersBackpackInventory.ABILITY_DATA, ITravelersBackpackInventory.TANKS_DATA);

        if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, inv.getItemStack()) && !sliderValue)
        {
            BackpackAbilities.ABILITIES.abilityRemoval(inv.getItemStack(), player, null);
        }

        if(inv.getItemStack().getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK.get() && inv.getLastTime() <= 0)
        {
            BackpackAbilities.ABILITIES.chickenAbility(player, true);
        }
    }

    public static void switchAbilitySliderTileEntity(PlayerEntity player, BlockPos pos, boolean sliderValue)
    {
        if(player.level.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity blockEntity = ((TravelersBackpackTileEntity)player.level.getBlockEntity(pos));
            blockEntity.setAbility(sliderValue);
            blockEntity.setChanged();

            blockEntity.getLevel().updateNeighborsAt(pos, blockEntity.getBlockState().getBlock());

            if(blockEntity.getBlockState().getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
            {
                ((TravelersBackpackBlock)blockEntity.getBlockState().getBlock()).tryAbsorbWater(blockEntity.getLevel(), pos);
            }
        }
    }

    public static void sortBackpack(PlayerEntity player, byte screenID, byte button, boolean shiftPressed)
    {
        if(screenID == Reference.TILE_SCREEN_ID && player.containerMenu instanceof TravelersBackpackTileContainer)
        {
            if(player.level.getBlockEntity(((TravelersBackpackTileContainer)player.containerMenu).inventory.getPosition()) instanceof TravelersBackpackTileEntity)
            {
                InventorySorter.selectSort(((TravelersBackpackTileContainer)player.containerMenu).inventory, player, button, shiftPressed);
            }
        }

        else if(screenID == Reference.ITEM_SCREEN_ID)
        {
            if(player.containerMenu instanceof TravelersBackpackItemContainer)
            {
                InventorySorter.selectSort(((TravelersBackpackItemContainer)player.containerMenu).inventory, player, button, shiftPressed);
            }
        }

        else if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            InventorySorter.selectSort(CapabilityUtils.getBackpackInv(player), player, button, shiftPressed);
        }
    }

    public static void toggleSleepingBag(PlayerEntity player, BlockPos pos)
    {
        World world = player.level;

        if(world.getBlockEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)world.getBlockEntity(pos);

            if(!te.isSleepingBagDeployed())
            {
                if(te.deploySleepingBag(world, pos))
                {
                    player.closeContainer();
                }
                else
                {
                    player.sendMessage(new TranslationTextComponent(Reference.DEPLOY), player.getUUID());
                }
            }
            else
            {
                te.removeSleepingBag(world);
            }
            player.closeContainer();
        }
    }

    public static void emptyTank(double tankType, PlayerEntity player, World world, byte screenID)
    {
        ITravelersBackpackInventory inv = null;

        if(screenID == Reference.WEARABLE_SCREEN_ID) inv = CapabilityUtils.getBackpackInv(player);
        if(screenID == Reference.ITEM_SCREEN_ID) inv = ((TravelersBackpackItemContainer)player.containerMenu).inventory;
        if(screenID == Reference.TILE_SCREEN_ID) inv = ((TravelersBackpackTileContainer)player.containerMenu).inventory;

        if(inv == null) return;

        FluidTank tank = tankType == 1D ? inv.getLeftTank() : inv.getRightTank();
        if(!world.isClientSide)
        {
            world.playSound(null, player.blockPosition(), FluidUtils.getFluidEmptySound(tank.getFluid().getFluid()), SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        tank.drain(TravelersBackpackConfig.tanksCapacity, IFluidHandler.FluidAction.EXECUTE);
        inv.setDataChanged(ITravelersBackpackInventory.TANKS_DATA);
    }

    public static boolean setFluidEffect(World world, PlayerEntity player, FluidTank tank)
    {
        FluidStack fluidStack = tank.getFluid();
        boolean done = false;

        if(EffectFluidRegistry.hasFluidEffectAndCanExecute(fluidStack, world, player))
        {
            done = EffectFluidRegistry.executeFluidEffectsForFluid(fluidStack, player, world);
        }
        return done;
    }

    public static void switchHoseMode(PlayerEntity player, double scrollDelta)
    {
        ItemStack hose = player.getMainHandItem();

        if(hose.getItem() instanceof HoseItem)
        {
            if(hose.getTag() != null)
            {
                int mode = HoseItem.getHoseMode(hose);

                if(scrollDelta > 0)
                {
                    mode = mode + 1;

                    if(mode == 4)
                    {
                        mode = 1;
                    }
                }

                else if(scrollDelta < 0)
                {
                    mode = mode - 1;

                    if(mode == 0)
                    {
                        mode = 3;
                    }
                }
                hose.getTag().putInt("Mode", mode);
            }
        }
    }

    public static void toggleHoseTank(PlayerEntity player)
    {
        ItemStack hose = player.getMainHandItem();

        if(hose.getItem() instanceof HoseItem)
        {
            if(hose.getTag() != null)
            {
                int tank = HoseItem.getHoseTank(hose);

                if(tank == 1)
                {
                    tank = 2;
                }
                else
                {
                    tank = 1;
                }

                hose.getTag().putInt("Tank", tank);
            }
        }
    }
}