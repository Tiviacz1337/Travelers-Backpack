package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnpackBackpackCommand
{
    public UnpackBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("unpack")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(source -> unpackTargetBlockEntity(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"))))
                .then(Commands.argument("target", EntityArgument.players())
                        .executes(source -> unpackTargetInventory(source.getSource(), EntityArgument.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public int unpackTargetBlockEntity(CommandSourceStack source, BlockPos blockPos) throws CommandSyntaxException
    {
        if(source.getLevel().getBlockEntity(blockPos) instanceof TravelersBackpackBlockEntity)
        {
            ITravelersBackpackContainer inv = (TravelersBackpackBlockEntity)source.getLevel().getBlockEntity(blockPos);
            NonNullList<ItemStack> stacks = NonNullList.create();

            for(int i = 0; i < inv.getCombinedHandler().getSlots(); i++)
            {
                ItemStack stackInSlot = inv.getCombinedHandler().getStackInSlot(i);

                if(!stackInSlot.isEmpty())
                {
                    stacks.add(stackInSlot);
                    inv.getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
                }
            }

            if(!stacks.isEmpty())
            {
                if(!source.getLevel().isClientSide)
                {
                    Containers.dropContents(source.getLevel(), blockPos, stacks);
                }

                source.sendSuccess(new TextComponent("Dropping contents of backpack placed at " + blockPos.toShortString()), true);
                return 1;
            }
            else
            {
                source.sendFailure(new TextComponent("There's no contents in backpack at coordinates " + blockPos.toShortString()));
                return -1;
            }
        }
        else
        {
            source.sendFailure(new TextComponent("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public int unpackTargetInventory(CommandSourceStack source, ServerPlayer serverPlayer) throws CommandSyntaxException
    {
        boolean hasBackpack = CapabilityUtils.isWearingBackpack(serverPlayer);

        if(hasBackpack)
        {
            AtomicBoolean flag = new AtomicBoolean(false);

            CapabilityUtils.getCapability(serverPlayer).ifPresent(cap ->
            {
                NonNullList<ItemStack> stacks = NonNullList.create();

                for(int i = 0; i < cap.getContainer().getCombinedHandler().getSlots(); i++)
                {
                    ItemStack stackInSlot = cap.getContainer().getCombinedHandler().getStackInSlot(i);

                    if(!stackInSlot.isEmpty())
                    {
                        stacks.add(stackInSlot);
                        cap.getContainer().getCombinedHandler().setStackInSlot(i, ItemStack.EMPTY);
                    }
                }

                if(!stacks.isEmpty())
                {
                    if(!source.getLevel().isClientSide)
                    {
                        Containers.dropContents(source.getLevel(), serverPlayer.blockPosition(), stacks);
                        flag.set(true);
                    }
                }
            });

            if(flag.get())
            {
                source.sendSuccess(new TextComponent("Dropping contents of " + serverPlayer.getDisplayName().getString() + " backpack at " + serverPlayer.blockPosition().toShortString()), true);
                return 1;
            }
            else
            {
                source.sendFailure(new TextComponent("There's no contents in " + serverPlayer.getDisplayName().getString() + " backpack"));
                return -1;
            }
        }
        else
        {
            source.sendFailure(new TextComponent("Player " + serverPlayer.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}