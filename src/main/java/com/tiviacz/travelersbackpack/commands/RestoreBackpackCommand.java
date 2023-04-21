package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
        LiteralArgumentBuilder<CommandSource> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("restore")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("backpack_id", UUIDArgument.uuid())
                                .executes(source -> restoreBackpack(source.getSource(), UUIDArgument.getUuid(source, "backpack_id"), EntityArgument.getPlayer(source, "target"))))));

        dispatcher.register(tbCommand);
    }

    public int restoreBackpack(CommandSource source, UUID backpackID, ServerPlayerEntity player) throws CommandSyntaxException
    {
        ItemStack backpack = BackpackManager.getBackpack(player.getLevel(), backpackID);
        if(backpack == null)
        {
            source.sendFailure(new StringTextComponent("Backpack with ID " + backpackID.toString() + " not found"));
            return 0;
        }

        if(!player.inventory.add(backpack))
        {
            player.drop(backpack, false);
        }

        source.sendSuccess(new StringTextComponent("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
        return 1;
    }
}