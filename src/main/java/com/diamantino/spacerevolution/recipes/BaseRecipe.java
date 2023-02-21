package com.diamantino.spacerevolution.recipes;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public abstract class BaseRecipe implements Recipe<SimpleInventory> {
    final Identifier id;
    final ItemStack output;
    final DefaultedList<Ingredient> recipeItems;

    public BaseRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public ItemStack craft(SimpleInventory inventory) {
        return output;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    public abstract static class BaseType<T extends BaseRecipe> implements RecipeType<T> {
        public BaseType() {}
    }

    public abstract static class BaseSerializer<S extends BaseRecipe> implements RecipeSerializer<S> {
        public BaseSerializer() {}
    }
}
