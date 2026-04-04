package com.tiviacz.travelersbackpack.common.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeUnlockAdvancementBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShapedBackpackRecipeBuilder implements RecipeBuilder {
    private final HolderGetter<Item> items;
    private final RecipeCategory category;
    private final ItemStackTemplate result;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final RecipeUnlockAdvancementBuilder advancementBuilder = new RecipeUnlockAdvancementBuilder();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;

    private ShapedBackpackRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        this(items, category, new ItemStackTemplate(result.asItem(), count));
    }

    private ShapedBackpackRecipeBuilder(HolderGetter<Item> p_365072_, RecipeCategory p_249996_, ItemStackTemplate result) {
        this.items = p_365072_;
        this.category = p_249996_;
        this.result = result;
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemLike item) {
        return shaped(items, category, item, 1);
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemLike item, int count) {
        return new ShapedBackpackRecipeBuilder(items, category, item, count);
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemStackTemplate result) {
        return new ShapedBackpackRecipeBuilder(items, category, result);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedBackpackRecipeBuilder define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(this.items.getOrThrow(tag)));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedBackpackRecipeBuilder define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedBackpackRecipeBuilder define(Character symbol, Ingredient ingredient) {
        if(this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if(symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredient);
            return this;
        }
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public ShapedBackpackRecipeBuilder pattern(String pattern) {
        if(!this.rows.isEmpty() && pattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    public ShapedBackpackRecipeBuilder unlockedBy(String p_126133_, Criterion<?> p_301126_) {
        this.criteria.put(p_126133_, p_301126_);
        return this;
    }

    public ShapedBackpackRecipeBuilder group(@Nullable String p_126146_) {
        this.group = p_126146_;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilder.getDefaultRecipeId(this.result);
    }

    public ShapedBackpackRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public void save(RecipeOutput output, ResourceKey<Recipe<?>> id) {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.key, this.rows);
        Advancement.Builder advancement$builder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        ShapedBackpackRecipe recipe = new ShapedBackpackRecipe(
                RecipeBuilder.createCraftingCommonInfo(this.showNotification),
                RecipeBuilder.createCraftingBookInfo(this.category, this.group),
                pattern,
                this.result
        );
        output.accept(id, recipe, advancement$builder.build(id.identifier().withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }
}
