package gg.tater.bedrock.database.pubsub.entity;

import gg.tater.bedrock.Bedrock;

public interface RedisEntity<T> {

    default String toJsonString(T type) {
        return Bedrock.SHARED_GSON.toJson(type);
    }

    default Object fromJsonString(String data, Class<?> clazz) {
        return Bedrock.SHARED_GSON.fromJson(data, clazz);
    }

    void handle(T type);

    boolean cache();

    String hashName();

    String key(T type);
}
