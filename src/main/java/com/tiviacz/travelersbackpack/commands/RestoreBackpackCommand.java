package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;

import java.util.UUID;

public class RestoreBackpackCommand
{
    public RestoreBackpackCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> literalargumentbuilder = Commands.literal("tb").requires(player -> player.hasPermission(2));

        Command<CommandSource> restore = (commandSource) -> {
            UUID backpackID = UUIDArgument.getUuid(commandSource, "backpack_id");
            ServerPlayerEntity player = EntityArgument.getPlayer(commandSource, "target");
            ItemStack backpack = BackpackManager.getBackpack(player.getLevel(), backpackID);
            if(backpack == null)
            {
                commandSource.getSource().sendFailure(new StringTextComponent("Backpack with ID " + backpackID.toString() + " not found"));
                return 0;
            }

            if(!player.inventory.add(backpack))
            {
                player.drop(backpack, false);
            }

            commandSource.getSource().sendSuccess(new StringTextComponent("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
            return 1;
        };

        literalargumentbuilder
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("backpack_id", UUIDArgument.uuid())
                                .then(Commands.literal("restore").executes(restore))
                        ));


        dispatcher.register(literalargumentbuilder);
    }
}