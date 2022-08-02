package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ModCommands
{
    public static void registerCommands()
    {
        CommandRegistrationCallback.EVENT.register(AccessBackpackCommand::register);
    }
}