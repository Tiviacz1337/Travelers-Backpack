package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.util.UUID;

public class RestoreBackpackCommand
{
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated)
    {
        LiteralArgumentBuilder<ServerCommandSource> tbCommand = CommandManager.literal("tb").requires(player -> player.hasPermissionLevel(2));

        tbCommand.then(CommandManager.literal("restore")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("backpack_id", UuidArgumentType.uuid())
                                .executes(source -> restoreBackpack(source.getSource(), UuidArgumentType.getUuid(source, "backpack_id"), EntityArgumentType.getPlayer(source, "target"))))));

        dispatcher.register(tbCommand);
    }

    public static int restoreBackpack(ServerCommandSource source, UUID backpackID, ServerPlayerEntity player) throws CommandSyntaxException
    {
        ItemStack backpack = BackpackManager.getBackpack(player.getServerWorld(), backpackID);
        if(backpack == null)
        {
            source.sendError(new LiteralText("Backpack with ID " + backpackID.toString() + " not found"));
            return 0;
        }

        if(!player.inventory.insertStack(backpack))
        {
            player.dropItem(backpack, false);
        }

        source.sendFeedback(new LiteralText("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
        return 1;
    }
}