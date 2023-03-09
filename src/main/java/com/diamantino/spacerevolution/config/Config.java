package com.diamantino.spacerevolution.config;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configFilePath;
    private JsonObject configJson;

    public Config(Path configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void loadConfigFile() {
        JsonObject configJson = null;

        try (BufferedReader reader = Files.newBufferedReader(this.configFilePath, StandardCharsets.UTF_8)) {
            configJson = GSON.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (configJson != null)
            configJson.addProperty("_header", "SpaceRevolution Configs");

        this.configJson = configJson;
    }

    public boolean configFileExists() {
        return Files.exists(this.configFilePath);
    }

    public void initEmptyJson() {
        this.configJson = new JsonObject();
    }

    public void saveConfigFile() {
        try {
            Files.createDirectories(this.configFilePath.getParent());
        } catch (IOException ignored) {}

        try (BufferedWriter writer = Files.newBufferedWriter(this.configFilePath, StandardCharsets.UTF_8)) {
            GSON.toJson(this.configJson, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getIntValue(ConfigValue.IntValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsInt());
    }

    public void getLongValue(ConfigValue.LongValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsLong());
    }

    public void getFloatValue(ConfigValue.FloatValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsFloat());
    }

    public void getDoubleValue(ConfigValue.DoubleValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsDouble());
    }

    public void getBooleanValue(ConfigValue.BooleanValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(false);

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsBoolean());
    }

    public void getStringValue(ConfigValue.StringValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue("");

            return;
        }

        value.setValue(el.get("value").getAsJsonPrimitive().getAsString());
    }

    public void getStringListValue(ConfigValue.StringListValue value) {
        JsonObject el = this.configJson.getAsJsonObject(value.getValueName());

        if (el == null || !el.isJsonObject()) {
            value.setValue(Collections.emptyList());

            return;
        }

        List<String> list = new ArrayList<>();

        for (JsonElement child : el.get("value").getAsJsonArray()) {
            if (child.isJsonPrimitive()) {
                list.add(child.getAsJsonPrimitive().getAsString());
            }
        }

        value.setValue(list);
    }

    public void setIntValue(ConfigValue.IntValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.add(value.getValueName(), object);
    }

    public void setLongValue(ConfigValue.LongValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.add(value.getValueName(), object);
    }

    public void setFloatValue(ConfigValue.FloatValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.add(value.getValueName(), object);
    }

    public void setDoubleValue(ConfigValue.DoubleValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.add(value.getValueName(), object);
    }

    public void setBooleanValue(ConfigValue.BooleanValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.add(value.getValueName(), object);
    }

    public void setStringValue(ConfigValue.StringValue value) {
        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", new JsonPrimitive(value.getValue()));

        this.configJson.addProperty("_comment_" + value.getValueIdentifier(), value.getComment());
        this.configJson.add(value.getValueName(), object);
    }

    public void setStringListValue(ConfigValue.StringListValue value) {
        JsonArray array = new JsonArray();

        for (String str : value.getValue()) {
            array.add(str);
        }

        JsonObject object = new JsonObject();

        object.addProperty("comment", value.getComment());
        object.add("value", array);

        this.configJson.add(value.getValueName(), object);
    }

    public boolean containsValue(String name) {
        return this.configJson.has(name);
    }

    public void removeValue(String name) {
        this.configJson.remove(name);
    }
}
