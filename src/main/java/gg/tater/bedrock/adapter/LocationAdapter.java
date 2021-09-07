package gg.tater.bedrock.adapter;

import com.google.gson.*;
import gg.tater.bedrock.util.LocationUtil;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return LocationUtil.deserialize(((JsonObject) element).get("data").getAsString());
    }

    @Override
    public JsonElement serialize(Location location, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("data", LocationUtil.serialize(location));
        return object;
    }
}
