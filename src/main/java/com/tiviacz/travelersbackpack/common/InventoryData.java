package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.*;

public class InventoryData extends SavedData
{
    private static final String SAVED_DATA_NAME = TravelersBackpack.MODID;

    private final Map<UUID, CompoundTag> backpackContents = new HashMap<>();
    private static final InventoryData clientStorageCopy = new InventoryData();

    private InventoryData() {}

    public static InventoryData get() {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null)
            {
                ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                //noinspection ConstantConditions - by this time overworld is loaded
                DimensionDataStorage storage = overworld.getDataStorage();
                return storage.computeIfAbsent(InventoryData::load, InventoryData::new, SAVED_DATA_NAME);
            }
        }
        return clientStorageCopy;
    }

    public static InventoryData load(CompoundTag nbt) {
        InventoryData storage = new InventoryData();
        readBackpackContents(nbt, storage);
        return storage;
    }

    private static void readBackpackContents(CompoundTag nbt, InventoryData storage)
    {
        for(Tag n : nbt.getList("backpackContents", Tag.TAG_COMPOUND)) {
            CompoundTag uuidContentsPair = (CompoundTag) n;
            UUID uuid = NbtUtils.loadUUID(Objects.requireNonNull(uuidContentsPair.get("uuid")));
            CompoundTag contents = uuidContentsPair.getCompound("contents");
            storage.backpackContents.put(uuid, contents);
        }
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        CompoundTag ret = new CompoundTag();
        writeBackpackContents(ret);
        return ret;
    }

    private void writeBackpackContents(CompoundTag ret) {
        ListTag backpackContentsNbt = new ListTag();
        for (Map.Entry<UUID, CompoundTag> entry : backpackContents.entrySet()) {
            CompoundTag uuidContentsPair = new CompoundTag();
            uuidContentsPair.put("uuid", NbtUtils.createUUID(entry.getKey()));
            uuidContentsPair.put("contents", entry.getValue());
            backpackContentsNbt.add(uuidContentsPair);
        }
        ret.put("backpackContents", backpackContentsNbt);
    }

    public CompoundTag getOrCreateBackpackContents(UUID backpackUuid) {
        return backpackContents.computeIfAbsent(backpackUuid, uuid -> {
            setDirty();
            return new CompoundTag();
        });
    }

    public void removeBackpackContents(UUID backpackUuid) {
        backpackContents.remove(backpackUuid);
    }

    public void setBackpackContents(UUID backpackUuid, CompoundTag contents) {
        if (!backpackContents.containsKey(backpackUuid)) {
            backpackContents.put(backpackUuid, contents);
            updatedBackpackSettingsFlags.add(backpackUuid);
        } else {
            CompoundTag currentContents = backpackContents.get(backpackUuid);
            for (String key : contents.getAllKeys()) {
                //noinspection ConstantConditions - the key is one of the tag keys so there's no reason it wouldn't exist here
                currentContents.put(key, contents.get(key));
            }
            setDirty();
        }
    }

    private final Set<UUID> updatedBackpackSettingsFlags = new HashSet<>();

    public boolean removeUpdatedBackpackSettingsFlag(UUID backpackUuid) {
        return updatedBackpackSettingsFlags.remove(backpackUuid);
    }
}