package gg.tater.bedrock.adapter;

import com.google.gson.*;
import gg.tater.bedrock.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Override
    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) element;
        return ItemUtil.deserialize(object.get("data").getAsString());
    }

    @Override
    public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("data", ItemUtil.serialize(stack));
        return object;
    }
}
