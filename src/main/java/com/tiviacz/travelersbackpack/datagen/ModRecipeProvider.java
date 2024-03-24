package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.ITravelersBackpackContainer;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider
{
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer)
    {
        //New smithing
        for(Item item : BACKPACKS)
        {
            //Tiers
            BackpackUpgradeRecipeBuilder.m_266555_(Ingredient.of(Items.LEATHER), Ingredient.of(createTieredStack(item, Tiers.LEATHER)), Ingredient.of(ModItems.IRON_TIER_UPGRADE.get()), RecipeCategory.TOOLS, item).m_266439_("has_iron_tier_upgrade", RecipeProvider.has(ModItems.IRON_TIER_UPGRADE.get())).m_266260_(writer, getItemName(item) + "_smithing_iron");
            BackpackUpgradeRecipeBuilder.m_266555_(Ingredient.of(Items.LEATHER), Ingredient.of(createTieredStack(item, Tiers.IRON)), Ingredient.of(ModItems.GOLD_TIER_UPGRADE.get()), RecipeCategory.TOOLS, item).m_266439_("has_gold_tier_upgrade", RecipeProvider.has(ModItems.GOLD_TIER_UPGRADE.get())).m_266260_(writer, getItemName(item) + "_smithing_gold");
            BackpackUpgradeRecipeBuilder.m_266555_(Ingredient.of(Items.LEATHER), Ingredient.of(createTieredStack(item, Tiers.GOLD)), Ingredient.of(ModItems.DIAMOND_TIER_UPGRADE.get()), RecipeCategory.TOOLS, item).m_266439_("has_diamond_tier_upgrade", RecipeProvider.has(ModItems.DIAMOND_TIER_UPGRADE.get())).m_266260_(writer, getItemName(item) + "_smithing_diamond");
            BackpackUpgradeRecipeBuilder.m_266555_(Ingredient.of(Items.LEATHER), Ingredient.of(createTieredStack(item, Tiers.DIAMOND)), Ingredient.of(ModItems.NETHERITE_TIER_UPGRADE.get()), RecipeCategory.TOOLS, item).m_266439_("has_netherite_tier_upgrade", RecipeProvider.has(ModItems.NETHERITE_TIER_UPGRADE.get())).m_266260_(writer, getItemName(item) + "_smithing_netherite");

            //Crafting upgrade
            BackpackUpgradeRecipeBuilder.m_266555_(Ingredient.of(Items.LEATHER), Ingredient.of(item), Ingredient.of(ModItems.CRAFTING_UPGRADE.get()), RecipeCategory.TOOLS, item).m_266439_("has_crafting_upgrade", RecipeProvider.has(ModItems.CRAFTING_UPGRADE.get())).m_266260_(writer, getItemName(item) + "_smithing_crafting");
        }

        List<Item> list = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);
        List<Item> list2 = List.of(ModItems.BLACK_SLEEPING_BAG.get(), ModItems.BLUE_SLEEPING_BAG.get(), ModItems.BROWN_SLEEPING_BAG.get(), ModItems.CYAN_SLEEPING_BAG.get(), ModItems.GRAY_SLEEPING_BAG.get(), ModItems.GREEN_SLEEPING_BAG.get(), ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), ModItems.LIME_SLEEPING_BAG.get(), ModItems.MAGENTA_SLEEPING_BAG.get(), ModItems.ORANGE_SLEEPING_BAG.get(), ModItems.PINK_SLEEPING_BAG.get(), ModItems.PURPLE_SLEEPING_BAG.get(), ModItems.RED_SLEEPING_BAG.get(), ModItems.YELLOW_SLEEPING_BAG.get(), ModItems.WHITE_SLEEPING_BAG.get());
        colorBlockWithDye(writer, list, list2, "sleeping_bag");
    }

    public ItemStack createTieredStack(Item item, Tiers.Tier tier) {
        ItemStack stack = item.getDefaultInstance();
        stack.getOrCreateTag().putInt(ITravelersBackpackContainer.TIER, tier.getOrdinal());
        return stack;
    }

    public static final Item[] BACKPACKS = {
            ModItems.STANDARD_TRAVELERS_BACKPACK.get(),
            ModItems.NETHERITE_TRAVELERS_BACKPACK.get(),
            ModItems.DIAMOND_TRAVELERS_BACKPACK.get(),
            ModItems.GOLD_TRAVELERS_BACKPACK.get(),
            ModItems.EMERALD_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_TRAVELERS_BACKPACK.get(),
            ModItems.LAPIS_TRAVELERS_BACKPACK.get(),
            ModItems.REDSTONE_TRAVELERS_BACKPACK.get(),
            ModItems.COAL_TRAVELERS_BACKPACK.get(),

            ModItems.QUARTZ_TRAVELERS_BACKPACK.get(),
            ModItems.BOOKSHELF_TRAVELERS_BACKPACK.get(),
            ModItems.END_TRAVELERS_BACKPACK.get(),
            ModItems.NETHER_TRAVELERS_BACKPACK.get(),
            ModItems.SANDSTONE_TRAVELERS_BACKPACK.get(),
            ModItems.SNOW_TRAVELERS_BACKPACK.get(),
            ModItems.SPONGE_TRAVELERS_BACKPACK.get(),

            ModItems.CAKE_TRAVELERS_BACKPACK.get(),

            ModItems.CACTUS_TRAVELERS_BACKPACK.get(),
            ModItems.HAY_TRAVELERS_BACKPACK.get(),
            ModItems.MELON_TRAVELERS_BACKPACK.get(),
            ModItems.PUMPKIN_TRAVELERS_BACKPACK.get(),

            ModItems.CREEPER_TRAVELERS_BACKPACK.get(),
            ModItems.DRAGON_TRAVELERS_BACKPACK.get(),
            ModItems.ENDERMAN_TRAVELERS_BACKPACK.get(),
            ModItems.BLAZE_TRAVELERS_BACKPACK.get(),
            ModItems.GHAST_TRAVELERS_BACKPACK.get(),
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK.get(),
            ModItems.SKELETON_TRAVELERS_BACKPACK.get(),
            ModItems.SPIDER_TRAVELERS_BACKPACK.get(),
            ModItems.WITHER_TRAVELERS_BACKPACK.get(),

            ModItems.BAT_TRAVELERS_BACKPACK.get(),
            ModItems.BEE_TRAVELERS_BACKPACK.get(),
            ModItems.WOLF_TRAVELERS_BACKPACK.get(),
            ModItems.FOX_TRAVELERS_BACKPACK.get(),
            ModItems.OCELOT_TRAVELERS_BACKPACK.get(),
            ModItems.HORSE_TRAVELERS_BACKPACK.get(),
            ModItems.COW_TRAVELERS_BACKPACK.get(),
            ModItems.PIG_TRAVELERS_BACKPACK.get(),
            ModItems.SHEEP_TRAVELERS_BACKPACK.get(),
            ModItems.CHICKEN_TRAVELERS_BACKPACK.get(),
            ModItems.SQUID_TRAVELERS_BACKPACK.get(),
            ModItems.VILLAGER_TRAVELERS_BACKPACK.get(),
            ModItems.IRON_GOLEM_TRAVELERS_BACKPACK.get(),
    };
}