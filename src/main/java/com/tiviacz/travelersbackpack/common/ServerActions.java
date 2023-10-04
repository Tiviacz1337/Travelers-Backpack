package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackBlockEntityMenu;
import com.tiviacz.travelersbackpack.inventory.menu.TravelersBackpackItemMenu;
import com.tiviacz.travelersbackpack.inventory.sorter.ContainerSorter;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.FluidUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class ServerActions
{
    public static void swapTool(Player player, double scrollDelta)
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            TravelersBackpackContainer inventory = CapabilityUtils.getBackpackInv(player);
            ItemStackHandler inv = inventory.getHandler();
            ItemStack heldItem = player.getMainHandItem();

            int toolSlots = inventory.getTier().getToolSlots();
            int firstSlot = inventory.getTier().getSlotIndex(Tiers.SlotType.TOOL_FIRST);
            int lastSlot = firstSlot + (toolSlots - 1);

            int j = 0;

            for(int i = firstSlot; i <= lastSlot; i++)
            {
                if(!inv.getStackInSlot(i).isEmpty())
                {
                    j++;
                }
            }

            ItemStack[] tools = new ItemStack[j];

            if(scrollDelta < 0)
            {
                int slot = 0;

                for(int i = firstSlot; i <= firstSlot + j - 1; i++)
                {
                    tools[slot] = inv.getStackInSlot(i).copy();
                    slot++;
                }

                ItemStack tempStack = tools[tools.length - 1];

                for(int i = tools.length - 1; i >= 0; i--)
                {
                    if(i - 1 < 0)
                    {
                        tools[0] = heldItem;
                        player.setItemInHand(InteractionHand.MAIN_HAND, tempStack);
                    }
                    else
                    {
                        tools[i] = tools[i - 1];
                    }
                }

                slot = 0;

                for(int i = firstSlot; i <= firstSlot + j - 1; i++)
                {
                    inv.setStackInSlot(i, tools[slot]);
                    slot++;
                }
            }
            else if(scrollDelta > 0)
            {
                int slot = 0;

                for(int i = firstSlot; i <= firstSlot + j - 1; i++)
                {
                    tools[slot] = inv.getStackInSlot(i).copy();
                    slot++;
                }

                ItemStack tempStack = tools[0];

                for(int i = 0; i <= tools.length - 1; i++)
                {
                    if(i + 1 > tools.length - 1)
                    {
                        tools[tools.length - 1] = heldItem;
                        player.setItemInHand(InteractionHand.MAIN_HAND, tempStack);
                    }
                    else
                    {
                        tools[i] = tools[i + 1];
                    }
                }

                slot = 0;

                for(int i = firstSlot; i <= firstSlot + j - 1; i++)
                {
                    inv.setStackInSlot(i, tools[slot]);
                    slot++;
                }
            }

            inventory.setDataChanged(ITravelersBackpackContainer.INVENTORY_DATA);
        }
    }

    public static void equipBackpack(Player player)
    {
        LazyOptional<ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        Level level = player.level;

        if(!level.isClientSide)
        {
            if(!cap.map(ITravelersBackpack::hasWearable).orElse(false))
            {
                if(player.containerMenu instanceof TravelersBackpackItemMenu) player.containerMenu.removed(player);

                ItemStack stack = player.getMainHandItem().copy();

                cap.ifPresent(inv -> inv.setWearable(stack));
                cap.ifPresent(inv -> inv.setContents(stack));
                player.getMainHandItem().shrink(1);
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.0F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }
            player.closeContainer();
        }
    }

    public static void unequipBackpack(Player player)
    {
        LazyOptional<ITravelersBackpack> cap = CapabilityUtils.getCapability(player);
        Level level = player.level;

        if(!level.isClientSide)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu) player.containerMenu.removed(player);

            ItemStack wearable = cap.map(ITravelersBackpack::getWearable).orElse(ItemStack.EMPTY).copy();

            if(!player.getInventory().add(wearable))
            {
                player.sendSystemMessage(Component.translatable(Reference.NO_SPACE));
                player.closeContainer();

                return;
            }

            if(cap.map(ITravelersBackpack::hasWearable).orElse(false))
            {
                cap.ifPresent(ITravelersBackpack::removeWearable);
                level.playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 1.05F, (1.0F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2F) * 0.7F);

                //Sync
                CapabilityUtils.synchronise(player);
                CapabilityUtils.synchroniseToOthers(player);
            }
            player.closeContainer();
        }
    }

    public static void switchAbilitySlider(Player player, boolean sliderValue)
    {
        TravelersBackpackContainer container = CapabilityUtils.getBackpackInv(player);
        container.setAbility(sliderValue);
        container.setDataChanged(ITravelersBackpackContainer.ABILITY_DATA, ITravelersBackpackContainer.TANKS_DATA);

        if(BackpackAbilities.isOnList(BackpackAbilities.ITEM_ABILITIES_REMOVAL_LIST, container.getItemStack()) && !sliderValue)
        {
            BackpackAbilities.ABILITIES.abilityRemoval(container.getItemStack(), player);
        }

        if(container.getItemStack().getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK.get() && container.getLastTime() <= 0)
        {
            BackpackAbilities.ABILITIES.chickenAbility(player, true);
        }
    }

    public static void switchAbilitySliderBlockEntity(Player player, BlockPos pos, boolean sliderValue)
    {
        if(player.level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            blockEntity.setAbility(sliderValue);
            blockEntity.setDataChanged();

            blockEntity.getLevel().updateNeighborsAt(pos, blockEntity.getBlockState().getBlock());

            if(blockEntity.getBlockState().getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK.get())
            {
                ((TravelersBackpackBlock)blockEntity.getBlockState().getBlock()).tryAbsorbWater(blockEntity.getLevel(), pos);
            }
        }
    }

    public static void sortBackpack(Player player, byte screenID, byte button, boolean shiftPressed)
    {
        if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID && player.containerMenu instanceof TravelersBackpackBlockEntityMenu menu)
        {
            if(player.level.getBlockEntity(menu.container.getPosition()) instanceof TravelersBackpackBlockEntity)
            {
                ContainerSorter.selectSort(menu.container, player, button, shiftPressed);
            }
        }

        else if(screenID == Reference.ITEM_SCREEN_ID)
        {
            if(player.containerMenu instanceof TravelersBackpackItemMenu menu)
            {
                ContainerSorter.selectSort(menu.container, player, button, shiftPressed);
            }
        }

        else if(screenID == Reference.WEARABLE_SCREEN_ID)
        {
            ContainerSorter.selectSort(CapabilityUtils.getBackpackInv(player), player, button, shiftPressed);
        }
    }

    public static void toggleSleepingBag(Player player, BlockPos pos)
    {
        Level level = player.level;

        if(level.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            if(!blockEntity.isSleepingBagDeployed())
            {
                if(blockEntity.deploySleepingBag(level, pos))
                {
                    player.closeContainer();
                }
                else
                {
                    player.sendSystemMessage(Component.translatable(Reference.DEPLOY));
                }
            }
            else
            {
                blockEntity.removeSleepingBag(level);
            }
            player.closeContainer();
        }
    }

    public static void emptyTank(double tankType, Player player, Level level, byte screenID)
    {
        ITravelersBackpackContainer container = null;

        if(screenID == Reference.WEARABLE_SCREEN_ID) container = CapabilityUtils.getBackpackInv(player);
        if(screenID == Reference.ITEM_SCREEN_ID) container = ((TravelersBackpackItemMenu)player.containerMenu).container;
        if(screenID == Reference.BLOCK_ENTITY_SCREEN_ID) container = ((TravelersBackpackBlockEntityMenu)player.containerMenu).container;

        if(container == null) return;

        FluidTank tank = tankType == 1D ? container.getLeftTank() : container.getRightTank();
        if(!level.isClientSide)
        {
            level.playSound(null, player.blockPosition(), FluidUtils.getFluidEmptySound(tank.getFluid().getFluid()), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        tank.drain(container.getTier().getTankCapacity(), IFluidHandler.FluidAction.EXECUTE);
        container.setDataChanged(ITravelersBackpackContainer.TANKS_DATA);
    }

    public static boolean setFluidEffect(Level level, Player player, FluidTank tank)
    {
        FluidStack fluidStack = tank.getFluid();
        boolean done = false;

        if(EffectFluidRegistry.hasExecutableEffects(fluidStack, level, player))
        {
            done = EffectFluidRegistry.executeEffects(fluidStack, player, level);
        }
        return done;
    }

    public static void switchHoseMode(Player player, double scrollDelta)
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

    public static void toggleHoseTank(Player player)
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