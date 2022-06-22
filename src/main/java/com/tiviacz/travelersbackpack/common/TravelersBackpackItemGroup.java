package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class TravelersBackpackItemGroup
{
    public static final ItemGroup INSTANCE = FabricItemGroupBuilder.create(new Identifier(TravelersBackpack.MODID, "group")).appendItems(TravelersBackpackItemGroup::appendItems).icon(() -> new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK)).build();

    public static void appendItems(List<ItemStack> stacks)
    {
        addItem(stacks, ModItems.SLEEPING_BAG);
        addItem(stacks, ModItems.BACKPACK_TANK);
        addItem(stacks, ModItems.HOSE_NOZZLE);
        addItem(stacks, ModItems.HOSE);

        //Standard
        addBlock(stacks, ModBlocks.STANDARD_TRAVELERS_BACKPACK);

        //Blocks
        addBlock(stacks, ModBlocks.NETHERITE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.DIAMOND_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.GOLD_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.EMERALD_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.IRON_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.LAPIS_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.REDSTONE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.COAL_TRAVELERS_BACKPACK);

        addBlock(stacks, ModBlocks.QUARTZ_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK);
        //addBlock(items, ModBlocks.CRYING_OBSIDIAN_TRAVELERS_BACKPACK);

        addBlock(stacks, ModBlocks.HAY_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.MELON_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.PUMPKIN_TRAVELERS_BACKPACK);

        addBlock(stacks, ModBlocks.BLAZE_TRAVELERS_BACKPACK);


        //Friendly Mobs
        addBlock(stacks, ModBlocks.BAT_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.WOLF_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.FOX_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.OCELOT_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.COW_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.PIG_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.CHICKEN_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.VILLAGER_TRAVELERS_BACKPACK);
    }

    public static void addItem(List<ItemStack> items, Item item)
    {
        items.add(new ItemStack(item));
    }

    public static void addBlock(List<ItemStack> items, Block block)
    {
        items.add(new ItemStack(block));
    }
}
