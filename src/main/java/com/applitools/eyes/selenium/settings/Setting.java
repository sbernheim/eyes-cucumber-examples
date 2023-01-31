package com.applitools.eyes.selenium.settings;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;

public class Setting<V> {
    
    protected Optional<V> value;
    
    /**
     * Constructor that takes an Optional as the Setting value.
     * In most cases, you will want to use the Setting.fromProperty() or
     * Setting.fromEnv() static factory methods instead of this constructor.
     * 
     * @param an Optional of the Setting value
     */
    public Setting(Optional<V> value) {
        this.value = value;
    }
    
    /**
     * Constructor that takes a Setting value and wraps it in an Optional.
     * In most cases, you will want to use the Setting.fromProperty() or
     * Setting.fromEnv() static factory methods instead of this constructor.
     * 
     * @param a value for the Setting
     */
    public Setting(V value) {
        this(Optional.ofNullable(value));
    }
    
    /**
     * Factory method that creates a StringSetting from a String.
     * StringSetting is a Setting<String> plus methods for parsing String values.
     * 
     * @param String
     * @return StringSetting
     */
    public static StringSetting of(String value) {
        return new StringSetting(value);
    }
    
    /**
     * Factory method that creates a StringSetting from a System property.
     * StringSetting is a Setting<String> plus methods for parsing String values.
     * 
     * @param property
     * @return a StringSetting
     */
    public static StringSetting fromProperty(String property) {
        return of(System.getProperty(property));
    }
    
    /**
     * Factory method that creates a StringSetting from an environment variable.
     * StringSetting is a Setting<String> plus methods for parsing String values.
     * 
     * @param property
     * @return a StringSetting
     */
    public static StringSetting fromEnv(String envVar) {
        return of(System.getenv(envVar));
    }
    
    /**
     * Returns true if this Setting value is not null, or false if it is null.
     * 
     * @return a boolean
     */
    public boolean isNotNull() {
        return value.isPresent();
    }
    
    /**
     * If this Setting value is not null, returns a new Setting with the same value.
     * If this Setting value is null, returns a new Setting with the passed Optional value.
     * 
     * @param value - an Optional of the new value
     * @return a new Setting of the same Generic type
     */
    public Setting<V> or(Optional<V> value) {
        return new Setting<V>(this.value.or(() -> value));
    }
    
    /**
     * If this Setting value is not null, returns a new Setting with the same value.
     * If this Setting value is null, returns a new Setting with the passed value.
     * 
     * @param value - a new value (can be null)
     * @return a new Setting of the same Generic type
     */
    public Setting<V> or(V value) {
        return or(Optional.ofNullable(value));
    }
    
    /**
     * If this Setting value is not null, returns a new Setting with the same value.
     * If this Setting value is null, calls the passed Supplier function to get an Optional
     * of the new value and returns a new Setting with the returned value.
     * 
     * @param value - a Supplier function for an Optional of the new value
     * @return a new Setting of the same Generic type
     */
    public Setting<V> orCall(Supplier<? extends Optional<? extends V>> supplier) {
        return or(value.or(supplier));
    }
    
    /**
     * If this Setting value is not null, passes the value to the mapper Function and
     * returns a new Setting for that value.
     * If this Setting value is null, returns a new Setting with a null value.
     * 
     * Use this method to change the value type of a Setting, such as converting
     * a String to an Enum Type.
     * 
     * @param <R> - the type of the new Setting
     * @param mapper - a Function that converts this Setting value to the new value
     * @return a new Setting for the mapper Function return value
     */
    public <R> Setting<R> map(Function<V, R> mapper) {
        return new Setting<R>(value.map(mapper));
    }
    
    /**
     * If this Setting's value is not null, returns this Setting.
     * If this Setting value is null, returns a new Setting with the passed Optional value.
     * 
     * Not sure if this method is actually needed.
     * 
     * @param value - an Optional of the new value
     * @return a Setting of the same Generic type
     */
    /*public Setting<V> thisOr(Optional<V> value) {
        return isNotNull() ? this : new Setting<V>(value);
    }*/
    
    /**
     * Return the value of this Setting or null.
     * 
     * @return the Setting value or null
     */
    public V get() {
        return isNotNull() ? value.get() : null;
    }
    
    /**
     * Return the value of this Setting or the passed default value.
     * 
     * @param defaultValue
     * @return the Setting value or default value
     */
    public V orDefault(V defaultValue) {
        return or(defaultValue).get();
    }
    
    /**
     * Return the value of this Setting or throw a RuntimeException with the
     * passed message.
     * 
     * @param msg - a RuntimeException message String
     * @return the Setting value
     */
    public V orThrow(String msg) {
        return value.orElseThrow(() -> new RuntimeException(msg));
    }
    
    public static class StringSetting extends Setting<String> {
        
        /**
         * Constructor that takes an Optional as the StringSetting value.
         * In most cases, you will want to use the Setting.fromProperty() or
         * Setting.fromEnv() static factory methods instead of this constructor.
         * 
         * @param optionalValue
         */
        public StringSetting(Optional<String> value) {
            super(value);
        }
        
        /**
         * Constructor that takes a StringSetting value and wraps it in an Optional.
         * In most cases, you will want to use the Setting.fromProperty() or
         * Setting.fromEnv() static factory methods instead of this constructor.
         * 
         * @param a value for the Setting
         */
        public StringSetting(String value) {
            super(value);
        }
        
        public StringSetting or(String value) {
            return isNotBlank() ? this : new StringSetting(value);
        }

        public StringSetting orProperty(String property) {
            return or(System.getProperty(property));
        }
    
        public StringSetting orEnv(String envVar) {
            return or(System.getenv(envVar));
        }
    
        public Setting<Boolean> asBoolean() {
            return map(Boolean::parseBoolean);
        }
        
        public Setting<Integer> asInteger() {
            return map(Integer::parseInt);
        }
    
        public Setting<Long> asLong() {
            return map(Long::parseLong);
        }
    
        public boolean isNotBlank() {
            return isNotNull() && StringUtils.isNotBlank(get());
        }
        
        public StringSetting notBlank() {
            return isNotBlank() ? this : new StringSetting(Optional.empty());
        }
        
        public String notBlankOrThrow(String msg) {
            return notBlank().orThrow(msg);
        }

    }

}
