package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class BackpackManager
{
    public static WorldSavePath BACKPACKS = new WorldSavePath("backpacks");

    public static void addBackpack(ServerPlayerEntity player, ItemStack stack)
    {
        try {
            LocalDateTime deathTime = LocalDateTime.now();
            //Format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH.mm.ss");
            String formattedDeathTime = deathTime.format(formatter);

            String datedBackpackName = Registries.ITEM.getId(stack.getItem()).toString().replace(":", ".") + "_" + formattedDeathTime + ".dat";
            File backpackFile = getBackpackFile(player, datedBackpackName);
            backpackFile.getParentFile().mkdirs();
            NbtIo.write(stack.writeNbt(new NbtCompound()), backpackFile);
            LogHelper.info("Created new backpack backup file for " + player.getDisplayName().getString() + " with unique ID " + datedBackpackName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public static ItemStack getBackpack(ServerWorld world, UUID playerUUID, String backpackId) {
        try {
            NbtCompound data = NbtIo.read(getBackpackFile(world, playerUUID, backpackId));
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
    public static ItemStack getBackpack(ServerWorld world, String backpackId)
    {
        File deathFolder = getBackpackFolder(world);
        File[] players = deathFolder.listFiles((dir, name) -> name.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));

        if(players == null)
        {
            return null;
        }

        for(File f : players)
        {
            if(!f.isDirectory())
            {
                continue;
            }
            File[] files = f.listFiles((dir, name) -> name.equals(backpackId));
            if(files != null && files.length > 0)
            {
                return getBackpack(world, UUID.fromString(f.getName()), backpackId);
            }
        }
        return null;
    }

    public static File getBackpackFile(ServerWorld world, UUID playerUUID, String backpackId) {
        return new File(getPlayerBackpackFolder(world, playerUUID), backpackId);
    }

    public static File getBackpackFile(ServerPlayerEntity player, String backpackId) {
        return new File(getPlayerBackpackFolder(player), backpackId);
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