package com.tiviacz.travelersbackpack.datagen;

import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipeBuilder;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.inventory.Tiers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider
{
    public ModRecipeProvider(DataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> writer)
    {
        for(Item item : BACKPACKS)
        {
            BackpackUpgradeRecipeBuilder.upgrade(Ingredient.of(createTieredStack(item, Tiers.LEATHER)), Ingredient.of(ModItems.IRON_TIER_UPGRADE.get()), item).unlocks("has_iron_tier_upgrade", RecipeProvider.has(ModItems.IRON_TIER_UPGRADE.get())).save(writer, getItemName(item) + "_smithing_iron");
            BackpackUpgradeRecipeBuilder.upgrade(Ingredient.of(createTieredStack(item, Tiers.IRON)), Ingredient.of(ModItems.GOLD_TIER_UPGRADE.get()), item).unlocks("has_gold_tier_upgrade", RecipeProvider.has(ModItems.GOLD_TIER_UPGRADE.get())).save(writer, getItemName(item) + "_smithing_gold");
            BackpackUpgradeRecipeBuilder.upgrade(Ingredient.of(createTieredStack(item, Tiers.GOLD)), Ingredient.of(ModItems.DIAMOND_TIER_UPGRADE.get()), item).unlocks("has_diamond_tier_upgrade", RecipeProvider.has(ModItems.DIAMOND_TIER_UPGRADE.get())).save(writer, getItemName(item) + "_smithing_diamond");
            BackpackUpgradeRecipeBuilder.upgrade(Ingredient.of(createTieredStack(item, Tiers.DIAMOND)), Ingredient.of(ModItems.NETHERITE_TIER_UPGRADE.get()), item).unlocks("has_netherite_tier_upgrade", RecipeProvider.has(ModItems.NETHERITE_TIER_UPGRADE.get())).save(writer, getItemName(item) + "_smithing_netherite");
        }

        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BLACK_SLEEPING_BAG.get(), Items.BLACK_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BLUE_SLEEPING_BAG.get(), Items.BLUE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.BROWN_SLEEPING_BAG.get(), Items.BROWN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.CYAN_SLEEPING_BAG.get(), Items.CYAN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.GRAY_SLEEPING_BAG.get(), Items.GRAY_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.GREEN_SLEEPING_BAG.get(), Items.GREEN_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIGHT_BLUE_SLEEPING_BAG.get(), Items.LIGHT_BLUE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIGHT_GRAY_SLEEPING_BAG.get(), Items.LIGHT_GRAY_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.LIME_SLEEPING_BAG.get(), Items.LIME_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.MAGENTA_SLEEPING_BAG.get(), Items.MAGENTA_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.ORANGE_SLEEPING_BAG.get(), Items.ORANGE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.PINK_SLEEPING_BAG.get(), Items.PINK_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.PURPLE_SLEEPING_BAG.get(), Items.PURPLE_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.RED_SLEEPING_BAG.get(), Items.RED_DYE);
        sleepingBagFromWhiteSleepingBagAndDye(writer, ModItems.YELLOW_SLEEPING_BAG.get(), Items.YELLOW_DYE);
    }

    protected static void sleepingBagFromWhiteSleepingBagAndDye(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pDyedBed, ItemLike pDye)
    {
        ShapelessRecipeBuilder.shapeless(pDyedBed).requires(ModItems.WHITE_SLEEPING_BAG.get()).requires(pDye).group("dyed_sleeping_bag").unlockedBy("has_sleeping_bag", has(ModItems.WHITE_SLEEPING_BAG.get())).save(pFinishedRecipeConsumer, getConversionRecipeName(pDyedBed, ModItems.WHITE_SLEEPING_BAG.get()));
    }

    public ItemStack createTieredStack(Item item, Tiers.Tier tier)
    {
        ItemStack stack = item.getDefaultInstance();
        stack.getOrCreateTag().putInt(Tiers.TIER, tier.getOrdinal());
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