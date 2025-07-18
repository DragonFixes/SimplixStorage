package de.leonhard.storage.internal;

import de.leonhard.storage.internal.serialize.SimplixSerializerManager;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.util.JsonUtils;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

/**
 * An extended HashMap, to easily process the nested HashMaps created by reading the Configuration
 * files.
 */
@SuppressWarnings("unchecked")
public class FileData {

  private final Map<String, Object> localMap;
  @Getter
  private final String rawPath;

  public FileData(final DataType dataType, final String pathPattern) {
    this.rawPath = pathPattern;
    this.localMap = dataType.getMapImplementation();
  }

  public FileData(final Map<String, Object> map, final DataType dataType, final String pathPattern) {
    this.rawPath = pathPattern;
    this.localMap = dataType.getMapImplementation();

    this.localMap.putAll(map);
  }

  public FileData(final Map<String, Object> map, final DataType dataType) {
    this(map, dataType, ".");
  }

  public FileData(final JSONObject jsonObject, final String pathPattern) {
    this.rawPath = pathPattern;
    this.localMap = new HashMap<>(jsonObject.toMap());
  }

  public FileData(final JSONObject jsonObject) {
    this(jsonObject, ".");
  }

  public FileData(final JSONObject jsonObject, final DataType dataType, final String pathPattern) {
    this.rawPath = pathPattern;
    this.localMap = dataType.getMapImplementation();
    this.localMap.putAll(jsonObject.toMap());
  }

  public FileData(final JSONObject jsonObject, final DataType dataType) {
    this(jsonObject, dataType, ".");
  }

  /**
   * @return A string array split by the separator of this file.
   */
  public String[] splitPath(String path) {
    return path.split(Pattern.quote(rawPath));
  }

  public void clear() {
    this.localMap.clear();
  }

  /**
   * Loads data from a map clears our current data before
   *
   * @param map Map to load data from
   */
  public void loadData(final Map<String, Object> map) {
    clear();

    if (map != null) {
      this.localMap.putAll(map);
    }
  }

  /**
   * Method to get the object assign to a key from a FileData Object.
   *
   * @param key the key to look for.
   * @return the value assigned to the given key or null if the key does not exist.
   */
  public Object get(final String key) {
    final String[] parts = splitPath(key);
    return get(this.localMap, parts, 0);
  }

  /**
   * Method to get the object assign to a key from a FileData Object.
   *
   * @param key the key to look for.
   * @return the value assigned to the given key or null if the key does not exist.
   */
  public Object get(final String[] key) {
    return get(this.localMap, key, 0);
  }

