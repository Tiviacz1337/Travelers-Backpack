package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class RestoreBackpackCommand
{
    public RestoreBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("restore")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("backpack_id", UuidArgument.uuid())
                                .executes(source -> restoreBackpack(source.getSource(), UuidArgument.getUuid(source, "backpack_id"), EntityArgument.getPlayer(source, "target"))))));

        dispatcher.register(tbCommand);
    }

    public int restoreBackpack(CommandSourceStack source, UUID backpackID, ServerPlayer player) throws CommandSyntaxException
    {
        ItemStack backpack = BackpackManager.getBackpack(player.getLevel(), backpackID);
        if(backpack == null)
        {
            source.sendFailure(new TextComponent("Backpack with ID " + backpackID.toString() + " not found"));
            return 0;
        }

        if(!player.getInventory().add(backpack))
        {
            player.drop(backpack, false);
        }

        source.sendSuccess(new TextComponent("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
        return 1;
    }
}