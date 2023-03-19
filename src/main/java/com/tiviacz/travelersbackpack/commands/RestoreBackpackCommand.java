package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class RestoreBackpackCommand
{
    public RestoreBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalargumentbuilder = Commands.literal("tb").requires(player -> player.hasPermission(2));

        Command<CommandSourceStack> restore = (commandSource) -> {
            UUID backpackID = UuidArgument.getUuid(commandSource, "backpack_id");
            ServerPlayer player = EntityArgument.getPlayer(commandSource, "target");
            ItemStack backpack = BackpackManager.getBackpack(player.getLevel(), backpackID);
            if(backpack == null)
            {
                commandSource.getSource().sendFailure(Component.literal("Backpack with ID " + backpackID.toString() + " not found"));
                return 0;
            }

            if(!player.getInventory().add(backpack))
            {
                player.drop(backpack, false);
            }

            commandSource.getSource().sendSuccess(Component.literal("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
            return 1;
        };

        literalargumentbuilder
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("backpack_id", UuidArgument.uuid())
                                .then(Commands.literal("restore").executes(restore))
                        ));


        dispatcher.register(literalargumentbuilder);
    }
}