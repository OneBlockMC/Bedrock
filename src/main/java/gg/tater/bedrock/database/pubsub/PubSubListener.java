package gg.tater.bedrock.database.pubsub;

import com.google.gson.JsonObject;
import gg.tater.bedrock.Bedrock;
import gg.tater.bedrock.database.BedrockDatabase;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PubSubListener extends RedisPubSubAdapter<String, String> {

    private final BedrockDatabase database;

    @Override
    public void message(String channelName, String message) {
        if (channelName.equals(database.getChannelName())) {
            try {
                JsonObject jsonObject = Bedrock.SHARED_GSON.fromJson(message, JsonObject.class);

                Class<?> clazz = Class.forName(jsonObject.get("class").getAsString());
                String json = jsonObject.get("content").getAsString();

                database.getRegisteredEntity(clazz)
                        .ifPresent(entity -> {
                            Object object = entity.fromJsonString(json, clazz);

                            if (entity.cache()) {
                                database.addToCache(object, entity);
                            }

                            entity.handle(object);
                        });
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}