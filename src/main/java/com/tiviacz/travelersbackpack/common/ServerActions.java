package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
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
    public static void cycleTool(PlayerEntity player, double scrollDelta)
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            TravelersBackpackInventory inventory = CapabilityUtils.getBackpackInv(player);
            ItemStackHandler inv = inventory.getInventory();
            ItemStack heldItem = player.getHeldItemMainhand();

            if(scrollDelta < 0)
            {
                player.setHeldItem(Hand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_UPPER));
                inv.setStackInSlot(Reference.TOOL_UPPER, inv.getStackInSlot(Reference.TOOL_LOWER));
                inv.setStackInSlot(Reference.TOOL_LOWER, heldItem);
            }

            else if(scrollDelta > 0)
            {
                player.setHeldItem(Hand.MAIN_HAND, inv.getStackInSlot(Reference.TOOL_LOWER));
                inv.setStackInSlot(Reference.TOOL_LOWER, inv.getStackInSlot(Reference.TOOL_UPPER));
                inv.setStackInSlot(Reference.TOOL_UPPER, heldItem);
            }
            inventory.markDirty();
        }
    }

    public static void equipBackpack(PlayerEntity player)
    {
        LazyOptional<com.tiviacz.travelersbackpack.capability.ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        World world = player.world;

        if(!world.isRemote)
        {
            if(!cap.map(com.tiviacz.travelersbackpack.capability.ITravelersBackpack::hasWearable).orElse(false))
            {
                ItemStack stack = player.getHeldItemMainhand().copy();
                cap.ifPresent(inv -> inv.setWearable(stack));
                player.getHeldItemMainhand().shrink(1);
                world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }
            player.closeScreen();
        }
    }

    public static void unequipBackpack(PlayerEntity player)
    {
        LazyOptional<com.tiviacz.travelersbackpack.capability.ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        World world = player.world;

      //  CapabilityUtils.onUnequipped(world, player, cap.getWearable());

        if(!world.isRemote)
        {
            ItemStack wearable = cap.map(com.tiviacz.travelersbackpack.capability.ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY).copy();

            if(!player.inventory.addItemStackToInventory(wearable))
            {
                player.sendMessage(new TranslationTextComponent(Reference.NO_SPACE), player.getUniqueID());
                player.closeScreen();

                return;
            }

            if(cap.map(com.tiviacz.travelersbackpack.capability.ITravelersBackpack::hasWearable).orElse(false))
            {
                cap.ifPresent(com.tiviacz.travelersbackpack.capability.ITravelersBackpack::removeWearable);
                world.playSound(null, player.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }

            player.closeScreen();
        }
    }

    public static void toggleSleepingBag(PlayerEntity player, BlockPos pos)
    {
        World world = player.world;

        if(world.getTileEntity(pos) instanceof TravelersBackpackTileEntity)
        {
            TravelersBackpackTileEntity te = (TravelersBackpackTileEntity)world.getTileEntity(pos);

            if(!te.isSleepingBagDeployed())
            {
                if(te.deploySleepingBag(world, pos))
                {
                    player.closeScreen();
                }
                else
                {
                    player.sendMessage(new TranslationTextComponent(Reference.DEPLOY), player.getUniqueID());
                }
            }
            else
            {
                te.removeSleepingBag(world);
            }
            player.closeScreen();
        }
    }

    public static void emptyTank(double tankType, PlayerEntity player, World world)
    {
        TravelersBackpackInventory inv = CapabilityUtils.getBackpackInv(player);
        FluidTank tank = tankType == 1D ? inv.getLeftTank() : inv.getRightTank();
        world.playSound(null, player.getPosition(), FluidUtils.getFluidEmptySound(tank.getFluid().getFluid()), SoundCategory.BLOCKS, 1.0F, 1.0F);
        tank.drain(TravelersBackpackConfig.COMMON.tanksCapacity.get(), IFluidHandler.FluidAction.EXECUTE);
        player.closeScreen();

        //Sync
        CapabilityUtils.synchronise(player);
        CapabilityUtils.synchroniseToOthers(player);
        inv.markTankDirty();
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
        ItemStack hose = player.getHeldItemMainhand();

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
        ItemStack hose = player.getHeldItemMainhand();

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
