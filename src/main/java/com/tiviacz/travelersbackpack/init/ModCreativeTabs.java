package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.config.TravelersBackpackConfig;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TravelersBackpack.MODID);

    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TRAVELERS_BACKPACK = CREATIVE_MODE_TABS.register("travelersbackpack", () -> CreativeModeTab.builder()
            .icon(ModCreativeTabs::createTabStack)
            .title(Component.translatable("itemGroup.travelersbackpack")).displayItems(ModCreativeTabs::displayItems).build());

    public static ItemStack createTabStack()
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        stack.getOrCreateTag().put("LeftTank", new FluidStack(Fluids.WATER, TravelersBackpackConfig.tanksCapacity == null ? 2000 : Tiers.LEATHER.getTankCapacity()).writeToNBT(new CompoundTag()));
        stack.getOrCreateTag().put("RightTank", new FluidStack(Fluids.LAVA, TravelersBackpackConfig.tanksCapacity == null ? 2000 : Tiers.LEATHER.getTankCapacity()).writeToNBT(new CompoundTag()));
        return stack;
    }

    public static void displayItems(CreativeModeTab.ItemDisplayParameters displayParameters, CreativeModeTab.Output output)
    {
        output.accept(ModItems.BACKPACK_TANK.get());
        output.accept(ModItems.HOSE_NOZZLE.get());
        output.accept(ModItems.HOSE.get());

        //Tiers
        output.accept(ModItems.BLANK_UPGRADE.get());
        output.accept(ModItems.IRON_TIER_UPGRADE.get());
        output.accept(ModItems.GOLD_TIER_UPGRADE.get());
        output.accept(ModItems.DIAMOND_TIER_UPGRADE.get());
        output.accept(ModItems.NETHERITE_TIER_UPGRADE.get());

        //Standard
        output.accept(ModBlocks.STANDARD_TRAVELERS_BACKPACK.get());
        output.accept(createTieredBackpack(Tiers.IRON));
        output.accept(createTieredBackpack(Tiers.GOLD));
        output.accept(createTieredBackpack(Tiers.DIAMOND));
        output.accept(createTieredBackpack(Tiers.NETHERITE));

        //Blocks
        output.accept(ModBlocks.NETHERITE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.DIAMOND_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.GOLD_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.EMERALD_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.IRON_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.LAPIS_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.REDSTONE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.COAL_TRAVELERS_BACKPACK.get());

        output.accept(ModBlocks.QUARTZ_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.END_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.NETHER_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SNOW_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SPONGE_TRAVELERS_BACKPACK.get());

        output.accept(ModBlocks.CAKE_TRAVELERS_BACKPACK.get());

        output.accept(ModBlocks.CACTUS_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.HAY_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.MELON_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK.get());

        output.accept(ModBlocks.CREEPER_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.DRAGON_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.BLAZE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.GHAST_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SKELETON_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SPIDER_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.WITHER_TRAVELERS_BACKPACK.get());

        //Friendly Mobs
        output.accept(ModBlocks.BAT_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.BEE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.WOLF_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.FOX_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.OCELOT_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.HORSE_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.COW_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.PIG_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SHEEP_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.CHICKEN_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.SQUID_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.VILLAGER_TRAVELERS_BACKPACK.get());
        output.accept(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK.get());

        output.accept(ModItems.WHITE_SLEEPING_BAG.get());
        output.accept(ModItems.ORANGE_SLEEPING_BAG.get());
        output.accept(ModItems.MAGENTA_SLEEPING_BAG.get());
        output.accept(ModItems.LIGHT_BLUE_SLEEPING_BAG.get());
        output.accept(ModItems.YELLOW_SLEEPING_BAG.get());
        output.accept(ModItems.LIME_SLEEPING_BAG.get());
        output.accept(ModItems.PINK_SLEEPING_BAG.get());
        output.accept(ModItems.GRAY_SLEEPING_BAG.get());
        output.accept(ModItems.LIGHT_GRAY_SLEEPING_BAG.get());
        output.accept(ModItems.CYAN_SLEEPING_BAG.get());
        output.accept(ModItems.PURPLE_SLEEPING_BAG.get());
        output.accept(ModItems.BLUE_SLEEPING_BAG.get());
        output.accept(ModItems.BROWN_SLEEPING_BAG.get());
        output.accept(ModItems.GREEN_SLEEPING_BAG.get());
        output.accept(ModItems.RED_SLEEPING_BAG.get());
        output.accept(ModItems.BLACK_SLEEPING_BAG.get());
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier)
    {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(Tiers.TIER, tier.getOrdinal());
        return stack;
    }
}