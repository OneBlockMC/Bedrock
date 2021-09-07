package gg.tater.bedrock.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import gg.tater.bedrock.database.pubsub.PubSubListener;
import gg.tater.bedrock.database.pubsub.entity.RedisEntity;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public final class BedrockDatabase {

    @Getter
    private final Credentials credentials;

    @Getter
    private final RedisClient client;

    @Getter
    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;

    private final Map<Class<?>, RedisEntity<?>> entityIdentityMap = Collections.synchronizedMap(Maps.newIdentityHashMap());

    @Getter
    private final String channelName = "object_channel";

    public BedrockDatabase(Credentials credentials) {
        this.credentials = credentials;

        this.client = RedisClient.create(RedisURI.builder()
                .withHost(credentials.getHost())
                .withPort(credentials.getPort())
                .withDatabase(credentials.getDatabase())
                .withPassword(credentials.getPassword())
                .withTimeout(Duration.ofSeconds(5L))
                .build());

        this.pool = ConnectionPoolSupport.createGenericObjectPool(client::connect, new GenericObjectPoolConfig<>());

        StatefulRedisPubSubConnection<String, String> pubSub = client.connectPubSub();
        pubSub.addListener(new PubSubListener(this));
        pubSub.sync().subscribe(channelName);
    }

    public <T> void publish(T provided) {
        getRegisteredEntity(provided.getClass()).ifPresent(entity -> {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("class", provided.getClass().getName());
            jsonObject.addProperty("content", entity.toJsonString(provided));

            getAsyncCommands().publish(channelName, jsonObject.toString());
        });
    }

    @SneakyThrows
    public <T> Optional<T> getCachedEntity(Class<T> clazz, String key) {
        Optional<RedisEntity<T>> optional = getRegisteredEntity(clazz);

        if (optional.isPresent()) {
            RedisEntity<T> redisEntity = optional.get();
            String json = getAsyncCommands().hget(redisEntity.hashName(), key.toLowerCase()).get();
            return Optional.ofNullable(clazz.cast(redisEntity.fromJsonString(json, clazz)));
        }

        return Optional.empty();
    }

    // o(n) be aware of time complexity
    @SneakyThrows
    public <T> List<T> getAllCachedEntities(Class<T> clazz) {
        List<T> entities = Lists.newArrayList();
        Optional<RedisEntity<T>> optional = getRegisteredEntity(clazz);

        if (optional.isPresent()) {
            RedisEntity<T> entity = optional.get();

            getAsyncCommands()
                    .hgetall(entity.hashName())
                    .get()
                    .values()
                    .stream()
                    .map(data -> entity.fromJsonString(data, clazz))
                    .map(clazz::cast)
                    .forEach(entities::add);
        }

        return entities;
    }

    public <T> Optional<T> popRandomElement(Class<T> clazz) throws ExecutionException, InterruptedException {
        Optional<RedisEntity<T>> optional = getRegisteredEntity(clazz);

        if (optional.isPresent()) {
            RedisEntity<T> entity = optional.get();

            return Optional.of(clazz.cast(
                    entity.fromJsonString(getAsyncCommands()
                            .srandmember(entity.hashName())
                            .get(), clazz)));
        }

        return Optional.empty();
    }

    public <T> void addToCache(T type, RedisEntity<T> redisEntity) {
        getAsyncCommands().hset(
                redisEntity.hashName(),
                redisEntity.key(type).toLowerCase(),
                redisEntity.toJsonString(type));
    }

    public <T> void removeFromCache(T type) {
        getRegisteredEntity(type.getClass())
                .ifPresent(redisEntity -> getAsyncCommands().hdel(redisEntity.hashName(), redisEntity.key(type).toLowerCase()));
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<RedisEntity<T>> getRegisteredEntity(Class<?> clazz) {
        RedisEntity<?> found = entityIdentityMap.get(clazz);
        return Optional.ofNullable((RedisEntity<T>) found);
    }

    public void registerEntity(Class<?> clazz, RedisEntity<?> redisEntity) {
        entityIdentityMap.put(clazz, redisEntity);
    }

    @SneakyThrows
    private RedisAsyncCommands<String, String> getAsyncCommands() {
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            return connection.async();
        }
    }
}
