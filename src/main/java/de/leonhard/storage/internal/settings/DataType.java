package de.leonhard.storage.internal.settings;

import de.leonhard.storage.internal.provider.SimplixProviders;
import de.leonhard.storage.internal.provider.MapProvider;
import java.util.Map;
import lombok.RequiredArgsConstructor;

/**
 * An Enum defining how the Data should be stored
 */
@RequiredArgsConstructor
public enum DataType {
  SORTED {
    @Override
    public Map<String, Object> getMapImplementation() {
      return mapProvider.getSortedMapImplementation();
    }
  },

  UNSORTED {
    @Override
    public Map<String, Object> getMapImplementation() {
      return mapProvider.getMapImplementation();
    }
  };

  private static final MapProvider mapProvider = SimplixProviders.mapProvider();

  public static DataType forConfigSetting(final ConfigSettings configSettings) {
    if (configSettings == null) return UNSORTED;
    switch (configSettings) {
      // Only Configs needs the preservation of the order of the keys
      case PRESERVE_COMMENTS, FIRST_TIME -> {
          return SORTED;
      }
      // In all other cases using the normal HashMap is better to save memory.
      default -> {
          return UNSORTED;
      }
    }
  }

  public Map<String, Object> getMapImplementation() {
    throw new AbstractMethodError("Not implemented");
  }
}
