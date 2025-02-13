package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BackpackIconCommands {
    public static class Hide {
        public Hide(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
            LiteralArgumentBuilder<FabricClientCommandSource> tbCommand = ClientCommandManager.literal("tb");
            tbCommand.then(ClientCommandManager.literal("hide").executes(source -> hideIcon(source.getSource())));
            dispatcher.register(tbCommand);
        }

        public int hideIcon(FabricClientCommandSource source) {
            TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory = false;
            TravelersBackpackConfig.saveConfig();
            source.sendFeedback(Component.translatable("screen.travelersbackpack.hidden_icon_info"));
            return 1;
        }
    }
    public static class Show {
        public Show(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
            LiteralArgumentBuilder<FabricClientCommandSource> tbCommand = ClientCommandManager.literal("tb");
            tbCommand.then(ClientCommandManager.literal("show").executes(source -> showIcon()));
            dispatcher.register(tbCommand);
        }

        public int showIcon() {
            TravelersBackpackConfig.getConfig().client.showBackpackIconInInventory = true;
            TravelersBackpackConfig.saveConfig();
            return 1;
        }
    }
}
