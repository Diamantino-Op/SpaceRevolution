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
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsInt());
    }

    public void getLongValue(ConfigValue.LongValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsLong());
    }

    public void getFloatValue(ConfigValue.FloatValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsFloat());
    }

    public void getDoubleValue(ConfigValue.DoubleValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue(0);

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsDouble());
    }

    public void getBooleanValue(ConfigValue.BooleanValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue(false);

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsBoolean());
    }

    public void getStringValue(ConfigValue.StringValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonPrimitive()) {
            value.setValue("");

            return;
        }

        value.setValue(el.getAsJsonPrimitive().getAsString());
    }

    public void getStringListValue(ConfigValue.StringListValue value) {
        JsonElement el = this.configJson.get(value.getValueIdentifier());

        if (el == null || !el.isJsonArray()) {
            value.setValue(Collections.emptyList());

            return;
        }

        List<String> list = new ArrayList<>();

        for (JsonElement child : el.getAsJsonArray()) {
            if (child.isJsonPrimitive()) {
                list.add(child.getAsJsonPrimitive().getAsString());
            }
        }

        value.setValue(list);
    }

    public void setIntValue(ConfigValue.IntValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setLongValue(ConfigValue.LongValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setFloatValue(ConfigValue.FloatValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setDoubleValue(ConfigValue.DoubleValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setBooleanValue(ConfigValue.BooleanValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setStringValue(ConfigValue.StringValue value) {
        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), new JsonPrimitive(value.getValue()));
    }

    public void setStringListValue(ConfigValue.StringListValue value) {
        JsonArray array = new JsonArray();

        for (String str : value.getValue()) {
            array.add(str);
        }

        this.configJson.addProperty("_comment", value.getComment());
        this.configJson.add(value.getValueIdentifier(), array);
    }

    public boolean containsValue(String name) {
        return this.configJson.has(name);
    }

    public void removeValue(String name) {
        this.configJson.remove(name);
    }
}
