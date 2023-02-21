package com.diamantino.spacerevolution.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CrushingRecipe extends BaseRecipe {
    public CrushingRecipe(Identifier id, ItemStack output, DefaultedList<Ingredient> recipeItems) {
        super(id, output, recipeItems);
    }

    @Override
    public boolean matches(SimpleInventory inventory, World world) {
        if (world.isClient())
            return false;

        return recipeItems.get(0).test(inventory.getStack(0));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.instance;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.instance;
    }

    public static class Type extends BaseType<CrushingRecipe> {
        public static final BaseType<CrushingRecipe> instance = new Type();
        public static final String id = "crushing";
    }

    public static class Serializer extends BaseSerializer<CrushingRecipe> {
        public static final BaseSerializer<CrushingRecipe> instance = new Serializer();
        public static final String id = "crushing";

        @Override
        public CrushingRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new CrushingRecipe(id, output, inputs);
        }

        @Override
        public CrushingRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            inputs.replaceAll(ingredient -> Ingredient.fromPacket(buf));

            ItemStack output = buf.readItemStack();

            return new CrushingRecipe(id, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, CrushingRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());
        }
    }
}
