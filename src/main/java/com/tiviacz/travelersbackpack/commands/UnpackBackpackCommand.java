package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnpackBackpackCommand
{
    public UnpackBackpackCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("unpack")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(source -> unpackTargetBlockEntity(source.getSource(), BlockPosArgument.getOrLoadBlockPos(source, "pos"))))
                .then(Commands.argument("target", EntityArgument.players())
                        .executes(source -> unpackTargetInventory(source.getSource(), EntityArgument.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public int unpackTargetBlockEntity(CommandSource source, BlockPos blockPos) throws CommandSyntaxException
    {
        if(source.getLevel().getBlockEntity(blockPos) instanceof TravelersBackpackTileEntity)
        {
            ITravelersBackpackInventory inv = (TravelersBackpackTileEntity)source.getLevel().getBlockEntity(blockPos);
            NonNullList<ItemStack> stacks = NonNullList.create();

            for(int i = 0; i < inv.getInventory().getSlots(); i++)
            {
                ItemStack stack = inv.getInventory().getStackInSlot(i);

                if(!stack.isEmpty())
                {
                    stacks.add(stack);

                    inv.getInventory().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            for(int i = 0; i < inv.getCraftingGridInventory().getSlots(); i++)
            {
                ItemStack stack = inv.getCraftingGridInventory().getStackInSlot(i);

                if(!stack.isEmpty())
                {
                    stacks.add(stack);

                    inv.getCraftingGridInventory().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            if(stacks.size() > 0)
            {
                if(!source.getLevel().isClientSide)
                {
                    InventoryHelper.dropContents(source.getLevel(), blockPos, stacks);
                }

                source.sendSuccess(new StringTextComponent("Dropping contents of backpack placed at " + blockPos.toShortString()), true);
                return 1;
            }
            else
            {
                source.sendFailure(new StringTextComponent("There's no contents in backpack at coordinates " + blockPos.toShortString()));
                return -1;
            }
        }
        else
        {
            source.sendFailure(new StringTextComponent("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public int unpackTargetInventory(CommandSource source, ServerPlayerEntity serverPlayer) throws CommandSyntaxException
    {
        boolean hasBackpack = CapabilityUtils.isWearingBackpack(serverPlayer);

        if(hasBackpack)
        {
            AtomicBoolean flag = new AtomicBoolean(false);

            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap ->
            {
                NonNullList<ItemStack> stacks = NonNullList.create();

                for(int i = 0; i < cap.getInventory().getInventory().getSlots(); i++)
                {
                    ItemStack stack = cap.getInventory().getInventory().getStackInSlot(i);

                    if(!stack.isEmpty())
                    {
                        stacks.add(stack);

                        cap.getInventory().getInventory().setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                for(int i = 0; i < cap.getInventory().getCraftingGridInventory().getSlots(); i++)
                {
                    ItemStack stack = cap.getInventory().getCraftingGridInventory().getStackInSlot(i);

                    if(!stack.isEmpty())
                    {
                        stacks.add(stack);

                        cap.getInventory().getCraftingGridInventory().setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                if(stacks.size() > 0)
                {
                    if(!source.getLevel().isClientSide)
                    {
                        InventoryHelper.dropContents(source.getLevel(), serverPlayer.blockPosition(), stacks);
                        flag.set(true);
                    }
                }
            });

            if(flag.get())
            {
                source.sendSuccess(new StringTextComponent("Dropping contents of " + serverPlayer.getDisplayName().getString() + " backpack at " + serverPlayer.blockPosition().toShortString()), true);
                return 1;
            }
            else
            {
                source.sendFailure(new StringTextComponent("There's no contents in " + serverPlayer.getDisplayName().getString() + " backpack"));
                return -1;
            }
        }
        else
        {
            source.sendFailure(new StringTextComponent("Player " + serverPlayer.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}