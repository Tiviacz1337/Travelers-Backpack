package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.mixin.WorldSavePathMixin;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackpackManager
{
    public static WorldSavePath BACKPACKS = WorldSavePathMixin.invokeInit("backpacks");

    public static void addBackpack(ServerPlayerEntity player, ItemStack stack)
    {
        try {
            LocalDateTime deathTime = LocalDateTime.now();
            //Format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.mm.ss");
            String formattedDeathTime = deathTime.format(formatter);

            String datedBackpackName = stack.getRegistryEntry().getKey().get().getRegistry().toString().replace(":", ".") + "_" + formattedDeathTime;
            File backpackFile = getBackpackFile(player, datedBackpackName);
            backpackFile.getParentFile().mkdirs();
            NbtIo.write(stack.writeNbt(new NbtCompound()), backpackFile.toPath());
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + datedBackpackName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack readBackpack(ServerPlayerEntity serverPlayer, String backpackId) {
        try {
            NbtCompound data = NbtIo.read(getBackpackFile(serverPlayer, backpackId).toPath());
            if(data == null)
            {
                return null;
            }
            return ItemStack.fromNbt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerPlayerEntity player, String backpackId)
    {
        File deathFolder = getBackpackFolder(player.getServerWorld());
        File[] backpacks = deathFolder.listFiles((dir, name) -> name.equals(player.getName().getString()));

        if(backpacks != null && backpacks.length > 0)
        {
            return readBackpack(player, backpackId);
        }
        return null;
    }

    public static File getBackpackFile(ServerPlayerEntity player, String backpackId) {
        return new File(getPlayerBackpackFolder(player), backpackId);
    }

    public static File getPlayerBackpackFolder(ServerPlayerEntity player) {
        return new File(getBackpackFolder(player.getServerWorld()), player.getName().getString());
    }

    public static File getBackpackFolder(ServerWorld world) {
        return getWorldFolder(world, BACKPACKS);
    }

    public static File getWorldFolder(ServerWorld serverWorld, WorldSavePath path) {
        return serverWorld.getServer().getSavePath(path).toFile();
    }
}