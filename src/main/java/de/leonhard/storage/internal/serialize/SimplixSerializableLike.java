package de.leonhard.storage.internal.serialize;

public interface SimplixSerializableLike {

  Object deserialized() throws ClassCastException;

  default Object deserializedParsed() throws ClassCastException {
    return SimplixSerializerManager.parseObject(deserialized());
  }
}
