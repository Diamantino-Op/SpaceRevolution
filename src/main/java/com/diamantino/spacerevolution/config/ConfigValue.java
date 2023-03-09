package com.diamantino.spacerevolution.config;

import net.minecraft.text.Text;

import java.util.List;

public abstract class ConfigValue {
    private final String valueName;
    private final String valueIdentifier;

    public ConfigValue(String identifier) {
        valueName = Text.translatable("config.spacerevolution." + identifier).toString();
        valueIdentifier = identifier;
    }

    public String getValueName() {
       return valueName;
    }

    public String getValueIdentifier() {
        return valueIdentifier;
    }

    public class IntValue extends ConfigValue {
        private int value;

        public IntValue(String identifier, int value) {
            super(identifier);

            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public class LongValue extends ConfigValue {
        private long value;

        public LongValue(String identifier, long value) {
            super(identifier);

            this.value = value;
        }

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }
    }

    public class FloatValue extends ConfigValue {
        private float value;

        public FloatValue(String identifier, float value) {
            super(identifier);

            this.value = value;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }
    }

    public class DoubleValue extends ConfigValue {
        private double value;

        public DoubleValue(String identifier, double value) {
            super(identifier);

            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    public class BooleanValue extends ConfigValue {
        private boolean value;

        public BooleanValue(String identifier, boolean value) {
            super(identifier);

            this.value = value;
        }

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }

    public class StringValue extends ConfigValue {
        private String value;

        public StringValue(String identifier, String value) {
            super(identifier);

            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class StringListValue extends ConfigValue {
        private List<String> value;

        public StringListValue(String identifier, List<String> value) {
            super(identifier);

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
