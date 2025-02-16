package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BackpackIconCommands {
    public BackpackIconCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tb_client")
                .then(Commands.literal("hide").executes(source -> hideIcon(source.getSource())))
                .then(Commands.literal("show").executes(source -> showIcon())));
    }

    public int hideIcon(CommandSourceStack source) {
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(false);
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
        source.sendSuccess(() -> Component.translatable("screen.travelersbackpack.hidden_icon_info"), true);
        return 1;
    }

    public int showIcon() {
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(true);
        TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
        return 1;
    }
}