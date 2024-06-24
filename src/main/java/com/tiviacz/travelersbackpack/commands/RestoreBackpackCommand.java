package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.tiviacz.travelersbackpack.common.BackpackManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestoreBackpackCommand
{
    private static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) ->
    {
        File backpacksFolder = BackpackManager.getBackpackFolder(context.getSource().getWorld());
        if(backpacksFolder.listFiles() == null) return Suggestions.empty();

        List<String> backpackEntries = new ArrayList<>();

        for(File file : backpacksFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")))
        {
            if(file.listFiles() == null) continue;

            backpackEntries.addAll(Arrays.stream(file.listFiles()).collect(ArrayList::new, (list, backpack) -> list.add(backpack.getName()), List::addAll));
        }
        return CommandSource.suggestMatching(backpackEntries.stream(), builder);
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        LiteralArgumentBuilder<ServerCommandSource> tbCommand = CommandManager.literal("tb").requires(player -> player.hasPermissionLevel(2));

        tbCommand.then(CommandManager.literal("restore")
                .then(CommandManager.argument("target", EntityArgumentType.player())
                        .then(CommandManager.argument("backpack_id", StringArgumentType.string()).suggests(SUGGESTION_PROVIDER)
                                .executes(source -> restoreBackpack(source.getSource(), StringArgumentType.getString(source, "backpack_id"), EntityArgumentType.getPlayer(source, "target"))))));

        dispatcher.register(tbCommand);
    }

    public static int restoreBackpack(ServerCommandSource source, String backpackId, ServerPlayerEntity player)
    {
        ItemStack backpack = BackpackManager.getBackpack(player.getServerWorld(), backpackId);
        if(backpack == null)
        {
            source.sendError(Text.literal("Backpack with ID " + backpackId + " not found"));
            return 0;
        }

        if(!player.getInventory().insertStack(backpack))
        {
            player.dropItem(backpack, false);
        }

        source.sendFeedback(() -> Text.literal("Successfully restored " + player.getDisplayName().getString() + "'s backpack"), true);
        return 1;
    }
}