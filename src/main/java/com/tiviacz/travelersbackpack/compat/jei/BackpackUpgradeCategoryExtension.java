package com.tiviacz.travelersbackpack.compat.jei;

import com.tiviacz.travelersbackpack.common.recipes.BackpackUpgradeRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.library.util.RecipeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BackpackUpgradeCategoryExtension implements ISmithingCategoryExtension<BackpackUpgradeRecipe> {
    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(BackpackUpgradeRecipe recipe, T ingredientAcceptor) {
        Optional<Ingredient> template = recipe.templateIngredient();
        Objects.requireNonNull(ingredientAcceptor);
        template.ifPresent(ingredientAcceptor::add);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(BackpackUpgradeRecipe recipe, T ingredientAcceptor) {
        Ingredient base = recipe.baseIngredient();
        ingredientAcceptor.add(base);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(BackpackUpgradeRecipe recipe, T ingredientAcceptor) {
        Optional<Ingredient> addition = recipe.additionIngredient();
        Objects.requireNonNull(ingredientAcceptor);
        addition.ifPresent(ingredientAcceptor::add);
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(BackpackUpgradeRecipe recipe, T ingredientAcceptor) {
        Optional<Ingredient> templateIngredient = recipe.templateIngredient();
        Ingredient baseIngredient = recipe.baseIngredient();
        Optional<Ingredient> additionIngredient = recipe.additionIngredient();
        Minecraft minecraft = Minecraft.getInstance();
        ContextMap contextmap = SlotDisplayContext.fromLevel((Level)Objects.requireNonNull(minecraft.level));
        List<ItemStack> templateStacks = (List)templateIngredient.map((i) -> i.display().resolveForStacks(contextmap)).orElse(List.of(ItemStack.EMPTY));
        if (templateStacks.isEmpty()) {
            templateStacks = List.of(ItemStack.EMPTY);
        }

        List<ItemStack> baseStacks = baseIngredient.display().resolveForStacks(contextmap);
        if (baseStacks.isEmpty()) {
            baseStacks = List.of(ItemStack.EMPTY);
        }

        ItemStack addition = (ItemStack)additionIngredient.map((i) -> i.display().resolveForFirstStack(contextmap)).orElse(ItemStack.EMPTY);

        for(ItemStack template : templateStacks) {
            for(ItemStack base : baseStacks) {
                SmithingRecipeInput recipeInput = new SmithingRecipeInput(template, base, addition);
                ItemStack output = RecipeUtil.assembleResultItem(recipeInput, recipe);
                ingredientAcceptor.add(output);
            }
        }

    }
}