package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.blockentity.TravelersBackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;

public class AccessBackpackCommand
{
    public AccessBackpackCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("access")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(source -> openTargetBlockEntity(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"))))
                        .then(Commands.argument("target", EntityArgument.players())
                                .executes(source -> openTargetInventory(source.getSource(), EntityArgument.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public int openTargetBlockEntity(CommandSourceStack source, BlockPos blockPos) throws CommandSyntaxException
    {
        if(source.getLevel().getBlockEntity(blockPos) instanceof TravelersBackpackBlockEntity)
        {
            NetworkHooks.openGui(source.getPlayerOrException(), (TravelersBackpackBlockEntity)source.getLevel().getBlockEntity(blockPos), blockPos);
            source.sendSuccess(new TextComponent("Accessing backpack of " + blockPos.toShortString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new TextComponent("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public int openTargetInventory(CommandSourceStack source, ServerPlayer serverPlayer) throws CommandSyntaxException
    {
        ServerPlayer self = source.getPlayerOrException();
        boolean hasBackpack = CapabilityUtils.isWearingBackpack(serverPlayer);

        if(hasBackpack)
        {
            NetworkHooks.openGui(self, CapabilityUtils.getBackpackInv(serverPlayer), packetBuffer -> packetBuffer.writeByte(Reference.WEARABLE_SCREEN_ID).writeInt(serverPlayer.getId()));
            source.sendSuccess(new TextComponent("Accessing backpack of " + serverPlayer.getDisplayName().getString()), true);
            return 1;
        }
        else
        {
            source.sendFailure(new TextComponent("Can't access backpack"));
            return -1;
        }
    }
}