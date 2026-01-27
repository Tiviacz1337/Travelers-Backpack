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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShapedBackpackRecipeBuilder implements RecipeBuilder {
    private final HolderGetter<Item> items;
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final ItemStack resultStack; // Neo: add stack result support
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private boolean showNotification = true;

    private ShapedBackpackRecipeBuilder(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        this(items, category, new ItemStack(result, count));
    }

    private ShapedBackpackRecipeBuilder(HolderGetter<Item> p_365072_, RecipeCategory p_249996_, ItemStack result) {
        this.items = p_365072_;
        this.category = p_249996_;
        this.result = result.getItem();
        this.count = result.getCount();
        this.resultStack = result;
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemLike result) {
        return shaped(items, category, result, 1);
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> items, RecipeCategory category, ItemLike result, int count) {
        return new ShapedBackpackRecipeBuilder(items, category, result, count);
    }

    public static ShapedBackpackRecipeBuilder shaped(HolderGetter<Item> p_365019_, RecipeCategory p_251325_, ItemStack result) {
        return new ShapedBackpackRecipeBuilder(p_365019_, p_251325_, result);
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

    public ShapedBackpackRecipeBuilder showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput p_301098_, ResourceKey<Recipe<?>> p_380072_) {
        ShapedRecipePattern shapedrecipepattern = this.ensureValid(p_380072_);
        Advancement.Builder advancement$builder = p_301098_.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(p_380072_))
                .rewards(AdvancementRewards.Builder.recipe(p_380072_))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        ShapedBackpackRecipe shapedrecipe = new ShapedBackpackRecipe(
                Objects.requireNonNullElse(this.group, ""),
                RecipeBuilder.determineBookCategory(this.category),
                shapedrecipepattern,
                this.resultStack,
                this.showNotification
        );
        p_301098_.accept(p_380072_, shapedrecipe, advancement$builder.build(p_380072_.identifier().withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private ShapedRecipePattern ensureValid(ResourceKey<Recipe<?>> recipe) {
        if(this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipe.identifier());
        } else {
            return ShapedRecipePattern.of(this.key, this.rows);
        }
    }
}
