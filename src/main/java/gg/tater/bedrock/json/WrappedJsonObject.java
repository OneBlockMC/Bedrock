package gg.tater.bedrock.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WrappedJsonObject {

    private final JsonObject object = new JsonObject();

    public WrappedJsonObject add(String field, String data) {
        object.addProperty(field, data);
        return this;
    }

    public WrappedJsonObject add(String field, int data) {
        object.addProperty(field, data);
        return this;
    }

    public WrappedJsonObject add(String field, JsonElement element) {
        object.add(field, element);
        return this;
    }

    public JsonObject toRegularJsonObject() {
        return object;
    }
}
