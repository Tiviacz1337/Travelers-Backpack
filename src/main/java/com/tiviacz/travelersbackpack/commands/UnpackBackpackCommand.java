package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackInventory;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnpackBackpackCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        LiteralArgumentBuilder<ServerCommandSource> tbCommand = CommandManager.literal("tb").requires(player -> player.hasPermissionLevel(2));

        tbCommand.then(CommandManager.literal("unpack")
                .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(source -> unpackTargetBlockEntity(source.getSource(), BlockPosArgumentType.getBlockPos(source, "pos"))))
                .then(CommandManager.argument("target", EntityArgumentType.players())
                        .executes(source -> unpackTargetInventory(source.getSource(), EntityArgumentType.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public static int unpackTargetBlockEntity(ServerCommandSource source, BlockPos blockPos) throws CommandSyntaxException
    {
        if(source.getWorld().getBlockEntity(blockPos) instanceof TravelersBackpackBlockEntity blockEntity)
        {
            DefaultedList<ItemStack> stacks = DefaultedList.of();

            for(int i = 0; i < blockEntity.getCombinedInventory().size(); i++)
            {
                ItemStack stackInSlot = blockEntity.getCombinedInventory().getStack(i);

                if(!stackInSlot.isEmpty())
                {
                    stacks.add(stackInSlot);
                    blockEntity.getCombinedInventory().setStack(i, ItemStack.EMPTY);
                }
            }

            if(!stacks.isEmpty())
            {
                if(!source.getWorld().isClient)
                {
                    ItemScatterer.spawn(source.getWorld(), blockPos, stacks);
                }

                source.sendFeedback(() -> Text.literal("Dropping contents of backpack placed at " + blockPos.toShortString()), true);
                return 1;
            }
            else
            {
                source.sendError(Text.literal("There's no contents in backpack at coordinates " + blockPos.toShortString()));
                return -1;
            }
        }
        else
        {
            source.sendError(Text.literal("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public static int unpackTargetInventory(ServerCommandSource source, ServerPlayerEntity serverPlayer) throws CommandSyntaxException
    {
        boolean hasBackpack = ComponentUtils.isWearingBackpack(serverPlayer);

        if(hasBackpack)
        {
            AtomicBoolean flag = new AtomicBoolean(false);

            ITravelersBackpackInventory inventory = ComponentUtils.getComponent(serverPlayer).getInventory();

            DefaultedList<ItemStack> stacks = DefaultedList.of();

            for(int i = 0; i < inventory.getCombinedInventory().size(); i++)
            {
                ItemStack stackInSlot = inventory.getCombinedInventory().getStack(i);

                if(!stackInSlot.isEmpty())
                {
                    stacks.add(stackInSlot);
                    inventory.getCombinedInventory().setStack(i, ItemStack.EMPTY);
                }
            }

            if(!stacks.isEmpty())
            {
                if(!source.getWorld().isClient)
                {
                    ItemScatterer.spawn(source.getWorld(), serverPlayer.getBlockPos(), stacks);
                    flag.set(true);
                }
            }

            if(flag.get())
            {
                source.sendFeedback(() -> Text.literal("Dropping contents of " + serverPlayer.getDisplayName().getString() + " backpack at " + serverPlayer.getBlockPos().toShortString()), true);
                return 1;
            }
            else
            {
                source.sendError(Text.literal("There's no contents in " + serverPlayer.getDisplayName().getString() + " backpack"));
                return -1;
            }
        }
        else
        {
            source.sendError(Text.literal("Player " + serverPlayer.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}