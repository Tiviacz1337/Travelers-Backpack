package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

public class BackpackManager
{
    public static FolderName BACKPACKS = new FolderName("backpacks");

    public static void addBackpack(ServerPlayerEntity player, ItemStack stack)
    {
        try {
            UUID randomBackpackUUID = UUID.randomUUID();
            File backpackFile = getBackpackFile(player, randomBackpackUUID);
            backpackFile.getParentFile().mkdirs();
            CompressedStreamTools.write(stack.save(new CompoundNBT()), backpackFile);
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + randomBackpackUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerPlayerEntity player, UUID id) {
        return getBackpack(player.getLevel(), player.getUUID(), id);
    }

    @Nullable
    public static ItemStack getBackpack(ServerWorld world, UUID playerUUID, UUID id) {
        try {
            CompoundNBT data = CompressedStreamTools.read(getBackpackFile(world, playerUUID, id));
            if (data == null) {
                return null;
            }
            return ItemStack.of(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack getBackpack(File file) {
        try {
            CompoundNBT data = CompressedStreamTools.read(file);
            if (data == null) {
                return null;
            }
            return ItemStack.of(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerWorld world, UUID id) {
        File deathFolder = getBackpackFolder(world);
        File[] players = deathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        if (players == null) {
            return null;
        }

        for (File f : players) {
            if (!f.isDirectory()) {
                continue;
            }
            File[] files = f.listFiles((dir, name) -> name.equals(id.toString() + ".dat"));
            if (files != null && files.length > 0) {
                return getBackpack(world, UUID.fromString(f.getName()), id);
            }
        }
        return null;
    }

    public static File getBackpackFile(ServerWorld world, UUID playerUUID, UUID id) {
        return new File(getPlayerBackpackFolder(world, playerUUID), id.toString() + ".dat");
    }

    public static File getBackpackFile(ServerPlayerEntity player, UUID id) {
        return new File(getPlayerBackpackFolder(player), id.toString() + ".dat");
    }

    public static File getPlayerBackpackFolder(ServerPlayerEntity player) {
        return getPlayerBackpackFolder(player.getLevel(), player.getUUID());
    }

    public static File getPlayerBackpackFolder(ServerWorld world, UUID uuid) {
        return new File(getBackpackFolder(world), uuid.toString());
    }

    public static File getBackpackFolder(ServerWorld world) {
        return getWorldFolder(world, BACKPACKS);
    }

    public static File getWorldFolder(ServerWorld serverWorld, FolderName folderName) {
        return serverWorld.getServer().getWorldPath(folderName).toFile();
    }
}