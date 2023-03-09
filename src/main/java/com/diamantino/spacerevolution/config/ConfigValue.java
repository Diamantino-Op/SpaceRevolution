package com.diamantino.spacerevolution.config;

import net.minecraft.text.Text;

import java.util.List;

public abstract class ConfigValue {
    private final String valueName;
    private final String valueIdentifier;
    private final String commentIdentifier;

    public ConfigValue(String identifier, String commentIdentifier) {
        this.valueName = Text.translatable("config.spacerevolution." + identifier).toString();
        this.valueIdentifier = identifier;
        this.commentIdentifier = commentIdentifier;
    }

    public String getValueName() {
       return valueName;
    }

    public String getValueIdentifier() {
        return valueIdentifier;
    }

    public String getComment() {
        return Text.translatable("config.spacerevolution.comment." + commentIdentifier).toString();
    }

    public static class IntValue extends ConfigValue {
        private int value;

        public IntValue(String identifier, String commentIdentifier, int value) {
            super(identifier, commentIdentifier);

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

        public LongValue(String identifier, String commentIdentifier, long value) {
            super(identifier, commentIdentifier);

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

        public FloatValue(String identifier, String commentIdentifier, float value) {
            super(identifier, commentIdentifier);

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

        public DoubleValue(String identifier, String commentIdentifier, double value) {
            super(identifier, commentIdentifier);

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

        public BooleanValue(String identifier, String commentIdentifier, boolean value) {
            super(identifier, commentIdentifier);

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

        public StringValue(String identifier, String commentIdentifier, String value) {
            super(identifier, commentIdentifier);

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

        public StringListValue(String identifier, String commentIdentifier, List<String> value) {
            super(identifier, commentIdentifier);

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
