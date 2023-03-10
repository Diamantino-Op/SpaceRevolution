package com.diamantino.spacerevolution.config;

import net.minecraft.text.Text;

import java.util.List;

public abstract class ConfigValue {
    private final String valueName;
    private final String valueIdentifier;
    private final String defaultTranslation;

    public ConfigValue(String identifier, String defaultTranslation) {
        this.valueName = Text.translatable("config.spacerevolution." + identifier).getString();
        this.valueIdentifier = identifier;
        this.defaultTranslation = defaultTranslation;
    }

    public String getValueName() {
       return valueName;
    }

    public String getValueIdentifier() {
        return valueIdentifier;
    }

    public String getComment() {
        String translatedComment = Text.translatable("config.spacerevolution.comment." + valueIdentifier).getString();

        if (!translatedComment.contains("config.spacerevolution.comment."))
            return translatedComment;

        return defaultTranslation;
    }

    public static class IntValue extends ConfigValue {
        private int value;

        public IntValue(String identifier, String defaultTranslation, int value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class LongValue extends ConfigValue {
        private long value;

        public LongValue(String identifier, String defaultTranslation, long value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

    public static class FloatValue extends ConfigValue {
        private float value;

        public FloatValue(String identifier, String defaultTranslation, float value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

    public static class DoubleValue extends ConfigValue {
        private double value;

        public DoubleValue(String identifier, String defaultTranslation, double value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    public static class BooleanValue extends ConfigValue {
        private boolean value;

        public BooleanValue(String identifier, String defaultTranslation, boolean value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public static class StringValue extends ConfigValue {
        private String value;

        public StringValue(String identifier, String defaultTranslation, String value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class StringListValue extends ConfigValue {
        private List<String> value;

        public StringListValue(String identifier, String defaultTranslation, List<String> value) {
            super(identifier, defaultTranslation);

            this.value = value;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
