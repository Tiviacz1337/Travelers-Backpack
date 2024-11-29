package com.tiviacz.travelersbackpackneo.init;

import com.tiviacz.travelersbackpackold.commands.AccessBackpackCommand;
import com.tiviacz.travelersbackpackold.commands.ClearBackpackCommand;
import com.tiviacz.travelersbackpackold.commands.RestoreBackpackCommand;
import com.tiviacz.travelersbackpackold.commands.UnpackBackpackCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

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