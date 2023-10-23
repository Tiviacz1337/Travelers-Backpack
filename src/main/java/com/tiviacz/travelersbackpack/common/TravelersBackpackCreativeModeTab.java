package com.tiviacz.travelersbackpack.common;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.RegistryObject;

public class TravelersBackpackCreativeModeTab extends CreativeModeTab
{
    public static final CreativeModeTab TAB_TRAVELERS_BACKPACK = new TravelersBackpackCreativeModeTab(CreativeModeTab.getGroupCountSafe(), TravelersBackpack.MODID);

    private TravelersBackpackCreativeModeTab(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon()
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        stack.getOrCreateTag().put("LeftTank", new FluidStack(Fluids.WATER, TravelersBackpackConfig.tanksCapacity == null ? 2000 : Tiers.LEATHER.getTankCapacity()).writeToNBT(new CompoundTag()));
        stack.getOrCreateTag().put("RightTank", new FluidStack(Fluids.LAVA, TravelersBackpackConfig.tanksCapacity == null ? 2000 : Tiers.LEATHER.getTankCapacity()).writeToNBT(new CompoundTag()));
        return stack;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void fillItemList(NonNullList<ItemStack> items)
    {
        addItem(items, ModItems.BACKPACK_TANK.get());
        addItem(items, ModItems.HOSE_NOZZLE.get());
        addItem(items, ModItems.HOSE.get());

        addItem(items, ModItems.BLANK_UPGRADE.get());
        addItem(items, ModItems.IRON_TIER_UPGRADE.get());
        addItem(items, ModItems.GOLD_TIER_UPGRADE.get());
        addItem(items, ModItems.DIAMOND_TIER_UPGRADE.get());
        addItem(items, ModItems.NETHERITE_TIER_UPGRADE.get());

        //Standard
        addBlock(items, ModBlocks.STANDARD_TRAVELERS_BACKPACK);
        items.add(createTieredBackpack(Tiers.IRON));
        items.add(createTieredBackpack(Tiers.GOLD));
        items.add(createTieredBackpack(Tiers.DIAMOND));
        items.add(createTieredBackpack(Tiers.NETHERITE));

        //Blocks
        addBlock(items, ModBlocks.NETHERITE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.DIAMOND_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.GOLD_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.EMERALD_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.IRON_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.LAPIS_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.REDSTONE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.COAL_TRAVELERS_BACKPACK);

        addBlock(items, ModBlocks.QUARTZ_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.END_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.NETHER_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SANDSTONE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SNOW_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SPONGE_TRAVELERS_BACKPACK);

        addBlock(items, ModBlocks.CAKE_TRAVELERS_BACKPACK);

        addBlock(items, ModBlocks.CACTUS_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.HAY_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.MELON_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.PUMPKIN_TRAVELERS_BACKPACK);

        addBlock(items, ModBlocks.CREEPER_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.DRAGON_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.ENDERMAN_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.BLAZE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.GHAST_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SKELETON_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SPIDER_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.WITHER_TRAVELERS_BACKPACK);

        //Friendly Mobs
        addBlock(items, ModBlocks.BAT_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.BEE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.WOLF_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.FOX_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.OCELOT_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.HORSE_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.COW_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.PIG_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SHEEP_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.CHICKEN_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.SQUID_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.VILLAGER_TRAVELERS_BACKPACK);
        addBlock(items, ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK);

        addItem(items, ModItems.WHITE_SLEEPING_BAG.get());
        addItem(items, ModItems.ORANGE_SLEEPING_BAG.get());
        addItem(items, ModItems.MAGENTA_SLEEPING_BAG.get());
        addItem(items, ModItems.LIGHT_BLUE_SLEEPING_BAG.get());
        addItem(items, ModItems.YELLOW_SLEEPING_BAG.get());
        addItem(items, ModItems.LIME_SLEEPING_BAG.get());
        addItem(items, ModItems.PINK_SLEEPING_BAG.get());
        addItem(items, ModItems.GRAY_SLEEPING_BAG.get());
        addItem(items, ModItems.LIGHT_GRAY_SLEEPING_BAG.get());
        addItem(items, ModItems.CYAN_SLEEPING_BAG.get());
        addItem(items, ModItems.PURPLE_SLEEPING_BAG.get());
        addItem(items, ModItems.BLUE_SLEEPING_BAG.get());
        addItem(items, ModItems.BROWN_SLEEPING_BAG.get());
        addItem(items, ModItems.GREEN_SLEEPING_BAG.get());
        addItem(items, ModItems.RED_SLEEPING_BAG.get());
        addItem(items, ModItems.BLACK_SLEEPING_BAG.get());
    }

    public void addItem(NonNullList<ItemStack> items, Item item)
    {
        items.add(new ItemStack(item));
    }

    public void addBlock(NonNullList<ItemStack> items, RegistryObject<Block> block)
    {
        items.add(new ItemStack(block.get()));
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier)
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(Tiers.TIER, tier.getOrdinal());
        return stack;
    }
}