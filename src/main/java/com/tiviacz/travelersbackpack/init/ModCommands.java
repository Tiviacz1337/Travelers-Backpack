package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ModCommands
{
    public static void registerCommands()
    {
        CommandRegistrationCallback.EVENT.register(AccessBackpackCommand::register);
        CommandRegistrationCallback.EVENT.register(RestoreBackpackCommand::register);
    }
}