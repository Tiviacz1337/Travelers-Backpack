package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeJsonFactory;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import me.shedaniel.cloth.api.datagen.v1.DataGeneratorHandler;
import me.shedaniel.cloth.api.datagen.v1.LootTableData;
import me.shedaniel.cloth.api.datagen.v1.RecipeData;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.registry.Registry;

import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ModRecipesProvider
{
    public static void generate()
    {
        DataGeneratorHandler handler = DataGeneratorHandler.create(Paths.get("../src/generated/resources"));
        RecipeData recipeData = handler.getRecipes();
        LootTableData lootTableData = handler.getLootTables();

        addRecipes(recipeData);
        ModBlockLootTables.addLootTables(lootTableData);

        handler.run();
    }

    public static void addRecipes(RecipeData data)
    {
        for(Item item : BACKPACKS)
        {
            BackpackUpgradeJsonFactory.create(Ingredient.ofStacks(Stream.of(createTieredStack(item, Tiers.LEATHER))), Ingredient.ofItems(ModItems.IRON_TIER_UPGRADE), item).criterion("has_iron_tier_upgrade", conditionsFromItem(ModItems.IRON_TIER_UPGRADE)).offerTo(data, Registry.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_iron");
            BackpackUpgradeJsonFactory.create(Ingredient.ofStacks(Stream.of(createTieredStack(item, Tiers.IRON))), Ingredient.ofItems(ModItems.GOLD_TIER_UPGRADE), item).criterion("has_gold_tier_upgrade", conditionsFromItem(ModItems.GOLD_TIER_UPGRADE)).offerTo(data, Registry.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_gold");
            BackpackUpgradeJsonFactory.create(Ingredient.ofStacks(Stream.of(createTieredStack(item, Tiers.GOLD))), Ingredient.ofItems(ModItems.DIAMOND_TIER_UPGRADE), item).criterion("has_diamond_tier_upgrade", conditionsFromItem(ModItems.DIAMOND_TIER_UPGRADE)).offerTo(data, Registry.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_diamond");
            BackpackUpgradeJsonFactory.create(Ingredient.ofStacks(Stream.of(createTieredStack(item, Tiers.DIAMOND))), Ingredient.ofItems(ModItems.NETHERITE_TIER_UPGRADE), item).criterion("has_netherite_tier_upgrade", conditionsFromItem(ModItems.NETHERITE_TIER_UPGRADE)).offerTo(data, Registry.ITEM.getKey(item.asItem()).get().getValue().getPath() + "_smithing_netherite");
        }

        offerSleepingBagDyeingRecipe(data, ModItems.BLACK_SLEEPING_BAG, Items.BLACK_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.BLUE_SLEEPING_BAG, Items.BLUE_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.BROWN_SLEEPING_BAG, Items.BROWN_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.CYAN_SLEEPING_BAG, Items.CYAN_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.GRAY_SLEEPING_BAG, Items.GRAY_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.GREEN_SLEEPING_BAG, Items.GREEN_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.LIGHT_BLUE_SLEEPING_BAG, Items.LIGHT_BLUE_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.LIGHT_GRAY_SLEEPING_BAG, Items.LIGHT_GRAY_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.LIME_SLEEPING_BAG, Items.LIME_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.MAGENTA_SLEEPING_BAG, Items.MAGENTA_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.ORANGE_SLEEPING_BAG, Items.ORANGE_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.PINK_SLEEPING_BAG, Items.PINK_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.PURPLE_SLEEPING_BAG, Items.PURPLE_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.RED_SLEEPING_BAG, Items.RED_DYE);
        offerSleepingBagDyeingRecipe(data, ModItems.YELLOW_SLEEPING_BAG, Items.YELLOW_DYE);
    }

    private static void offerSleepingBagDyeingRecipe(Consumer<RecipeJsonProvider> consumer, ItemConvertible itemConvertible, ItemConvertible itemConvertible2) {
        String string = Registry.ITEM.getId(itemConvertible.asItem()).getPath();
        ShapelessRecipeJsonFactory.create(itemConvertible).input(ModItems.WHITE_SLEEPING_BAG).input(itemConvertible2).group("dyed_sleeping_bag").criterion("has_sleeping_bag", conditionsFromItem(ModItems.WHITE_SLEEPING_BAG)).offerTo(consumer, string + "_from_white_sleeping_bag");
    }

    public static ItemStack createTieredStack(Item item, Tiers.Tier tier)
    {
        ItemStack stack = item.getDefaultStack();
        stack.getOrCreateTag().putInt(Tiers.TIER, tier.getOrdinal());
        return stack;
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItem(ItemConvertible item) {
        return conditionsFromItemPredicates(ItemPredicate.Builder.create().item(item).build());
    }

    private static InventoryChangedCriterion.Conditions conditionsFromItemPredicates(ItemPredicate... items) {
        return new InventoryChangedCriterion.Conditions(EntityPredicate.Extended.EMPTY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, NumberRange.IntRange.ANY, items);
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
