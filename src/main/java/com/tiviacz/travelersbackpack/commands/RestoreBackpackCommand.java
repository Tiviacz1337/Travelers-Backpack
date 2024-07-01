package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestoreBackpackCommand
{
    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = (context, builder) ->
    {
        File backpacksFolder = BackpackManager.getBackpackFolder(context.getSource().getLevel());
        if(backpacksFolder.listFiles() == null) return Suggestions.empty();

        List<String> backpackEntries = new ArrayList<>();

        for(File file : backpacksFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")))
        {
            if(file.listFiles() == null) continue;

            backpackEntries.addAll(Arrays.stream(file.listFiles()).collect(ArrayList::new, (list, backpack) -> list.add(backpack.getName()), List::addAll));
        }
        return SharedSuggestionProvider.suggest(backpackEntries.stream(), builder);
    };

    public RestoreBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("restore")
                .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("backpack_id", StringArgumentType.string()).suggests(SUGGESTION_PROVIDER)
                                .executes(source -> restoreBackpack(source.getSource(), StringArgumentType.getString(source, "backpack_id"), EntityArgument.getPlayer(source, "target"))))));

        dispatcher.register(tbCommand);
    }

    public int restoreBackpack(CommandSourceStack source, String backpackID, ServerPlayer player)
    {
        ItemStack backpack = BackpackManager.getBackpack(player.serverLevel(), backpackID);
        if(backpack == null)
        {
            source.sendFailure(Component.literal("Backpack with ID " + backpackID + " not found"));
            return 0;
        }

        if(!player.getInventory().add(backpack))
        {
            player.drop(backpack, false);
        }

        source.sendSuccess(() -> Component.literal("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
        return 1;
    }
}