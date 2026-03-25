package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class BackpackIconCommands {
    public BackpackIconCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommandManager.literal("tb_client")
                .then(ClientCommandManager.literal("hide").executes(source -> hideIcon(source.getSource())))
                .then(ClientCommandManager.literal("show").executes(source -> showIcon())));
    }

    public int hideIcon(FabricClientCommandSource source) {
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(false);
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
        source.sendFeedback(Component.translatable("screen.travelersbackpack.hidden_icon_info"));
        return 1;
    }

    public int showIcon() {
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(true);
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
        return 1;
    }
}
