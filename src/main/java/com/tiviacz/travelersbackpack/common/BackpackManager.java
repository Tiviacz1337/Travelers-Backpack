package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;

import javax.annotation.Nullable;
import java.io.File;
import java.util.UUID;

public class BackpackManager
{
    public static LevelResource BACKPACKS = new LevelResource("backpacks");

    public static void addBackpack(ServerPlayer player, ItemStack stack)
    {
        try {
            UUID randomBackpackUUID = UUID.randomUUID();
            File backpackFile = getBackpackFile(player, randomBackpackUUID);
            backpackFile.getParentFile().mkdirs();
            NbtIo.write(stack.save(new CompoundTag()), backpackFile);
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + randomBackpackUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerPlayer player, UUID id) {
        return getBackpack(player.serverLevel(), player.getUUID(), id);
    }

    @Nullable
    public static ItemStack getBackpack(ServerLevel level, UUID playerUUID, UUID id) {
        try {
            CompoundTag data = NbtIo.read(getBackpackFile(level, playerUUID, id));
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
            CompoundTag data = NbtIo.read(file);
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
    public static ItemStack getBackpack(ServerLevel level, UUID id) {
        File deathFolder = getBackpackFolder(level);
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
                return getBackpack(level, UUID.fromString(f.getName()), id);
            }
        }
        return null;
    }

    public static File getBackpackFile(ServerLevel level, UUID playerUUID, UUID id) {
        return new File(getPlayerBackpackFolder(level, playerUUID), id.toString() + ".dat");
    }

    public static File getBackpackFile(ServerPlayer player, UUID id) {
        return new File(getPlayerBackpackFolder(player), id.toString() + ".dat");
    }

    public static File getPlayerBackpackFolder(ServerPlayer player) {
        return getPlayerBackpackFolder(player.serverLevel(), player.getUUID());
    }

    public static File getPlayerBackpackFolder(ServerLevel level, UUID uuid) {
        return new File(getBackpackFolder(level), uuid.toString());
    }

    public static File getBackpackFolder(ServerLevel level) {
        return getWorldFolder(level, BACKPACKS);
    }

    public static File getWorldFolder(ServerLevel serverLevel, LevelResource levelResource) {
        return serverLevel.getServer().getWorldPath(levelResource).toFile();
    }
}