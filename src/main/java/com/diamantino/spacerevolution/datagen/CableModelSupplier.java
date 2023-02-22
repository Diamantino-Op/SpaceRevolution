package com.diamantino.spacerevolution.datagen;

import com.diamantino.spacerevolution.initialization.ModReferences;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class CableModelSupplier implements Supplier<JsonElement> {
    private final Identifier parent;
    private final String textureName;

    public CableModelSupplier(Identifier parent, String textureName) {
        this.parent = parent;
        this.textureName = textureName;
    }

    @Override
    public JsonElement get() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("parent", this.parent.toString());

        JsonObject texturesObject = new JsonObject();
        texturesObject.addProperty("cable", new Identifier(ModReferences.modId, "block/cables/" + textureName).toString());

        jsonObject.add("textures", texturesObject);
        return jsonObject;
    }
}