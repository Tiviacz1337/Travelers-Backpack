package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearBackpackCommand
{
    public ClearBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("remove")
                        .executes(source -> removeBackpack(source.getSource(), source.getSource().getPlayerOrException()))
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(source -> removeBackpack(source.getSource(), EntityArgument.getPlayer(source, "player")))));

        tbCommand.then(Commands.literal("clear")
                        .executes(source -> clearBackpack(source.getSource(), source.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(source -> clearBackpack(source.getSource(), EntityArgument.getPlayer(source, "player")))));

        dispatcher.register(tbCommand);
    }

    private static int removeBackpack(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            CapabilityUtils.getCapability(player).ifPresent(cap ->
            {
                cap.setWearable(ItemStack.EMPTY);
                cap.setContents(ItemStack.EMPTY);

                cap.synchronise();
                cap.synchroniseToOthers(player);
            });

            source.sendSuccess(() -> Component.literal("Removed Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(Component.literal("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }

    private static int clearBackpack(CommandSourceStack source, ServerPlayer player) throws CommandSyntaxException
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            CapabilityUtils.getCapability(player).ifPresent(cap ->
            {
                ItemStack stack = cap.getWearable();
                stack.setTag(new CompoundTag());

                cap.setWearable(stack);
                cap.setContents(stack);

                cap.synchronise();
                cap.synchroniseToOthers(player);
            });

            source.sendSuccess(() -> Component.literal("Cleared contents of Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(Component.literal("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}