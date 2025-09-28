package com.tiviacz.travelersbackpack.init;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.blocks.TravelersBackpackBlock;
import com.tiviacz.travelersbackpack.components.RenderInfo;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import com.tiviacz.travelersbackpack.util.NbtHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TravelersBackpack.MODID);
    public static RegistryObject<CreativeModeTab> TRAVELERS_BACKPACK = CREATIVE_MODE_TABS.register("travelersbackpack", () -> CreativeModeTab.builder()
            .icon(ModCreativeTabs::createTabStack)
            .title(Component.translatable("itemGroup.travelersbackpack")).displayItems(ModCreativeTabs::displayItems).build());

    public static ItemStack createTabStack() {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        NbtHelper.set(stack, ModDataHelper.RENDER_INFO, RenderInfo.createCreativeTabInfo());
        return stack;
    }

    public static void displayItems(CreativeModeTab.ItemDisplayParameters displayParameters, CreativeModeTab.Output output) {
        output.accept(ModItems.BACKPACK_TANK.get());
        output.accept(ModItems.HOSE_NOZZLE.get());
        output.accept(ModItems.HOSE.get());

        //Tiers
        output.accept(ModItems.BLANK_UPGRADE.get());
        output.accept(ModItems.IRON_TIER_UPGRADE.get());
        output.accept(ModItems.GOLD_TIER_UPGRADE.get());
        output.accept(ModItems.DIAMOND_TIER_UPGRADE.get());
        output.accept(ModItems.NETHERITE_TIER_UPGRADE.get());
        output.accept(ModItems.TANKS_UPGRADE.get());
        output.accept(ModItems.CRAFTING_UPGRADE.get());
        output.accept(ModItems.FURNACE_UPGRADE.get());
        output.accept(ModItems.SMOKER_UPGRADE.get());
        output.accept(ModItems.BLAST_FURNACE_UPGRADE.get());
        output.accept(ModItems.FEEDING_UPGRADE.get());
        output.accept(ModItems.REFILL_UPGRADE.get());
        output.accept(ModItems.PICKUP_UPGRADE.get());
        output.accept(ModItems.MAGNET_UPGRADE.get());
        output.accept(ModItems.VOID_UPGRADE.get());
        output.accept(ModItems.JUKEBOX_UPGRADE.get());

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

    public static ItemStack createTieredBackpack(Tiers.Tier tier) {
        ItemStack stack = new ItemStack(ModItems.STANDARD_TRAVELERS_BACKPACK.get());
        NbtHelper.set(stack, ModDataHelper.TIER, tier.getOrdinal());
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
        return stack;
    }

    public static ItemStack withTanks(RegistryObject<TravelersBackpackBlock> deferredBlock) {
        ItemStack stack = new ItemStack(deferredBlock.get());
        NbtHelper.set(stack, ModDataHelper.STARTER_UPGRADES, List.of(ModItems.TANKS_UPGRADE.get().getDefaultInstance()));
        return stack;
    }
}