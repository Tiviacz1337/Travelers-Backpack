package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.items.HoseItem;
import com.tiviacz.travelersbackpack.items.SleepingBagItem;
import com.tiviacz.travelersbackpack.items.TravelersBackpackItem;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TravelersBackpack.MODID);
    public static final List<Item> BACKPACKS = new ArrayList<>();

    public static final RegistryObject<Item> STANDARD_TRAVELERS_BACKPACK = ITEMS.register("standard", () -> new TravelersBackpackItem(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get()));
    //public static final RegistryObject<BlockItem> BAT_TRAVELERS_BACKPACK = ITEMS.register("bat", () -> new TravelersBackpackItem(ModBlocks.BAT_TRAVELERS_BACKPACK.get()));
    public static final RegistryObject<Item> SLEEPING_BAG = ITEMS.register("sleeping_bag", () -> new SleepingBagItem(new Item.Properties().group(Reference.TRAVELERS_BACKPACK_TAB)));
    public static final RegistryObject<Item> BACKPACK_TANK = ITEMS.register("backpack_tank", () -> new Item(new Item.Properties().group(Reference.TRAVELERS_BACKPACK_TAB).maxStackSize(16)));
    public static final RegistryObject<Item> HOSE = ITEMS.register("hose", () -> new HoseItem(new Item.Properties().group(Reference.TRAVELERS_BACKPACK_TAB).maxStackSize(1)));
    public static final RegistryObject<Item> HOSE_NOZZLE = ITEMS.register("hose_nozzle", () -> new Item(new Item.Properties().group(Reference.TRAVELERS_BACKPACK_TAB)));

    public static void addBackpacksToList()
    {
        add(() -> STANDARD_TRAVELERS_BACKPACK.get());
    }

    public static void add(Supplier<Item> itemSupplier)
    {
        BACKPACKS.add(itemSupplier.get());
    }
}
