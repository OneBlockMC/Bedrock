package gg.tater.bedrock.database.pubsub.entity;

import gg.tater.bedrock.BedrockPlugin;

public interface RedisEntity<T> {

    default String toJsonString(T type) {
        return BedrockPlugin.SHARED_GSON.toJson(type);
    }

    default Object fromJsonString(String data, Class<?> clazz) {
        return BedrockPlugin.SHARED_GSON.fromJson(data, clazz);
    }

    void handle(T type);

    boolean cache();

    String hashName();

    String key(T type);
}
