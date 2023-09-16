package org.koishi.launcher.h2co3.core.utils.json.model;

import androidx.annotation.NonNull;

import org.koishi.launcher.h2co3.core.utils.json.abstracts.JsonValue;

import java.math.BigDecimal;

/**
 * The class Json number stores a string that can be cast to a number.
 */
public class JsonNumber extends JsonValue {

    /**
     * The Value is a string that can be cast to a number.
     */
    protected String value;

    /**
     * Instantiates a new Json number.
     */
    public JsonNumber() {
    }

    /**
     * Instantiates a new Json number.
     *
     * @param key the key is the identifier of {@link JsonValue}
     */
    public JsonNumber(String key) {
        super(key);
    }

    /**
     * Instantiates a new Json number.
     *
     * @param key   the key is the identifier of {@link JsonValue}
     * @param value the value is the given string that is set to {@link #value}
     */
    public JsonNumber(String key, String value) {
        super(key);
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        String space = createSpace();

        stringBuilder.append(space);
        if (getKey() != null && !getKey().isEmpty()) {
            stringBuilder.append("\"").append(getKey()).append("\"").append(" : ");
        }
        stringBuilder.append(getValue());

        return stringBuilder.toString();
    }


    /**
     * Gets value.
     *
     * @return the value the value is the given string that is set to {@link #value}
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value is set to {@link #value}
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Byte value byte.
     * Convert {@link #value} to {@link Byte}
     *
     * @return the byte converted value
     */
    public byte byteValue() {
        return getBigDecimal().byteValue();
    }

    /**
     * Int value int.
     * Convert {@link #value} to {@link Integer}
     *
     * @return the int converted value
     */
    public int intValue() {
        return getBigDecimal().intValue();
    }

    /**
     * Short value short.
     * Convert {@link #value} to {@link Short}
     *
     * @return the short converted value
     */
    public short shortValue() {
        return getBigDecimal().shortValue();
    }

    /**
     * Double value double.
     * Convert {@link #value} to {@link Double}
     *
     * @return the double converted value
     */
    public double doubleValue() {
        return getBigDecimal().doubleValue();
    }

    /**
     * Float value float.
     * Convert {@link #value} to {@link Float}
     *
     * @return the float converted value
     */
    public float floatValue() {
        return getBigDecimal().floatValue();
    }

    /**
     * Long value long.
     * Convert {@link #value} to {@link Long}
     *
     * @return the long converted value
     */
    public long longValue() {
        return getBigDecimal().longValue();
    }

    /**
     * Gets number determined by given class {@link Class<T>}
     *
     * @param <T>   the type parameter determines the given number
     * @param clazz the clazz used to use correct number.
     * @return the number object of type {@link Number}
     */
    public <T> Number getNumber(Class<T> clazz) {

        if (clazz.equals(Byte.class) || clazz.equals(byte.class)) {
            return byteValue();
        } else if (clazz.equals(Integer.class) || clazz.equals(int.class)) {
            return intValue();
        } else if (clazz.equals(Short.class) || clazz.equals(short.class)) {
            return shortValue();
        } else if (clazz.equals(Double.class) || clazz.equals(double.class)) {
            return doubleValue();
        } else if (clazz.equals(Float.class) || clazz.equals(float.class)) {
            return floatValue();
        } else if (clazz.equals(Long.class) || clazz.equals(long.class)) {
            return longValue();
        }

        return null;
    }

    /**
     * Gets big decimal.
     *
     * @return the big decimal created by {@link #value}
     */
    public BigDecimal getBigDecimal() {
        return new BigDecimal(getValue());
    }


    /**
     * Is numeric boolean.
     * Check if given string is a number
     *
     * @param value the value is used to check
     * @return the boolean true or false
     */
    public boolean isNumeric(String value) {

        if (value == null) {
            return false;
        }
        boolean isNumber = value.matches("[+-]?(\\d+([.]\\d*)?([eE][+-]?\\d+)?|[.]\\d+([eE][+-]?\\d+)?)");

        if (isNumber) {
            String testValue;

            if (isInteger(value)) {
                int integerValue = intValue();

                testValue = String.valueOf(integerValue);
            } else if (isDouble(value)) {
                double doubleValue = doubleValue();


                if (doubleValue > Double.MAX_VALUE || doubleValue < Double.MIN_EXPONENT) {
                    return false;
                }

                testValue = String.valueOf(Math.round(doubleValue));
            } else if (isFloat(value)) {
                float floatValue = floatValue();

                if (floatValue > Float.MAX_VALUE || floatValue < Float.MIN_EXPONENT) {
                    return false;
                }

                testValue = String.valueOf(Math.round(floatValue));
            } else {
                testValue = value;
            }

            try {
                Long.parseLong(testValue);
            } catch (Exception exception) {
                return false;
            }

        }

        return isNumber;
    }

    /**
     * Is float boolean.
     * Check if given string is a float
     *
     * @param value the value is used to check
     * @return the boolean true or false
     */
    public boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Is integer boolean.
     * Check if given string is a integer
     *
     * @param value the value is used to check
     * @return the boolean true or false
     */
    public boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Is double boolean.
     * Check if given string is a double
     *
     * @param value the value is used to check
     * @return the boolean true or false
     */
    public boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}
