package com.tiviacz.travelersbackpackneo.initold;

import com.tiviacz.travelersbackpackneo.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TravelersBackpack.MODID);
    public static DeferredHolder<CreativeModeTab, CreativeModeTab> TRAVELERS_BACKPACK = CREATIVE_MODE_TABS.register("travelersbackpack", () -> CreativeModeTab.builder()
            .icon(ModCreativeTabs::createTabStack)
            .title(Component.translatable("itemGroup.travelersbackpack")).displayItems(ModCreativeTabs::displayItems).build());

    public static ItemStack createTabStack() {
        ItemStack stack = new ItemStack(ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get());
        stack.set(ModDataComponents.RENDER_INFO.get(), RenderInfo.createCreativeTabInfo());
        return stack;
    }

    public static void displayItems(CreativeModeTab.ItemDisplayParameters displayParameters, CreativeModeTab.Output output) {
        output.accept(ModItemsNeo.BACKPACK_TANK.get());
        output.accept(ModItemsNeo.HOSE_NOZZLE.get());
        output.accept(ModItemsNeo.HOSE.get());

        //Tiers
        output.accept(ModItemsNeo.BLANK_UPGRADE.get());
        output.accept(ModItemsNeo.IRON_TIER_UPGRADE.get());
        output.accept(ModItemsNeo.GOLD_TIER_UPGRADE.get());
        output.accept(ModItemsNeo.DIAMOND_TIER_UPGRADE.get());
        output.accept(ModItemsNeo.NETHERITE_TIER_UPGRADE.get());
        output.accept(ModItemsNeo.TANKS_UPGRADE.get());
        output.accept(ModItemsNeo.CRAFTING_UPGRADE.get());
        output.accept(ModItemsNeo.FEEDING_UPGRADE.get());
        output.accept(ModItemsNeo.PICKUP_UPGRADE.get());
        output.accept(ModItemsNeo.MAGNET_UPGRADE.get());
        output.accept(ModItemsNeo.VOID_UPGRADE.get());
        output.accept(ModItemsNeo.JUKEBOX_UPGRADE.get());

        //Standard
        output.accept(withTanks(ModBlocks.STANDARD_TRAVELERS_BACKPACK));
        output.accept(createTieredBackpack(Tiers.IRON));
        output.accept(createTieredBackpack(Tiers.GOLD));
        output.accept(createTieredBackpack(Tiers.DIAMOND));
        output.accept(createTieredBackpack(Tiers.NETHERITE));

        //Blocks
        output.accept(withTanks(ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.GOLD_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.EMERALD_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.IRON_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.LAPIS_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.COAL_TRAVELERS_BACKPACK));

        output.accept(withTanks(ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.END_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.NETHER_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SNOW_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SPONGE_TRAVELERS_BACKPACK));

        output.accept(withTanks(ModBlocks.CAKE_TRAVELERS_BACKPACK));

        output.accept(withTanks(ModBlocks.CACTUS_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.HAY_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.MELON_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

        output.accept(withTanks(ModBlocks.CREEPER_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.DRAGON_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.BLAZE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.GHAST_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SKELETON_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SPIDER_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.WITHER_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.WARDEN_TRAVELERS_BACKPACK));

        //Friendly Mobs
        output.accept(withTanks(ModBlocks.BAT_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.BEE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.WOLF_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.FOX_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.OCELOT_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.HORSE_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.COW_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.PIG_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SHEEP_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.SQUID_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.VILLAGER_TRAVELERS_BACKPACK));
        output.accept(withTanks(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK));

        output.accept(ModItemsNeo.WHITE_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.ORANGE_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.MAGENTA_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.LIGHT_BLUE_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.YELLOW_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.LIME_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.PINK_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.GRAY_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.LIGHT_GRAY_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.CYAN_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.PURPLE_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.BLUE_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.BROWN_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.GREEN_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.RED_SLEEPING_BAG.get());
        output.accept(ModItemsNeo.BLACK_SLEEPING_BAG.get());
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier) {
        ItemStack stack = new ItemStack(ModItemsNeo.STANDARD_TRAVELERS_BACKPACK.get());
        stack.set(ModDataComponents.TIER, tier.getOrdinal());
        stack.set(ModDataComponents.STARTER_UPGRADES, List.of(ModItemsNeo.TANKS_UPGRADE.toStack()));
        return stack;
    }

    public static ItemStack withTanks(DeferredBlock<TravelersBackpackBlock> deferredBlock) {
        ItemStack stack = new ItemStack(deferredBlock.get());
        stack.set(ModDataComponents.STARTER_UPGRADES, List.of(ModItemsNeo.TANKS_UPGRADE.toStack()));
        return stack;
    }
}