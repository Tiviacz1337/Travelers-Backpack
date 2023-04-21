package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.component.ComponentUtils;
import com.tiviacz.travelersbackpack.component.ITravelersBackpackComponent;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class ClearBackpackCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
    {
        LiteralArgumentBuilder<ServerCommandSource> tbCommand = CommandManager.literal("tb").requires(player -> player.hasPermissionLevel(2));

        tbCommand.then(CommandManager.literal("remove")
                        .executes(source -> removeBackpack(source.getSource(), source.getSource().getPlayer()))
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(source -> removeBackpack(source.getSource(), EntityArgumentType.getPlayer(source, "player")))));

        tbCommand.then(CommandManager.literal("clear")
                        .executes(source -> clearBackpack(source.getSource(), source.getSource().getPlayer()))
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(source -> clearBackpack(source.getSource(), EntityArgumentType.getPlayer(source, "player")))));

        dispatcher.register(tbCommand);
    }

    private static int removeBackpack(ServerCommandSource source, ServerPlayerEntity player) throws CommandSyntaxException
    {
        if(ComponentUtils.isWearingBackpack(player))
        {
            ITravelersBackpackComponent component = ComponentUtils.getComponent(player);

            component.setWearable(ItemStack.EMPTY);
            component.setContents(ItemStack.EMPTY);

            component.sync();
            component.syncToTracking(player);

            source.sendFeedback(new LiteralText("Removed Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendError(new LiteralText("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }

    private static int clearBackpack(ServerCommandSource source, ServerPlayerEntity player) throws CommandSyntaxException
    {
        if(ComponentUtils.isWearingBackpack(player))
        {
            ITravelersBackpackComponent component = ComponentUtils.getComponent(player);

            ItemStack stack = component.getWearable();
            stack.setTag(new NbtCompound());

            component.setWearable(ItemStack.EMPTY);
            component.setContents(ItemStack.EMPTY);

            component.sync();
            component.syncToTracking(player);

            source.sendFeedback(new LiteralText("Cleared contents of Traveler's Backpack from " + player.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendError(new LiteralText("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}