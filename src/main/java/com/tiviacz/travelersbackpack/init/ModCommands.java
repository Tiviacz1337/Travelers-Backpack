package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpack.commands.ClearBackpackCommand;
import com.tiviacz.travelersbackpack.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpack.commands.UnpackBackpackCommand;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class ModCommands
{
    public static void registerCommands()
    {
        CommandRegistrationCallback.EVENT.register(AccessBackpackCommand::register);
        CommandRegistrationCallback.EVENT.register(RestoreBackpackCommand::register);
        CommandRegistrationCallback.EVENT.register(ClearBackpackCommand::register);
        CommandRegistrationCallback.EVENT.register(UnpackBackpackCommand::register);
    }
}