package gg.tater.bedrock.database.pubsub;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.tater.bedrock.database.BedrockDatabase;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PubSubListener extends RedisPubSubAdapter<String, String> {

    private final BedrockDatabase database;

    @Override
    public void message(String channelName, String message) {
        if (channelName.equals(database.getCredentials().getChannelName())) {
            try {
                JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

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