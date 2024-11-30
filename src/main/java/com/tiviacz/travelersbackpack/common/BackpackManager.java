package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BackpackManager {
    public static LevelResource BACKPACKS = new LevelResource("backpacks");

    public static void addBackpack(ServerPlayer player, ItemStack stack) {
        try {
            LocalDateTime deathTime = LocalDateTime.now();
            //Format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.mm.ss");
            String formattedDeathTime = deathTime.format(formatter);

            String datedBackpackName = stack.getItemHolder().getRegisteredName().replace(":", ".") + "_" + formattedDeathTime + ".dat";
            File backpackFile = getBackpackFile(player, datedBackpackName);
            backpackFile.getParentFile().mkdirs();
            NbtIo.write((CompoundTag)stack.save(player.registryAccess()), backpackFile.toPath());
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + datedBackpackName);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack readBackpack(ServerLevel serverLevel, UUID playerUUID, String backpackId) {
        try {
            CompoundTag data = NbtIo.read(getBackpackFile(serverLevel, playerUUID, backpackId).toPath());
            if(data == null) {
                return null;
            }
            return ItemStack.parseOptional(serverLevel.registryAccess(), data);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerLevel serverLevel, String backpackId) {
        File deathFolder = getBackpackFolder(serverLevel);
        File[] players = deathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        if(players == null) {
            return null;
        }

        for(File f : players) {
            if(!f.isDirectory()) {
                continue;
            }
            File[] files = f.listFiles((dir, name) -> name.equals(backpackId));
            if(files != null && files.length > 0) {
                return readBackpack(serverLevel, UUID.fromString(f.getName()), backpackId);
            }
        }
        return null;
    }

    public static File getBackpackFile(ServerLevel serverLevel, UUID playerUUID, String backpackId) {
        return new File(getPlayerBackpackFolder(serverLevel, playerUUID), backpackId);
    }

    public static File getBackpackFile(ServerPlayer player, String backpackId) {
        return new File(getPlayerBackpackFolder(player), backpackId);
    }

    public static File getPlayerBackpackFolder(ServerPlayer player) {
        return getPlayerBackpackFolder(player.serverLevel(), player.getUUID());
    }

    public static File getPlayerBackpackFolder(ServerLevel serverLevel, UUID uuid) {
        return new File(getBackpackFolder(serverLevel), uuid.toString());
    }

    public static File getBackpackFolder(ServerLevel serverLevel) {
        return getWorldFolder(serverLevel, BACKPACKS);
    }

    public static File getWorldFolder(ServerLevel serverLevel, LevelResource path) {
        return serverLevel.getServer().getWorldPath(path).toFile();
    }
}