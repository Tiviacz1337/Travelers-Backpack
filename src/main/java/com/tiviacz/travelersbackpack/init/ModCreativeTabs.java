package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ModCreativeTabs {
    public static final ResourceKey<CreativeModeTab> TRAVELERS_BACKPACK = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(TravelersBackpack.MODID, "travelers_backpack"));

    public static void registerItemGroup() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TRAVELERS_BACKPACK, FabricItemGroup.builder()
                .icon(ModCreativeTabs::createTabStack)
                .title(Component.translatable("itemGroup.travelersbackpack")).build());
    }

    public static ItemStack createTabStack() {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        NbtHelper.set(stack, ModDataHelper.RENDER_INFO, RenderInfo.createCreativeTabInfo());
        return stack;
    }

    public static void addItemGroup() {
        ItemGroupEvents.modifyEntriesEvent(TRAVELERS_BACKPACK).register(entries ->
        {
            entries.accept(ModItems.BACKPACK_TANK);
            entries.accept(ModItems.HOSE_NOZZLE);
            entries.accept(ModItems.HOSE);

            //Tiers
            entries.accept(ModItems.BLANK_UPGRADE);
            entries.accept(ModItems.IRON_TIER_UPGRADE);
            entries.accept(ModItems.GOLD_TIER_UPGRADE);
            entries.accept(ModItems.DIAMOND_TIER_UPGRADE);
            entries.accept(ModItems.NETHERITE_TIER_UPGRADE);
            entries.accept(ModItems.TANKS_UPGRADE);
            entries.accept(ModItems.CRAFTING_UPGRADE);
            entries.accept(ModItems.FEEDING_UPGRADE);
            entries.accept(ModItems.PICKUP_UPGRADE);
            entries.accept(ModItems.MAGNET_UPGRADE);
            entries.accept(ModItems.VOID_UPGRADE);
            entries.accept(ModItems.JUKEBOX_UPGRADE);

            //Standard
            entries.accept(withTanks(ModBlocks.STANDARD_TRAVELERS_BACKPACK));
            entries.accept(createTieredBackpack(Tiers.IRON));
            entries.accept(createTieredBackpack(Tiers.GOLD));
            entries.accept(createTieredBackpack(Tiers.DIAMOND));
            entries.accept(createTieredBackpack(Tiers.NETHERITE));

            //Blocks
            entries.accept(withTanks(ModBlocks.NETHERITE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.DIAMOND_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.GOLD_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.EMERALD_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.IRON_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.LAPIS_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.REDSTONE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.COAL_TRAVELERS_BACKPACK));

            entries.accept(withTanks(ModBlocks.QUARTZ_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.BOOKSHELF_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.END_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.NETHER_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SANDSTONE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SNOW_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SPONGE_TRAVELERS_BACKPACK));

            entries.accept(withTanks(ModBlocks.CAKE_TRAVELERS_BACKPACK));

            entries.accept(withTanks(ModBlocks.CACTUS_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.HAY_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.MELON_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.PUMPKIN_TRAVELERS_BACKPACK));

            entries.accept(withTanks(ModBlocks.CREEPER_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.DRAGON_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.ENDERMAN_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.BLAZE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.GHAST_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.MAGMA_CUBE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SKELETON_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SPIDER_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.WITHER_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.WARDEN_TRAVELERS_BACKPACK));

            //Friendly Mobs
            entries.accept(withTanks(ModBlocks.BAT_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.BEE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.WOLF_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.FOX_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.OCELOT_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.HORSE_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.COW_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.PIG_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SHEEP_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.CHICKEN_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.SQUID_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.VILLAGER_TRAVELERS_BACKPACK));
            entries.accept(withTanks(ModBlocks.IRON_GOLEM_TRAVELERS_BACKPACK));

            entries.accept(ModItems.WHITE_SLEEPING_BAG);
            entries.accept(ModItems.ORANGE_SLEEPING_BAG);
            entries.accept(ModItems.MAGENTA_SLEEPING_BAG);
            entries.accept(ModItems.LIGHT_BLUE_SLEEPING_BAG);
            entries.accept(ModItems.YELLOW_SLEEPING_BAG);
            entries.accept(ModItems.LIME_SLEEPING_BAG);
            entries.accept(ModItems.PINK_SLEEPING_BAG);
            entries.accept(ModItems.GRAY_SLEEPING_BAG);
            entries.accept(ModItems.LIGHT_GRAY_SLEEPING_BAG);
            entries.accept(ModItems.CYAN_SLEEPING_BAG);
            entries.accept(ModItems.PURPLE_SLEEPING_BAG);
            entries.accept(ModItems.BLUE_SLEEPING_BAG);
            entries.accept(ModItems.BROWN_SLEEPING_BAG);
            entries.accept(ModItems.GREEN_SLEEPING_BAG);
            entries.accept(ModItems.RED_SLEEPING_BAG);
            entries.accept(ModItems.BLACK_SLEEPING_BAG);
        });
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier) {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        NbtHelper.set(stack, ModDataHelper.TIER, tier.getOrdinal());
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
        return stack;
    }

    public static ItemStack withTanks(Block block) {
        ItemStack stack = new ItemStack(block);
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
        return stack;
    }
}