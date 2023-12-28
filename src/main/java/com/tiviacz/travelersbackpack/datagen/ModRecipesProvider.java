package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeJsonBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;

import java.util.List;

public class ModRecipesProvider extends FabricRecipeProvider
{
    public ModRecipesProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter)
    {
        for(Item item : BACKPACKS)
        {
            BackpackUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(Items.LEATHER), Ingredient.ofStacks(createTieredStack(item, Tiers.LEATHER)), Ingredient.ofItems(ModItems.IRON_TIER_UPGRADE), RecipeCategory.TOOLS, item).criterion("has_iron_tier_upgrade", conditionsFromItem(ModItems.IRON_TIER_UPGRADE)).offerTo(exporter, Registries.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_iron");
            BackpackUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(Items.LEATHER), Ingredient.ofStacks(createTieredStack(item, Tiers.IRON)), Ingredient.ofItems(ModItems.GOLD_TIER_UPGRADE), RecipeCategory.TOOLS, item).criterion("has_gold_tier_upgrade", conditionsFromItem(ModItems.GOLD_TIER_UPGRADE)).offerTo(exporter, Registries.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_gold");
            BackpackUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(Items.LEATHER), Ingredient.ofStacks(createTieredStack(item, Tiers.GOLD)), Ingredient.ofItems(ModItems.DIAMOND_TIER_UPGRADE), RecipeCategory.TOOLS, item).criterion("has_diamond_tier_upgrade", conditionsFromItem(ModItems.DIAMOND_TIER_UPGRADE)).offerTo(exporter, Registries.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_diamond");
            BackpackUpgradeRecipeJsonBuilder.create(Ingredient.ofItems(Items.LEATHER), Ingredient.ofStacks(createTieredStack(item, Tiers.DIAMOND)), Ingredient.ofItems(ModItems.NETHERITE_TIER_UPGRADE), RecipeCategory.TOOLS, item).criterion("has_netherite_tier_upgrade", conditionsFromItem(ModItems.NETHERITE_TIER_UPGRADE)).offerTo(exporter, Registries.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_netherite");
        }

        List<Item> list = List.of(Items.BLACK_DYE, Items.BLUE_DYE, Items.BROWN_DYE, Items.CYAN_DYE, Items.GRAY_DYE, Items.GREEN_DYE, Items.LIGHT_BLUE_DYE, Items.LIGHT_GRAY_DYE, Items.LIME_DYE, Items.MAGENTA_DYE, Items.ORANGE_DYE, Items.PINK_DYE, Items.PURPLE_DYE, Items.RED_DYE, Items.YELLOW_DYE, Items.WHITE_DYE);
        List<Item> list2 = List.of(ModItems.BLACK_SLEEPING_BAG, ModItems.BLUE_SLEEPING_BAG, ModItems.BROWN_SLEEPING_BAG, ModItems.CYAN_SLEEPING_BAG, ModItems.GRAY_SLEEPING_BAG, ModItems.GREEN_SLEEPING_BAG, ModItems.LIGHT_BLUE_SLEEPING_BAG, ModItems.LIGHT_GRAY_SLEEPING_BAG, ModItems.LIME_SLEEPING_BAG, ModItems.MAGENTA_SLEEPING_BAG, ModItems.ORANGE_SLEEPING_BAG, ModItems.PINK_SLEEPING_BAG, ModItems.PURPLE_SLEEPING_BAG, ModItems.RED_SLEEPING_BAG, ModItems.YELLOW_SLEEPING_BAG, ModItems.WHITE_SLEEPING_BAG);
        offerDyeableRecipes(exporter, list, list2, "sleeping_bag");
    }

    public static ItemStack createTieredStack(Item item, Tiers.Tier tier)
    {
        ItemStack stack = item.getDefaultStack();
        stack.getOrCreateNbt().putInt(Tiers.TIER, tier.getOrdinal());
        return stack;
    }

    public static final Item[] BACKPACKS = {
            ModItems.STANDARD_TRAVELERS_BACKPACK,
            ModItems.NETHERITE_TRAVELERS_BACKPACK,
            ModItems.DIAMOND_TRAVELERS_BACKPACK,
            ModItems.GOLD_TRAVELERS_BACKPACK,
            ModItems.EMERALD_TRAVELERS_BACKPACK,
            ModItems.IRON_TRAVELERS_BACKPACK,
            ModItems.LAPIS_TRAVELERS_BACKPACK,
            ModItems.REDSTONE_TRAVELERS_BACKPACK,
            ModItems.COAL_TRAVELERS_BACKPACK,

            ModItems.QUARTZ_TRAVELERS_BACKPACK,
            ModItems.BOOKSHELF_TRAVELERS_BACKPACK,
            ModItems.END_TRAVELERS_BACKPACK,
            ModItems.NETHER_TRAVELERS_BACKPACK,
            ModItems.SANDSTONE_TRAVELERS_BACKPACK,
            ModItems.SNOW_TRAVELERS_BACKPACK,
            ModItems.SPONGE_TRAVELERS_BACKPACK,

            ModItems.CAKE_TRAVELERS_BACKPACK,

            ModItems.CACTUS_TRAVELERS_BACKPACK,
            ModItems.HAY_TRAVELERS_BACKPACK,
            ModItems.MELON_TRAVELERS_BACKPACK,
            ModItems.PUMPKIN_TRAVELERS_BACKPACK,

            ModItems.CREEPER_TRAVELERS_BACKPACK,
            ModItems.DRAGON_TRAVELERS_BACKPACK,
            ModItems.ENDERMAN_TRAVELERS_BACKPACK,
            ModItems.BLAZE_TRAVELERS_BACKPACK,
            ModItems.GHAST_TRAVELERS_BACKPACK,
            ModItems.MAGMA_CUBE_TRAVELERS_BACKPACK,
            ModItems.SKELETON_TRAVELERS_BACKPACK,
            ModItems.SPIDER_TRAVELERS_BACKPACK,
            ModItems.WITHER_TRAVELERS_BACKPACK,

            ModItems.BAT_TRAVELERS_BACKPACK,
            ModItems.BEE_TRAVELERS_BACKPACK,
            ModItems.WOLF_TRAVELERS_BACKPACK,
            ModItems.FOX_TRAVELERS_BACKPACK,
            ModItems.OCELOT_TRAVELERS_BACKPACK,
            ModItems.HORSE_TRAVELERS_BACKPACK,
            ModItems.COW_TRAVELERS_BACKPACK,
            ModItems.PIG_TRAVELERS_BACKPACK,
            ModItems.SHEEP_TRAVELERS_BACKPACK,
            ModItems.CHICKEN_TRAVELERS_BACKPACK,
            ModItems.SQUID_TRAVELERS_BACKPACK,
            ModItems.VILLAGER_TRAVELERS_BACKPACK,
            ModItems.IRON_GOLEM_TRAVELERS_BACKPACK,
    };
}