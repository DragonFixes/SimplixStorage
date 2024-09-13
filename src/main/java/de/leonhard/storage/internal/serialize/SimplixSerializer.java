package de.leonhard.storage.internal.serialize;

import de.leonhard.storage.util.Valid;

import java.util.*;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

/**
 * Class to register serializable's
 */
@UtilityClass
@SuppressWarnings("ALL")
public class SimplixSerializer {

  private final Map<Class<?>, SimplixSerializable<?>> serializables = Collections
      .synchronizedMap(new HashMap<>());

  public boolean isSerializable(final Class<?> clazz) {
    return findSerializable(clazz) != null;
  }

  /**
   * Register a serializable to our list
   *
   * @param serializable Serializable to register
   */
  public void registerSerializable(@NonNull final SimplixSerializable<?> serializable) {
    Valid.notNull(
        serializable.getClazz(),
        "Class of serializable mustn't be null");
    serializables.put(serializable.getClazz(), serializable);
  }

  @Nullable
  public <T> SimplixSerializable<T> findSerializable(final Class<T> clazz) {
    SimplixSerializable<?> s = serializables.get(clazz);
    if (s.getClazz() == clazz) return (SimplixSerializable<T>) s;
    return null;
  }

  /**
   * Serializes into an object to save
   */
  public <T> Object serialize(final T obj) {
    final SimplixSerializable serializable = findSerializable(obj.getClass());

    Valid.notNull(
        serializable,
        "No serializable found for '" + obj.getClass().getSimpleName() + "'");
    return serializable.serialize(obj);
  }

  /**
   * Serializes into an object to save
   */
  public <T> Object serialize(final T obj, final Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);

    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'",
            "Raw: '" + obj.getClass().getSimpleName() + "'");
    return serializable.serialize(obj);
  }

  /**
   * Deserialize an object into the given type (if available)
   */
  public <T> T deserialize(final Object raw, final Object data, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'",
            "Raw: '" + raw.getClass().getSimpleName() + "'");
    return serializable.deserialize(raw, data);
  }

  /**
   * Deserialize an object into the given type (if available)
   */
  public <T> T deserialize(final Object raw, Class<T> type) {
    return deserialize(raw, null, type);
  }

  /**
   * Serializes a list into objects to save
   */
  public <T> List<Object> serializeList(final List<T> raw, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> serializable.serialize(o)).toList();
  }

  /**
   * Serializes a list into objects to save.<br>
   * Also handles every result and filters each one.
   */
  public <T> List<Object> serializeListFiltered(final List<T> raw, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> {
      try {
        return serializable.serialize(o);
      } catch (Throwable e) {
        return null;
      }
    }).filter(Objects::nonNull).toList();
  }

  /**
   * Deserializes a list of objects into the given type (if available)
   */
  public <T> List<T> deserializeList(final List<?> raw, final Object data, Class<T> type) {

    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> serializable.deserialize(o, data)).toList();
  }

  /**
   * Deserializes a list of objects into the given type (if available)
   */
  public <T> List<T> deserializeList(final List<?> raw, Class<T> type) {
    return deserializeList(raw, null, type);
  }

  /**
   * Deserializes a list of objects into the given type (if available)<br>
   * Also handles every result and filters each one.
   */
  public <T> List<T> deserializeListFiltered(final List<?> raw, final Object data, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> {
      try {
        return serializable.deserialize(o, data);
      } catch (Throwable e) {
        return null;
      }
    }).filter(Objects::nonNull).toList();
  }

  /**
   * Deserializes a list of objects into the given type (if available)<br>
   * Also handles every result and filters each one.
   */
  public <T> List<T> deserializeListFiltered(final List<?> raw, Class<T> type) {
    return deserializeListFiltered(raw, null, type);
  }

  /**
   * Serializes a map into objects to save.
   */
  public <T> Map<String, Object> serializeMap(final Map<String, T> map, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    final Map<String, Object> newMap = new HashMap<>();
    for (Map.Entry<String, T> e : map.entrySet()) {
      newMap.put(e.getKey(), serializable.serialize(e.getValue()));
    }
    return newMap;
  }

  /**
   * Serializes a map into objects to save.<br>
   * Also handles every result and filters each one.
   */
  public <T> Map<String, Object> serializeMapFiltered(final Map<String, T> map, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    final Map<String, Object> newMap = new HashMap<>();
    for (Map.Entry<String, T> e : map.entrySet()) {
      try {
        newMap .put(e.getKey(), serializable.serialize(e.getValue()));
      } catch (Throwable ex) {
      }
    }
    return newMap;
  }

  /**
   * Deserialize a map of objects into the given type (if available)
   */
  public <T> Map<String, T> deserializeMap(final Map<?, ?> raw, final Object data, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    final Map<String, T> map = new HashMap<>();
    for (Map.Entry<?, ?> e : raw.entrySet()) {
      map.put((String) e.getKey(), serializable.deserialize(e.getValue(), data));
    }
    return map;
  }

  /**
   * Deserialize a map of objects into the given type (if available)
   */
  public <T> Map<String, T> deserializeMap(final Map<?, ?> raw, Class<T> type) {
    return deserializeMap(raw, null, type);
  }

  /**
   * Deserialize a map of objects into the given type (if available)<br>
   * Also handles every result and filters each one.
   */
  public <T> Map<String, T> deserializeMapFiltered(final Map<?, ?> raw, final Object data, Class<T> type) {
    final SimplixSerializable<T> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    final Map<String, T> map = new HashMap<>();
    for (Map.Entry<?, ?> e : raw.entrySet()) {
      try {
        map.put((String) e.getKey(), serializable.deserialize(e.getValue(), data));
      } catch (Throwable ex) {
      }
    }
    return map;
  }

  /**
   * Deserialize a map of objects into the given type (if available)<br>
   * Also handles every result and filters each one.
   */
  public <T> Map<String, T> deserializeMapFiltered(final Map<?, ?> raw, Class<T> type) {
    return deserializeMapFiltered(raw, null, type);
  }
}
