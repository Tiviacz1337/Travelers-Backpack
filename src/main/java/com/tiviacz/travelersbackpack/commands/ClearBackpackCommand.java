package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;

public class ClearBackpackCommand
{
    public ClearBackpackCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

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

    private static int removeBackpack(CommandSource source, ServerPlayerEntity player) throws CommandSyntaxException
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

            source.sendSuccess(new StringTextComponent("Removed Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new StringTextComponent("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }

    private static int clearBackpack(CommandSource source, ServerPlayerEntity player) throws CommandSyntaxException
    {
        if(CapabilityUtils.isWearingBackpack(player))
        {
            CapabilityUtils.getCapability(player).ifPresent(cap ->
            {
                ItemStack stack = cap.getWearable();
                stack.setTag(new CompoundNBT());

                cap.setWearable(stack);
                cap.setContents(stack);

                cap.synchronise();
                cap.synchroniseToOthers(player);
            });

            source.sendSuccess(new StringTextComponent("Cleared contents of Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new StringTextComponent("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}