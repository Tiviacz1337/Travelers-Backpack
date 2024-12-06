package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.init.ModBlocks;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
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

public class ModItemGroups
{
    public static final ResourceKey<CreativeModeTab> TRAVELERS_BACKPACK = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "travelers_backpack"));

    public static void registerItemGroup()
    {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TRAVELERS_BACKPACK, FabricItemGroup.builder()
                .icon(ModItemGroups::createTabStack)
                .title(Component.translatable("itemGroup.travelersbackpack")).build());
    }

    public static ItemStack createTabStack() {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        stack.set(ModDataComponents.RENDER_INFO, RenderInfo.createCreativeTabInfo());
        return stack;
    }

    public static void addItemGroup()
    {
        ItemGroupEvents.modifyEntriesEvent(TRAVELERS_BACKPACK).register(output ->
        {
            output.accept(ModItems.BACKPACK_TANK);
            output.accept(ModItems.HOSE_NOZZLE);
            output.accept(ModItems.HOSE);

            //Tiers
            output.accept(ModItems.BLANK_UPGRADE);
            output.accept(ModItems.IRON_TIER_UPGRADE);
            output.accept(ModItems.GOLD_TIER_UPGRADE);
            output.accept(ModItems.DIAMOND_TIER_UPGRADE);
            output.accept(ModItems.NETHERITE_TIER_UPGRADE);
            output.accept(ModItems.TANKS_UPGRADE);
            output.accept(ModItems.CRAFTING_UPGRADE);
            output.accept(ModItems.FEEDING_UPGRADE);
            output.accept(ModItems.PICKUP_UPGRADE);
            output.accept(ModItems.MAGNET_UPGRADE);
            output.accept(ModItems.VOID_UPGRADE);
            output.accept(ModItems.JUKEBOX_UPGRADE);

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

            output.accept(ModItems.WHITE_SLEEPING_BAG);
            output.accept(ModItems.ORANGE_SLEEPING_BAG);
            output.accept(ModItems.MAGENTA_SLEEPING_BAG);
            output.accept(ModItems.LIGHT_BLUE_SLEEPING_BAG);
            output.accept(ModItems.YELLOW_SLEEPING_BAG);
            output.accept(ModItems.LIME_SLEEPING_BAG);
            output.accept(ModItems.PINK_SLEEPING_BAG);
            output.accept(ModItems.GRAY_SLEEPING_BAG);
            output.accept(ModItems.LIGHT_GRAY_SLEEPING_BAG);
            output.accept(ModItems.CYAN_SLEEPING_BAG);
            output.accept(ModItems.PURPLE_SLEEPING_BAG);
            output.accept(ModItems.BLUE_SLEEPING_BAG);
            output.accept(ModItems.BROWN_SLEEPING_BAG);
            output.accept(ModItems.GREEN_SLEEPING_BAG);
            output.accept(ModItems.RED_SLEEPING_BAG);
            output.accept(ModItems.BLACK_SLEEPING_BAG);
        });
    }

    public static ItemStack createTieredBackpack(Tiers.Tier tier) {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK);
        stack.set(ModDataComponents.TIER, tier.getOrdinal());
        stack.set(ModDataComponents.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
        return stack;
    }

    public static ItemStack withTanks(Block block) {
        ItemStack stack = new ItemStack(block);
        stack.set(ModDataComponents.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.getDefaultInstance()));
        return stack;
    }
}
