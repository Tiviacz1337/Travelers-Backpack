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
import java.util.UUID;

public class BackpackManager
{
    public static WorldSavePath BACKPACKS = WorldSavePathMixin.invokeInit("backpacks");

    public static void addBackpack(ServerPlayerEntity player, ItemStack stack)
    {
        try {
            UUID randomBackpackUUID = UUID.randomUUID();
            File backpackFile = getBackpackFile(player, randomBackpackUUID);
            backpackFile.getParentFile().mkdirs();
            NbtIo.write(stack.writeNbt(new NbtCompound()), backpackFile);
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + randomBackpackUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerPlayerEntity player, UUID id) {
        return getBackpack(player.getServerWorld(), player.getUuid(), id);
    }

    @Nullable
    public static ItemStack getBackpack(ServerWorld world, UUID playerUUID, UUID id) {
        try {
            NbtCompound data = NbtIo.read(getBackpackFile(world, playerUUID, id));
            if (data == null) {
                return null;
            }
            return ItemStack.fromNbt(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack getBackpack(File file) {
        try {
            NbtCompound data = NbtIo.read(file);
            if (data == null) {
                return null;
            }
            return ItemStack.fromNbt(data);
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
        return getPlayerBackpackFolder(player.getServerWorld(), player.getUuid());
    }

    public static File getPlayerBackpackFolder(ServerWorld world, UUID uuid) {
        return new File(getBackpackFolder(world), uuid.toString());
    }

    public static File getBackpackFolder(ServerWorld world) {
        return getWorldFolder(world, BACKPACKS);
    }

    public static File getWorldFolder(ServerWorld serverWorld, WorldSavePath path) {
        return serverWorld.getServer().getSavePath(path).toFile();
    }
}