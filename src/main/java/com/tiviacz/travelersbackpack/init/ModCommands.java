package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.commands.AccessCommand;
import com.tiviacz.travelersbackpack.commands.ClearCommand;
import com.tiviacz.travelersbackpack.commands.RestoreCommand;
import com.tiviacz.travelersbackpack.commands.UnpackCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(AccessCommand::new);
        CommandRegistrationCallback.EVENT.register(ClearCommand::new);
        CommandRegistrationCallback.EVENT.register(RestoreCommand::new);
        CommandRegistrationCallback.EVENT.register(UnpackCommand::new);
    }
}