  private Object get(final Map<String, Object> map, final String[] key, final int id) {
    if (id < key.length - 1) {
      if (map.get(key[id]) instanceof Map) {
        final Map<String, Object> tempMap = (Map<String, Object>) map.get(key[id]);
        return get(tempMap, key, id + 1);
      } else {
        return null;
      }
    } else {
      return map.get(key[id]);
    }
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized void insert(final String[] key, final Object value) {
    this.localMap.put(
            key[0],
            this.localMap.containsKey(key[0]) && this.localMap.get(key[0]) instanceof Map
                    ? insert((Map<String, Object>) this.localMap.get(key[0]), key, value, 1)
                    : insert(createNewMap(), key, value, 1));
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized void insert(final String key, final Object value) {
    insert(splitPath(key), value);
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializable(final String[] key, final T value, final Class<T> type) {
    insert(key, SimplixSerializerManager.serialize(value, type));
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializable(final String key, final T value, final Class<T> type) {
    insertSerializable(splitPath(key), value, type);
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializableList(final String[] key, final List<T> value, final Class<T> type) {
    insert(key, SimplixSerializerManager.serializeList(value, type));
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializableList(final String key, final List<T> value, final Class<T> type) {
    insertSerializableList(splitPath(key), value, type);
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializableMap(final String[] key, final Map<String, T> value, final Class<T> type) {
    insert(key, SimplixSerializerManager.serializeMap(value, type));
  }

  /**
   * Method to assign a value to a key.
   *
   * @param key   the key to be used.
   * @param value the value to be assigned to the key.
   */
  public synchronized <T> void insertSerializableMap(final String key, final Map<String, T> value, final Class<T> type) {
    insertSerializableMap(splitPath(key), value, type);
  }

  private Object insert(
          final Map<String, Object> map, final String[] key, final Object value,
          final int id) {
    if (id < key.length) {
      final Map<String, Object> tempMap = createNewMap(map);
      final Map<String, Object> childMap =
              map.containsKey(key[id]) && map.get(key[id]) instanceof Map
                      ? (Map<String, Object>) map.get(key[id])
                      : createNewMap();
      tempMap.put(key[id], insert(childMap, key, value, id + 1));
      return tempMap;
    } else {
      return value;
    }
  }

  /**
   * Check whether the map contains a certain key.
   *
   * @param key the key to be looked for.
   * @return true if the key exists, otherwise false.
   */
  public boolean containsKey(final String key) {
    final String[] parts = splitPath(key);
    return containsKey(this.localMap, parts, 0);
  }

  /**
   * Check whether the map contains a certain key.
   *
   * @param key the key to be looked for.
   * @return true if the key exists, otherwise false.
   */
  public boolean containsKey(final String[] key) {
    return containsKey(this.localMap, key, 0);
  }

  private boolean containsKey(
          final Map<String, Object> map, final String[] key,
          final int id) {
    if (id < key.length - 1) {
      if (map.containsKey(key[id]) && map.get(key[id]) instanceof Map) {
        final Map<String, Object> tempMap = (Map<String, Object>) map.get(key[id]);
        return containsKey(tempMap, key, id + 1);
      } else {
        return false;
      }
    } else {
      return map.containsKey(key[id]);
    }
  }

  /**
   * Remove a key with its assigned value from the map if given key exists.
   *
   * @param key the key to be removed from the map.
   */
  public synchronized void remove(final String key) {
    if (containsKey(key)) {
      final String[] parts = splitPath(key);
      removePr(parts);
    }
  }

  /**
   * Remove a key with its assigned value from the map if given key exists.
   *
   * @param key the key to be removed from the map.
   */
  public synchronized void remove(final String[] key) {
    if (containsKey(key)) {
      removePr(key);
    }
  }

  private void removePr(final @NotNull String[] key) {
    if (key.length == 1) {
      this.localMap.remove(key[0]);
    } else {
      final Object tempValue = this.localMap.get(key[0]);
      if (tempValue instanceof Map) {
        //noinspection unchecked
        this.localMap.put(key[0], this.removePr((Map) tempValue, key, 1));
        if (((Map) this.localMap.get(key[0])).isEmpty()) {
          this.localMap.remove(key[0]);
        }
      }
    }
  }

  private Map<String, Object> removePr(
          final Map<String, Object> map,
          final String[] key,
          final int keyIndex) {
    if (keyIndex < key.length - 1) {
      final Object tempValue = map.get(key[keyIndex]);
      if (tempValue instanceof Map) {
        //noinspection unchecked
        map.put(key[keyIndex], this.removePr((Map) tempValue, key, keyIndex + 1));
        if (((Map) map.get(key[keyIndex])).isEmpty()) {
          map.remove(key[keyIndex]);
        }
      }
    } else {
      map.remove(key[keyIndex]);
    }
    return map;
  }

  /**
   * get the keySet of a single layer of the map.
   *
   * @return the keySet of the top layer of localMap.
   */
  public Set<String> singleLayerKeySet() {
    return this.localMap.keySet();
  }

  /**
   * get the keySet of a single layer of the map.
   *
   * @param key the key of the layer.
   * @return the keySet of the given layer or an empty set if the key does not exist.
   */
  public Set<String> singleLayerKeySet(final String key) {
    return get(key) instanceof Map
            ? ((Map<String, Object>) get(key)).keySet()
            : new HashSet<>();
  }

  /**
   * get the keySet of a single layer of the map.
   *
   * @param key the key of the layer.
   * @return the keySet of the given layer or an empty set if the key does not exist.
   */
  public Set<String> singleLayerKeySet(final String[] key) {
    return get(key) instanceof Map
            ? ((Map<String, Object>) get(key)).keySet()
            : new HashSet<>();
  }

  /**
   * get the keySet of all layers of the map combined.
   *
   * @return the keySet of all layers of localMap combined (Format: key.subkey).
   */
  public Set<String> keySet() {
    return multiLayerKeySet(this.localMap);
  }

  public Set<Map.Entry<String, Object>> entrySet() {
    return multiLayerEntrySet(this.localMap);
  }

  public Set<Map.Entry<String, Object>> singleLayerEntrySet() {
    return this.localMap.entrySet();
  }

  /**
   * get the keySet of all sublayers of the given key combined.
   *
   * @param key the key of the layer
   * @return the keySet of all sublayers of the given key or an empty set if the key does not exist
   * (Format: key.subkey).
   */
  public Set<String> keySet(final String key) {
    return get(key) instanceof Map
            ? multiLayerKeySet((Map<String, Object>) get(key))
            : new HashSet<>();
  }

  /**
   * get the keySet of all sublayers of the given key combined.
   *
   * @param key the key of the layer
   * @return the keySet of all sublayers of the given key or an empty set if the key does not exist
   * (Format: key.subkey).
   */
  public Set<String> keySet(final String[] key) {
    return get(key) instanceof Map
            ? multiLayerKeySet((Map<String, Object>) get(key))
            : new HashSet<>();
  }

  /**
   * Private helper method to get the key set of a map containing maps recursively
   */
  private Set<String> multiLayerKeySet(final Map<String, Object> map) {
    final Set<String> out = new HashSet<>();
    for (final Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        for (final String tempKey : multiLayerKeySet((Map<String, Object>) entry.getValue())) {
          out.add(entry.getKey() + this.rawPath + tempKey);
        }
      } else {
        out.add(entry.getKey());
      }
    }
    return out;
  }

  private Set<Map.Entry<String, Object>> multiLayerEntrySet(
          final Map<String, Object> map) {
    final Set<Map.Entry<String, Object>> out = new HashSet<>();
    for (val entry : map.entrySet()) {
      if (map.get(entry.getKey()) instanceof Map) {
        for (final String tempKey :
                multiLayerKeySet((Map<String, Object>) map.get(entry.getKey()))) {
          out.add(new SimpleEntry<>(entry.getKey() + this.rawPath + tempKey, entry.getValue()));
        }
      } else {
        out.add(entry);
      }
    }
    return out;
  }

  /**
   * Get the size of a single layer of the map.
   *
   * @return the size of the top layer of localMap.
   */
  public int singleLayerSize() {
    return this.localMap.size();
  }

  /**
   * get the size of a single layer of the map.
   *
   * @param key the key of the layer.
   * @return the size of the given layer or 0 if the key does not exist.
   */
  public int singleLayerSize(final String key) {
    return get(key) instanceof Map ? ((Map) get(key)).size() : 0;
  }

  /**
   * Get the size of the local map.
   *
   * @return the size of all layers of localMap combined.
   */
  public int size() {
    return this.localMap.size();
  }

  /**
   * Get the size of all sublayers of the given key combined.
   *
   * @param key the key of the layers
   * @return the size of all sublayers of the given key or 0 if the key does not exist.
   */
  public int size(final String key) {
    Object o = get(key);
    if (o instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) o;
      return size(map);
    }
    return 0;
  }

  /**
   * Get the size of all sublayers of the given key combined.
   *
   * @param key the key of the layers
   * @return the size of all sublayers of the given key or 0 if the key does not exist.
   */
  public int size(final String[] key) {
    Object o = get(key);
    if (o instanceof Map) {
      Map<String, Object> map = (Map<String, Object>) o;
      return size(map);
    }
    return 0;
  }

  public void putAll(final Map<String, Object> map) {
    this.localMap.putAll(map);
  }

  public void putAllRaw(final Map<String[], Object> map) {
    for (Map.Entry<String[], Object> entry : map.entrySet()) {
      insert(entry.getKey(), entry.getValue());
    }
  }

  private int size(final Map<String, Object> map) {
    int size = map.size();
    for (final Map.Entry<String, Object> entry : map.entrySet()) {
      if (entry.getValue() instanceof Map) {
        size += size((Map<String, Object>) entry.getValue());
      }
    }
    return size;
  }

  // ----------------------------------------------------------------------------------------------------
  // Utility functions
  // ----------------------------------------------------------------------------------------------------

  public Map<String, Object> toMap() {
      return Objects.requireNonNullElseGet(this.localMap, HashMap::new);
  }

  public JSONObject toJsonObject() {
    return JsonUtils.getJsonFromMap(this.localMap);
  }

  public boolean isSorted() {
    return this.localMap instanceof LinkedHashMap;
  }

  public Map<String, Object> createNewMap() {
    return isSorted() ? new LinkedHashMap<>() : new HashMap<>();
  }

  public Map<String, Object> createNewMap(Map<String, Object> value) {
    return isSorted() ? new LinkedHashMap<>(value) : new HashMap<>(value);
  }

  // ----------------------------------------------------------------------------------------------------
  // Overridden methods form Object
  // ----------------------------------------------------------------------------------------------------

  @Override
  public int hashCode() {
    return this.localMap.hashCode();
  }

  @Override
  public String toString() {
    return this.localMap.toString();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    } else if (obj == null || getClass() != obj.getClass()) {
      return false;
    } else {
      final FileData fileData = (FileData) obj;
      return this.localMap.equals(fileData.localMap);
    }
  }
}
