package com.tiviacz.travelersbackpack.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blockentity.BackpackBlockEntity;
import com.tiviacz.travelersbackpack.capability.AttachmentUtils;
import com.tiviacz.travelersbackpack.inventory.BackpackWrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnpackCommand {
    public UnpackCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> tbCommand = Commands.literal("tb").requires(player -> player.hasPermission(2));

        tbCommand.then(Commands.literal("unpack")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(source -> unpackTargetBlockEntity(source.getSource(), BlockPosArgument.getLoadedBlockPos(source, "pos"))))
                .then(Commands.argument("target", EntityArgument.players())
                        .executes(source -> unpackTargetInventory(source.getSource(), EntityArgument.getPlayer(source, "target")))));

        dispatcher.register(tbCommand);
    }

    public int unpackTargetBlockEntity(CommandSourceStack source, BlockPos blockPos) {
        if(source.getLevel().getBlockEntity(blockPos) instanceof BackpackBlockEntity blockEntity) {
            NonNullList<ItemStack> stacks = collectItems(blockEntity.getWrapper());
            if(!stacks.isEmpty()) {
                if(!source.getLevel().isClientSide) {
                    Containers.dropContents(source.getLevel(), blockPos, stacks);
                }
                source.sendSuccess(() -> Component.literal("Dropping contents of backpack placed at " + blockPos.toShortString()), true);
                return 1;
            } else {
                source.sendFailure(Component.literal("There's no contents in backpack at coordinates " + blockPos.toShortString()));
                return -1;
            }
        } else {
            source.sendFailure(Component.literal("There's no backpack at coordinates " + blockPos.toShortString()));
            return -1;
        }
    }

    public int unpackTargetInventory(CommandSourceStack source, ServerPlayer serverPlayer) {
        boolean hasBackpack = AttachmentUtils.isWearingBackpack(serverPlayer);
        if(TravelersBackpack.enableIntegration()) return -1;

        if(hasBackpack) {
            AtomicBoolean flag = new AtomicBoolean(false);
            AttachmentUtils.getAttachment(serverPlayer).ifPresent(data -> {
                NonNullList<ItemStack> stacks = collectItems(data.getWrapper());
                if(!stacks.isEmpty()) {
                    if(!source.getLevel().isClientSide) {
                        data.synchronise();
                        Containers.dropContents(source.getLevel(), serverPlayer.blockPosition(), stacks);
                        flag.set(true);
                    }
                }
            });
            if(flag.get()) {
                source.sendSuccess(() -> Component.literal("Dropping contents of " + serverPlayer.getDisplayName().getString() + " backpack at " + serverPlayer.blockPosition().toShortString()), true);
                return 1;
            } else {
                source.sendFailure(Component.literal("There's no contents in " + serverPlayer.getDisplayName().getString() + " backpack"));
                return -1;
            }
        } else {
            source.sendFailure(Component.literal("Player " + serverPlayer.getDisplayName().getString() + " is not wearing backpack"));
            return -1;
        }
    }

    public NonNullList<ItemStack> collectItems(BackpackWrapper wrapper) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        stacks.addAll(collectItems(wrapper.getStorage()));
        stacks.addAll(collectItems(wrapper.getTools()));
        stacks.addAll(collectItems(wrapper.getUpgrades()));
        return stacks;
    }

    public NonNullList<ItemStack> collectItems(ItemStackHandler handler) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        for(int i = 0; i < handler.getSlots(); i++) {
            ItemStack stackInSlot = handler.getStackInSlot(i);
            if(!stackInSlot.isEmpty()) {
                stacks.add(stackInSlot);
                handler.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        return stacks;
    }
}