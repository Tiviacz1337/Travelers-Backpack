package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BackpackIconCommands {
    public static class Hide {
        public Hide(CommandDispatcher<CommandSourceStack> dispatcher) {
            LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb");
            tbCommand.then(Commands.literal("hide").executes(source -> hideIcon(source.getSource())));
            dispatcher.register(tbCommand);
        }

        public int hideIcon(CommandSourceStack source) {
            TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(false);
            TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
            source.sendSuccess(() -> Component.translatable("screen.travelersbackpack.hidden_icon_info"), true);
            return 1;
        }
    }
    public static class Show {
        public Show(CommandDispatcher<CommandSourceStack> dispatcher) {
            LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb");
            tbCommand.then(Commands.literal("show").executes(source -> showIcon()));
            dispatcher.register(tbCommand);
        }

        public int showIcon() {
            TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.set(true);
            TravelersBackpackConfig.CLIENT.showBackpackIconInInventory.save();
            return 1;
        }
    }
}
