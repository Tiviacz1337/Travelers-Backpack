package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.TravelersBackpackInventory;
import com.tiviacz.travelersbackpack.inventory.screen.TravelersBackpackItemScreenHandler;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerActions
{
    public static void swapTool(PlayerEntity player, double scrollDelta)
    {
        if(ComponentUtils.isWearingBackpack(player))
        {
            TravelersBackpackInventory inventory = ComponentUtils.getBackpackInv(player);
            Inventory inv = inventory.getInventory();
            ItemStack heldItem = player.getMainHandStack();

            if(!inv.getStack(Reference.TOOL_UPPER).isEmpty() && inv.getStack(Reference.TOOL_LOWER).isEmpty() || !inv.getStack(Reference.TOOL_LOWER).isEmpty() && inv.getStack(Reference.TOOL_UPPER).isEmpty())
            {
                boolean isUpperEmpty = inv.getStack(Reference.TOOL_UPPER).isEmpty();
                player.setStackInHand(Hand.MAIN_HAND, isUpperEmpty ? inv.getStack(Reference.TOOL_LOWER) : inv.getStack(Reference.TOOL_UPPER));
                inv.setStack(isUpperEmpty ? Reference.TOOL_LOWER : Reference.TOOL_UPPER, heldItem);
            }

            if(!inv.getStack(Reference.TOOL_UPPER).isEmpty() && !inv.getStack(Reference.TOOL_LOWER).isEmpty())
            {
                if(scrollDelta < 0)
                {
                    player.setStackInHand(Hand.MAIN_HAND, inv.getStack(Reference.TOOL_UPPER));
                    inv.setStack(Reference.TOOL_UPPER, inv.getStack(Reference.TOOL_LOWER));
                    inv.setStack(Reference.TOOL_LOWER, heldItem);
                }

                else if(scrollDelta > 0)
                {
                    player.setStackInHand(Hand.MAIN_HAND, inv.getStack(Reference.TOOL_LOWER));
                    inv.setStack(Reference.TOOL_LOWER, inv.getStack(Reference.TOOL_UPPER));
                    inv.setStack(Reference.TOOL_UPPER, heldItem);
                }
            }
            inventory.markDataDirty(ITravelersBackpackInventory.INVENTORY_DATA);
        }
    }

    public static void equipBackpack(PlayerEntity player)
    {
        World world = player.world;

        if(!world.isClient)
        {
            if(!ComponentUtils.getComponent(player).hasWearable())
            {
                if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) ((ServerPlayerEntity)player).closeHandledScreen();

                ItemStack stack = player.getMainHandStack().copy();

                ComponentUtils.getComponent(player).setWearable(stack);
                ComponentUtils.getComponent(player).setContents(stack);
                player.getMainHandStack().decrement(1);
                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                ComponentUtils.sync(player);
                ComponentUtils.syncToTracking(player);
            }
            ((ServerPlayerEntity)player).closeHandledScreen();
        }
    }

    public static void unequipBackpack(PlayerEntity player)
    {
        World world = player.world;

        if(!world.isClient)
        {
            if(player.currentScreenHandler instanceof TravelersBackpackItemScreenHandler) player.currentScreenHandler.close(player);

            ItemStack wearable = ComponentUtils.getComponent(player).getWearable().copy();

            if(!player.getInventory().insertStack(wearable))
            {
                player.sendMessage(new TranslatableText(Reference.NO_SPACE), false);
                ((ServerPlayerEntity)player).closeHandledScreen();

                return;
            }

            if(ComponentUtils.getComponent(player).hasWearable())
            {
                ComponentUtils.getComponent(player).removeWearable();
                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.05F, (1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

                //Sync
                ComponentUtils.sync(player);
                ComponentUtils.syncToTracking(player);
            }
            ((ServerPlayerEntity)player).closeHandledScreen();
        }
    }

    public static void switchAbilitySlider(PlayerEntity player, boolean sliderValue)
    {
        TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        inv.setAbility(sliderValue);
        inv.markDataDirty(ITravelersBackpackInventory.ABILITY_DATA, ITravelersBackpackInventory.TANKS_DATA);

        if(inv.getItemStack().getItem() == ModItems.CHICKEN_TRAVELERS_BACKPACK && inv.getLastTime() <= 0)
        {
            BackpackAbilities.ABILITIES.chickenAbility(player, true);
        }
    }

    public static void switchAbilitySliderEntity(PlayerEntity player, BlockPos pos)
    {
        if(player.world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity blockEntity = ((TravelersBackpackBlockEntity)player.world.getBlockEntity(pos));
            blockEntity.setAbility(!blockEntity.getAbilityValue());
            blockEntity.markDirty();

            blockEntity.getWorld().updateNeighbors(pos, blockEntity.getCachedState().getBlock());

            if(blockEntity.getCachedState().getBlock() == ModBlocks.SPONGE_TRAVELERS_BACKPACK)
            {
                ((TravelersBackpackBlock)blockEntity.getCachedState().getBlock()).update(blockEntity.getWorld(), pos);
            }
        }
    }

    public static void toggleSleepingBag(PlayerEntity player, BlockPos pos)
    {
        World world = player.world;

        if(world.getBlockEntity(pos) instanceof TravelersBackpackBlockEntity)
        {
            TravelersBackpackBlockEntity te = (TravelersBackpackBlockEntity)world.getBlockEntity(pos);

            if(!te.isSleepingBagDeployed())
            {
                if(te.deploySleepingBag(world, pos))
                {
                    if(!world.isClient)
                    {
                        ((ServerPlayerEntity)player).closeHandledScreen();
                    }
                }
                else
                {
                    if(!world.isClient) player.sendMessage(new TranslatableText(Reference.DEPLOY), false);
                }
            }
            else
            {
                te.removeSleepingBag(world);
            }
            if(!world.isClient)
            {
                ((ServerPlayerEntity)player).closeHandledScreen();
            }
        }
    }

    public static void emptyTank(double tankType, PlayerEntity player, World world)
    {
        TravelersBackpackInventory inv = ComponentUtils.getBackpackInv(player);
        SingleVariantStorage<FluidVariant> tank = tankType == 1D ? inv.getLeftTank() : inv.getRightTank();
        world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        tank.variant = FluidVariant.blank();
        tank.amount = 0;
        if(!world.isClient)
        {
            ((ServerPlayerEntity)player).closeHandledScreen();
        }

        //Sync
        ComponentUtils.sync(player);
        ComponentUtils.syncToTracking(player);
        inv.markTankDirty();
    }

    public static boolean setFluidEffect(World world, PlayerEntity player, SingleVariantStorage<FluidVariant> tank)
    {
        boolean done = false;

        if(EffectFluidRegistry.hasFluidEffectAndCanExecute(tank, world, player))
        {
            done = EffectFluidRegistry.executeFluidEffectsForFluid(tank, player, world);
        }
        return done;
    }

    public static void switchHoseMode(PlayerEntity player, double scrollDelta)
    {
        ItemStack hose = player.getMainHandStack();

        if(hose.getItem() instanceof HoseItem)
        {
            if(hose.getNbt() != null)
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
                hose.getNbt().putInt("Mode", mode);
            }
        }
    }

    public static void toggleHoseTank(PlayerEntity player)
    {
        ItemStack hose = player.getMainHandStack();

        if(hose.getItem() instanceof HoseItem)
        {
            if(hose.getNbt() != null)
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

                hose.getNbt().putInt("Tank", tank);
            }
        }
    }
}