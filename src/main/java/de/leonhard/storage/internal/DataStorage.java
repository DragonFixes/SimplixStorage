package de.leonhard.storage.internal;

import de.leonhard.storage.internal.exceptions.SimplixValidationException;
import de.leonhard.storage.internal.provider.SimplixProviders;
import de.leonhard.storage.internal.serialize.SimplixSerializer;
import de.leonhard.storage.util.ClassWrapper;
import de.leonhard.storage.util.Valid;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface DataStorage {

  /**
   * Basic method to receive data from your data-structure
   *
   * @param key Key to search data for
   * @return Object in data-structure. Null if nothing was found!
   */
  @Nullable
  Object get(final String key);

  /**
   * Checks whether a key exists in the data-structure
   *
   * @param key Key to check
   * @return Returned value.
   */
  boolean contains(final String key);

  /**
   * Set an object to your data-structure
   *
   * @param key   The key your value should be associated with
   * @param value The value you want to set in your data-structure.
   */
  void set(final String key, final Object value);

  Set<String> singleLayerKeySet();

  Set<String> singleLayerKeySet(final String key);

  Set<String> keySet();

  Set<String> keySet(final String key);

  void remove(final String key);

  // ----------------------------------------------------------------------------------------------------
  //
  // Default-Implementations
  //
  // ----------------------------------------------------------------------------------------------------

  /**
   * Method to get a value of a predefined type from our data structure will return {@link
   * Optional#empty()} if the value wasn't found.
   *
   * @param key  Key to search the value for
   * @param type Type of the value
   * @throws ClassCastException if the type is not valid
   */
  default <T> Optional<T> find(final String key, final Class<T> type) {
    final Object raw = get(key);
    //Key wasn't found
    if (raw == null) {
      return Optional.empty();
    }
    return Optional.of(ClassWrapper.getFromDef(raw, type));
  }

  /**
   * Method to get a value of a predefined type from our data structure will return {@link
   * Optional#empty()} if the value wasn't found.<br>
   * The type is only used to get the class.
   *
   * @param key  Key to search the value for
   * @param type Type of the value
   * @throws ClassCastException if the type is not valid
   */
  default <T> Optional<T> find(final String key, final T type) {
    final Object raw = get(key);
    //Key wasn't found
    if (raw == null) {
      return Optional.empty();
    }
    return Optional.of(ClassWrapper.getFromDef(raw, type));
  }

  /**
   * Method to deserialize a class using the {@link SimplixSerializer}. You will need to register
   * your serializable in the {@link SimplixSerializer} before.
   *
   * @param key   The key your value should be associated with.
   * @param value The value you want to set in your data-structure.
   * @throws SimplixValidationException if an error is found
   */
  default <T> void setSerializable(@NonNull final String key, @NonNull final T value) {
    try {
      final Object data = SimplixSerializer.serialize(value);
      set(key, data);
    } catch (final Throwable throwable) {
      throw SimplixProviders.exceptionHandler().create(
          throwable,
          "Can't serialize: '" + key + "'",
          "Class: '" + value.getClass().getName() + "'",
          "Package: '" + value.getClass().getPackage() + "'");
    }
  }

  // ----------------------------------------------------------------------------------------------------
  // Getting Strings & primitive types from data-structure
  // ----------------------------------------------------------------------------------------------------

  /**
   * Get a value or a default one
   *
   * @param key Path to value in data-structure
   * @param def Default value & type of it
   * @see #getOrDefault(String, Object)
   */
  default <T> T get(final String key, final T def) {
    return getOrDefault(key, def);
  }

  /**
   * Get a value or null if no present
   *
   * @param key Path to value in data-structure
   * @param def Type of it
   */
  @Nullable
  default <T> T getRaw(final String key, final T def) {
    final Object raw = get(key);
    return raw == null ? null : ClassWrapper.getFromDef(raw, def);
  }

  /**
   * Get a value or null if no present
   *
   * @param key Path to value in data-structure
   * @param type Type of it
   */
  @Nullable
  default <T> T getRaw(final String key, final Class<T> type) {
    final Object raw = get(key);
    return raw == null ? null : ClassWrapper.getFromDef(raw, type);
  }

  /**
   * Get a String from a data-structure
   *
   * @param key Path to String in data-structure
   * @return Returns the value
   */
  default String getString(final String key) {
    return getOrDefault(key, "");
  }

  /**
   * Gets a long from a data-structure by key
   *
   * @param key Path to long in data-structure
   * @return String from data-structure
   */
  default long getLong(final String key) {
    return getOrDefault(key, 0L);
  }

  /**
   * Gets an int from a data-structure
   *
   * @param key Path to int in data-structure
   * @return Int from data-structure
   */
  default int getInt(final String key) {
    return getOrDefault(key, 0);
  }

  /**
   * Get a byte from a data-structure
   *
   * @param key Path to byte in data-structure
   * @return Byte from data-structure
   */
  default byte getByte(final String key) {
    return getOrDefault(key, (byte) 0);
  }

  /**
   * Get a boolean from a data-structure
   *
   * @param key Path to boolean in data-structure
   * @return Boolean from data-structure
   */
  default boolean getBoolean(final String key) {
    return getOrDefault(key, false);
  }

  /**
   * Get a float from a data-structure
   *
   * @param key Path to float in data-structure
   * @return Float from data-structure
   */
  default float getFloat(final String key) {
    return getOrDefault(key, 0F);
  }

  /**
   * Get a double from a data-structure
   *
   * @param key Path to double in the data-structure
   * @return Double from data-structure
   */
  default double getDouble(final String key) {
    return getOrDefault(key, 0D);
  }

  // ----------------------------------------------------------------------------------------------------
  // Getting Lists and non-ClassWrapper types from data-structure
  // ----------------------------------------------------------------------------------------------------

  /**
   * Get a List from a data-structure
   *
   * @param key Path to List in data structure.
   */
  @NonNull
  default List<?> getList(final String key) {
    return getOrDefault(key, new ArrayList<>());
  }

  /**
   * Attempts to get a List of the given type
   * @param key Path to List in data structure.
   */
  @NonNull
  default <T> List<T> getListParameterized(final String key) {
    return getOrSetDefault(key, new ArrayList<>());
  }

  @NonNull
  default List<String> getStringList(final String key) {
    return getOrDefault(key, new ArrayList<>());
  }

  @NonNull
  default List<Integer> getIntegerList(final String key) {
    return getOrDefault(key, new ArrayList<String>()).stream().map(Integer::parseInt).collect(Collectors.toList());
  }

  @NonNull
  default List<Byte> getByteList(final String key) {
    return getOrDefault(key, new ArrayList<String>()).stream().map(Byte::parseByte).collect(Collectors.toList());
  }

  @NonNull
  default List<Long> getLongList(final String key) {
    return getOrDefault(key, new ArrayList<String>()).stream().map(Long::parseLong).collect(Collectors.toList());
  }

  @NonNull
  default Map<?, ?> getMap(final String key) {
    return getOrDefault(key, new HashMap<>());
  }

  /**
   * Attempts to get a map of the given type
   * @param key Path to the Map in the data-structure
   */
  @NonNull
  default <K, V> Map<K, V> getMapParameterized(final String key) {
    return getOrSetDefault(key, new HashMap<>());
  }

  /**
   * Serialize an Enum from entry in the data-structure
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param <E>      EnumType
   * @return Serialized
   * @throws IllegalArgumentException if no enum match
   */
  @NonNull
  default <E extends Enum<E>> E getEnum(
      final String key,
      final Class<E> enumType) {
    final Object object = get(key);
    Valid.checkBoolean(
        object instanceof String,
        "No usable Enum-Value found for '" + key + "'.");
    return Enum.valueOf(enumType, (String) object);
  }

  /**
   * Serialize an Enum from entry in the data-structure
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param <E>      EnumType
   * @return Serialized
   * @throws IllegalArgumentException if no enum match
   */
  @Nullable
  default <E extends Enum<E>> E getRawEnum(
          final String key,
          final Class<E> enumType) {
    final Object object = get(key);
    if (object == null) return null;
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Enum.valueOf(enumType, (String) object);
  }

  /**
   * Serialize an Enum from entry in the data-structure
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param <E>      EnumType
   * @return Serialized Enum
   */
  default <E extends Enum<E>> Optional<E> findEnum(
          final String key,
          final Class<E> enumType) {
    final Object object = get(key);
    if (object == null)
      return Optional.empty();
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Optional.of(Enum.valueOf(enumType, (String) object));
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses an extra mapper to skip simple scenarios. (like capitalization)
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param mapper   Mapper for some simple scenarios
   * @param <E>      EnumType
   * @throws IllegalArgumentException if no enum match
   * @return Serialized Enum
   */
  @NonNull
  default <E extends Enum<E>> E getEnum(
          final String key,
          final Class<E> enumType,
          final Function<String, String> mapper) {
    final Object object = get(key);
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Enum.valueOf(enumType, mapper.apply((String) object));
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses an extra mapper to skip simple scenarios. (like capitalization)
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param mapper   Mapper for some simple scenarios
   * @param <E>      EnumType
   * @throws IllegalArgumentException if no enum match
   * @return Serialized Enum
   */
  @Nullable
  default <E extends Enum<E>> E getRawEnum(
          final String key,
          final Class<E> enumType,
          final Function<String, String> mapper) {
    final Object object = get(key);
    if (object == null) return null;
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Enum.valueOf(enumType, mapper.apply((String) object));
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses an extra mapper to skip simple scenarios. (like capitalization)
   *
   * @param key      Path to Enum
   * @param enumType Class of the Enum
   * @param mapper   Mapper for some simple scenarios
   * @param <E>      EnumType
   * @return Serialized Enum
   */
  default <E extends Enum<E>> Optional<E> findEnum(
          final String key,
          final Class<E> enumType,
          final Function<String, String> mapper) {
    final Object object = get(key);
    if (object == null)
      return Optional.empty();
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Optional.of(Enum.valueOf(enumType, mapper.apply((String) object)));
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses a transformer to get the enum.
   *
   * @param key         Path to Enum
   * @param transformer Transformer to enum
   * @param <E>         EnumType
   * @throws IllegalArgumentException if no enum match
   * @return Serialized Enum
   */
  @NonNull
  default <E extends Enum<E>> E getEnum(
          final String key,
          final Function<String, E> transformer) {
    final Object object = get(key);
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return transformer.apply((String) object);
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses a transformer to get the enum.
   *
   * @param key         Path to Enum
   * @param transformer Transformer to enum
   * @param <E>         EnumType
   * @throws IllegalArgumentException if no enum match
   * @return Serialized Enum
   */
  @Nullable
  default <E extends Enum<E>> E geRawEnum(
          final String key,
          final Function<String, E> transformer) {
    final Object object = get(key);
    if (object == null) return null;
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return transformer.apply((String) object);
  }

  /**
   * Serialize an Enum from entry in the data-structure.<br>
   * Uses a transformer to get the enum.
   *
   * @param key         Path to Enum
   * @param transformer Transformer to enum
   * @param <E>         EnumType
   * @return Serialized Enum
   */
  default <E extends Enum<E>> Optional<E> findEnum(
          final String key,
          final Function<String, E> transformer) {
    final Object object = get(key);
    if (object == null)
      return Optional.empty();
    Valid.checkBoolean(
            object instanceof String,
            "No usable Enum-Value found for '" + key + "'.");
    return Optional.of(transformer.apply((String) object));
  }

  /**
   * Method to serialize a Class using the {@link SimplixSerializer}.<br>
   * You will need to register your serializable in the {@link SimplixSerializer} before.
   *
   * @return Serialized instance of class.
   */
  @Nullable
  default <T> T getSerializable(final String key, final Class<T> clazz) {
    if (contains(key)) {
      Object raw = get(key);
      if (raw != null) {
        return SimplixSerializer.deserialize(raw, clazz);
      }
    }
    return null;
  }

  /**
   * Method to serialize a Class using the {@link SimplixSerializer}.<br>
   * You will need to register your serializable in the {@link SimplixSerializer} before.<br>
   * If the key doesn't yet exist, it will be created in the data-structure, set to def and afterward returned.
   *
   * @return Serialized instance of class.
   */
  @Nullable
  default <T> T getOrSetSerializable(final String key, final Class<T> clazz, final T def) {
    if (contains(key)) {
      Object raw = get(key);
      if (raw != null) {
        return SimplixSerializer.deserialize(raw, clazz);
      }
    }
    setSerializable(key, def);
    return def;
  }

  /**
   * Method to serialize a Class using the {@link SimplixSerializer}.<br>
   * You will need to register your serializable in the {@link SimplixSerializer} before.
   *
   * @return Serialized instance of class.
   * @throws NullPointerException if no serializer for the given class is found
   * @throws ClassCastException if the data does not match
   */
  @Nullable
  default <T> Optional<T> findSerializable(final String key, final Class<T> clazz) {
    if (contains(key)) {
      Object raw = get(key);
      if (raw != null) {
        return Optional.of(SimplixSerializer.deserialize(raw, clazz));
      }
    }
    return Optional.empty();
  }

  @Nullable
  default <T> List<T> getSerializableList(final String key, final Class<T> type) {
    if (!contains(key)) {
      return null;
    }

    final List<?> rawList = getList(key);

    return rawList
        .stream()
        .map(input -> SimplixSerializer.deserialize(input, type))
        .collect(Collectors.toList());
  }

  // ----------------------------------------------------------------------------------------------------
  // Advanced methods to save time.
  // ----------------------------------------------------------------------------------------------------

  /**
   * Returns the value for key in the data-structure, if it exists, else the specified default value.
   *
   * @param key Key to data in our data-structure.
   * @param def Default value, if data-structure doesn't contain key.
   * @param <T> Type of default-value.
   * @throws ClassCastException if the type is not valid
   */
  default <T> T getOrDefault(final String key, @NonNull final T def) {
    final Object raw = get(key);
    return raw == null ? def : ClassWrapper.getFromDef(raw, def);
  }

  /**
   * Sets a value in the data-structure if the key doesn't yet exist.
   * Has nothing to do with Bukkit's 'addDefault' method.
   *
   * @param key   Key of data to set in our data-structure.
   * @param value Value to set.
   */
  default void setDefault(final String key, final Object value) {
    if (!contains(key)) {
      set(key, value);
    }
  }

  /**
   * Mix of setDefault & getDefault.
   * <p>Gets the value of the key in the data structure, casted to the type of the specified default def.
   * If the key doesn't yet exist, it will be created in the data-structure, set to def and afterward returned.</p>
   *
   * @param key Key to set the value
   * @param def Value to set or return.
   */
  default <T> T getOrSetDefault(final String key, final T def) {
    final Object raw = get(key);
    //Key is not yet present in data-structure
    if (raw == null) {
      set(key, def);
      return def;
    } else {
      return ClassWrapper.getFromDef(raw, def);
    }
  }
}
