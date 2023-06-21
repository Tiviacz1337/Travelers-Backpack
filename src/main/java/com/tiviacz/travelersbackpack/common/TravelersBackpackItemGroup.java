package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.List;

public class TravelersBackpackItemGroup
{
    public static final ItemGroup INSTANCE = FabricItemGroupBuilder.create(new Identifier(TravelersBackpack.MODID, "group")).appendItems(TravelersBackpackItemGroup::appendItems).icon(TravelersBackpackItemGroup::createIcon).build();

    public static ItemStack createIcon()
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        stack.getOrCreateNbt().put("LeftTank", FluidVariant.of(Fluids.WATER).toNbt());
        stack.getOrCreateNbt().put("RightTank", FluidVariant.of(Fluids.LAVA).toNbt());
        stack.getOrCreateNbt().putLong("LeftTankAmount", Tiers.LEATHER.getTankCapacity());
        stack.getOrCreateNbt().putLong("RightTankAmount", Tiers.LEATHER.getTankCapacity());
        return stack;
    }

    public static void appendItems(List<ItemStack> stacks)
    {
        addItem(stacks, ModItems.BACKPACK_TANK);
        addItem(stacks, ModItems.HOSE_NOZZLE);
        addItem(stacks, ModItems.HOSE);

        //Upgrades
        addItem(stacks, ModItems.BLANK_UPGRADE);
        addItem(stacks, ModItems.IRON_TIER_UPGRADE);
        addItem(stacks, ModItems.GOLD_TIER_UPGRADE);
        addItem(stacks, ModItems.DIAMOND_TIER_UPGRADE);
        addItem(stacks, ModItems.NETHERITE_TIER_UPGRADE);

        //Standard
        addBlock(stacks, ModBlocks.STANDARD_TRAVELERS_BACKPACK);
        stacks.add(createTieredBackpack(Tiers.IRON));
        stacks.add(createTieredBackpack(Tiers.GOLD));
        stacks.add(createTieredBackpack(Tiers.DIAMOND));
        stacks.add(createTieredBackpack(Tiers.NETHERITE));

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
        addBlock(stacks, ModBlocks.END_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.NETHER_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SANDSTONE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SNOW_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SPONGE_TRAVELERS_BACKPACK);

        //Food
        addBlock(stacks, ModBlocks.CAKE_TRAVELERS_BACKPACK);

        //Plants
        addBlock(stacks, ModBlocks.CACTUS_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.HAY_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.MELON_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.PUMPKIN_TRAVELERS_BACKPACK);

        //Mobs
        addBlock(stacks, ModBlocks.CREEPER_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.DRAGON_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.ENDERMAN_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.BLAZE_TRAVELERS_BACKPACK);

        addBlock(stacks, ModBlocks.GHAST_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SKELETON_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SPIDER_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.WITHER_TRAVELERS_BACKPACK);

        //Friendly Mobs
        addBlock(stacks, ModBlocks.BAT_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.BEE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.WOLF_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.FOX_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.OCELOT_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.HORSE_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.COW_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.PIG_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SHEEP_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.CHICKEN_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.SQUID_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.VILLAGER_TRAVELERS_BACKPACK);
        addBlock(stacks, ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK);

        addItem(stacks, ModItems.WHITE_SLEEPING_BAG);
        addItem(stacks, ModItems.ORANGE_SLEEPING_BAG);
        addItem(stacks, ModItems.MAGENTA_SLEEPING_BAG);
        addItem(stacks, ModItems.LIGHT_BLUE_SLEEPING_BAG);
        addItem(stacks, ModItems.YELLOW_SLEEPING_BAG);
        addItem(stacks, ModItems.LIME_SLEEPING_BAG);
        addItem(stacks, ModItems.PINK_SLEEPING_BAG);
        addItem(stacks, ModItems.GRAY_SLEEPING_BAG);
        addItem(stacks, ModItems.LIGHT_GRAY_SLEEPING_BAG);
        addItem(stacks, ModItems.CYAN_SLEEPING_BAG);
        addItem(stacks, ModItems.PURPLE_SLEEPING_BAG);
        addItem(stacks, ModItems.BLUE_SLEEPING_BAG);
        addItem(stacks, ModItems.BROWN_SLEEPING_BAG);
        addItem(stacks, ModItems.GREEN_SLEEPING_BAG);
        addItem(stacks, ModItems.RED_SLEEPING_BAG);
        addItem(stacks, ModItems.BLACK_SLEEPING_BAG);
    }

    public static void addItem(List<ItemStack> items, Item item)
    {
        items.add(new ItemStack(item));
    }

    public static void addBlock(List<ItemStack> items, Block block)
    {
        items.add(new ItemStack(block));
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier)
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt(Tiers.TIER, tier.getOrdinal());
        return stack;
    }
}