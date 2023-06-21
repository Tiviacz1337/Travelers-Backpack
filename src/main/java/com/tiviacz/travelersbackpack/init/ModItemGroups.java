package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups
{
    public static ItemGroup TRAVELERS_BACKPACK;

    public static void registerItemGroup()
    {
        TRAVELERS_BACKPACK = FabricItemGroup.builder(new Identifier(TravelersBackpack.MODID, ""))
                .displayName(Text.translatable("itemGroup.travelersbackpack.group"))
                .icon(ModItemGroups::createIcon).build();
    }

    public static ItemStack createIcon()
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        stack.getOrCreateNbt().put("LeftTank", FluidVariant.of(Fluids.WATER).toNbt());
        stack.getOrCreateNbt().put("RightTank", FluidVariant.of(Fluids.LAVA).toNbt());
        stack.getOrCreateNbt().putLong("LeftTankAmount", Tiers.LEATHER.getTankCapacity());
        stack.getOrCreateNbt().putLong("RightTankAmount", Tiers.LEATHER.getTankCapacity());
        return stack;
    }

    public static void addItemGroup()
    {
        ItemGroupEvents.modifyEntriesEvent(TRAVELERS_BACKPACK).register(entries ->
        {
            entries.add(ModItems.BACKPACK_TANK);
            entries.add(ModItems.HOSE_NOZZLE);
            entries.add(ModItems.HOSE);

            //Upgrades
            entries.add(ModItems.BLANK_UPGRADE);
            entries.add(ModItems.IRON_TIER_UPGRADE);
            entries.add(ModItems.GOLD_TIER_UPGRADE);
            entries.add(ModItems.DIAMOND_TIER_UPGRADE);
            entries.add(ModItems.NETHERITE_TIER_UPGRADE);

            //Standard
            entries.add(ModBlocks.STANDARD_TRAVELERS_BACKPACK);
            entries.add(createTieredBackpack(Tiers.IRON));
            entries.add(createTieredBackpack(Tiers.GOLD));
            entries.add(createTieredBackpack(Tiers.DIAMOND));
            entries.add(createTieredBackpack(Tiers.NETHERITE));

            //Blocks
            entries.add(ModBlocks.NETHERITE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.DIAMOND_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.GOLD_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.EMERALD_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.IRON_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.LAPIS_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.REDSTONE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.COAL_TRAVELERS_BACKPACK);

            entries.add(ModBlocks.QUARTZ_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.END_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.NETHER_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SNOW_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SPONGE_TRAVELERS_BACKPACK);

            //Food
            entries.add(ModBlocks.CAKE_TRAVELERS_BACKPACK);

            //Plants
            entries.add(ModBlocks.CACTUS_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.HAY_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.MELON_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK);

            //Mobs
            entries.add(ModBlocks.CREEPER_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.DRAGON_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.BLAZE_TRAVELERS_BACKPACK);

            entries.add(ModBlocks.GHAST_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SKELETON_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SPIDER_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.WITHER_TRAVELERS_BACKPACK);

            //Friendly Mobs
            entries.add(ModBlocks.BAT_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.BEE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.WOLF_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.FOX_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.OCELOT_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.HORSE_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.COW_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.PIG_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SHEEP_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.CHICKEN_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.SQUID_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.VILLAGER_TRAVELERS_BACKPACK);
            entries.add(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK);

            entries.add(ModItems.WHITE_SLEEPING_BAG);
            entries.add(ModItems.ORANGE_SLEEPING_BAG);
            entries.add(ModItems.MAGENTA_SLEEPING_BAG);
            entries.add(ModItems.LIGHT_BLUE_SLEEPING_BAG);
            entries.add(ModItems.YELLOW_SLEEPING_BAG);
            entries.add(ModItems.LIME_SLEEPING_BAG);
            entries.add(ModItems.PINK_SLEEPING_BAG);
            entries.add(ModItems.GRAY_SLEEPING_BAG);
            entries.add(ModItems.LIGHT_GRAY_SLEEPING_BAG);
            entries.add(ModItems.CYAN_SLEEPING_BAG);
            entries.add(ModItems.PURPLE_SLEEPING_BAG);
            entries.add(ModItems.BLUE_SLEEPING_BAG);
            entries.add(ModItems.BROWN_SLEEPING_BAG);
            entries.add(ModItems.GREEN_SLEEPING_BAG);
            entries.add(ModItems.RED_SLEEPING_BAG);
            entries.add(ModItems.BLACK_SLEEPING_BAG);
        });
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier)
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putString(Tiers.TIER, tier.getName());
        return stack;
    }
}
