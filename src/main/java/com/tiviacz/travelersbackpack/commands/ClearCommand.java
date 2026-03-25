package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.attachment.AttachmentUtils;
import com.tiviacz.travelersbackpack.init.ModDataComponents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.ItemStack;

public class ClearCommand {
    public ClearCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));

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

    private static int removeBackpack(CommandSourceStack source, ServerPlayer player) {
        if(AttachmentUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return -1;

            AttachmentUtils.getAttachment(player).ifPresent(data -> {
                if(!player.addItem(data.getBackpack().copy())) {
                    player.drop(data.getBackpack().copy(), true);
                }
                data.equipBackpack(ItemStack.EMPTY, player);
                data.synchronise(player);
            });
            source.sendSuccess(() -> Component.literal("Removed Traveler's Backpack from " + player.getDisplayName().getString() + " and added copy to inventory"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }

    private static int clearBackpack(CommandSourceStack source, ServerPlayer player) {
        if(AttachmentUtils.isWearingBackpack(player)) {
            if(TravelersBackpack.enableIntegration()) return -1;

            AttachmentUtils.getAttachment(player).ifPresent(data -> {
                ItemStack stack = data.getBackpack().copy();
                if(!player.addItem(stack.copy())) {
                    player.drop(stack.copy(), true);
                }
                int tier = stack.getOrDefault(ModDataComponents.TIER, 0);
                ItemStack clearedStack = stack.getItem().getDefaultInstance();
                clearedStack.set(ModDataComponents.TIER, tier);
                data.equipBackpack(clearedStack, player);
                data.synchronise(player);
            });
            source.sendSuccess(() -> Component.literal("Cleared contents of Traveler's Backpack from " + player.getDisplayName().getString() + " and added copy to inventory"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Player " + player.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }
}