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
  public SimplixSerializable<?> findSerializable(final Class<?> clazz) {
    return serializables.get(clazz);
  }

  /**e
   * Method to save an object
   */
  public Object serialize(final Object obj) {
    final SimplixSerializable serializable = findSerializable(obj.getClass());

    Valid.notNull(
        serializable,
        "No serializable found for '" + obj.getClass().getSimpleName() + "'");
    return serializable.serialize(obj);
  }

  public Object serialize(final Object obj, Class<?> clazz) {
    final SimplixSerializable serializable = findSerializable(clazz);

    Valid.notNull(
            serializable,
            "No serializable found for '" + obj.getClass().getSimpleName() + "'");
    return serializable.serialize(obj);
  }

  public <T> T deserialize(final Object raw, Class<T> type) {
    final SimplixSerializable<?> serializable = findSerializable(type);
    Valid.notNull(
        serializable,
        "No serializable found for '" + type.getSimpleName() + "'",
        "Raw: '" + raw.getClass().getSimpleName() + "'");
    return (T) serializable.deserialize(raw);
  }

  public <T> List<T> deserializeList(final List<Object> raw, Class<T> type) {
    final SimplixSerializable<?> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> (T) serializable.deserialize(o)).toList();
  }

  public <T> List<T> deserializeListHandled(final List<Object> raw, Class<T> type) {
    final SimplixSerializable<?> serializable = findSerializable(type);
    Valid.notNull(
            serializable,
            "No serializable found for '" + type.getSimpleName() + "'");
    return raw.stream().map(o -> {
      try {
        return (T) serializable.deserialize(o);
      } catch (Throwable e) {
        return null;
      }
    }).filter(Objects::nonNull).toList();
  }
}
