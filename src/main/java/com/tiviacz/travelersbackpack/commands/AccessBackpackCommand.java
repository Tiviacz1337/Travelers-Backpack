package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.tileentity.TravelersBackpackTileEntity;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class AccessBackpackCommand
{
    public AccessBackpackCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        LiteralArgumentBuilder<CommandSource> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("access")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(source -> openTargetBlockEntity(source.getSource(), BlockPosArgument.getOrLoadBlockPos(source, "pos"))))
                        .then(Commands.argument("target", EntityArgument.players())
                                .executes(source -> openTargetInventory(source.getSource(), EntityArgument.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public int openTargetBlockEntity(CommandSource source, BlockPos blockPos) throws CommandSyntaxException
    {
        if(source.getLevel().getBlockEntity(blockPos) instanceof TravelersBackpackTileEntity)
        {
            NetworkHooks.openGui(source.getPlayerOrException(), (TravelersBackpackTileEntity)source.getLevel().getBlockEntity(blockPos), blockPos);
            source.sendSuccess(new StringTextComponent("Accessing backpack of " + blockPos.toShortString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new StringTextComponent("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public int openTargetInventory(CommandSource source, ServerPlayerEntity serverPlayer) throws CommandSyntaxException
    {
        ServerPlayerEntity self = source.getPlayerOrException();
        boolean hasBackpack = CapabilityUtils.isWearingBackpack(serverPlayer);

        if(hasBackpack)
        {
            NetworkHooks.openGui(self, CapabilityUtils.getBackpackInv(serverPlayer), packetBuffer -> packetBuffer.writeByte(Reference.WEARABLE_SCREEN_ID).writeInt(serverPlayer.getEntity().getId()));
            source.sendSuccess(new StringTextComponent("Accessing backpack of " + serverPlayer.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new StringTextComponent("Can't access backpack"));
            return -1;
        }
    }
}