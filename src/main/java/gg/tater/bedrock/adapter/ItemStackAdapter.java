package gg.tater.bedrock.adapter;

import com.google.common.collect.Lists;
import com.google.gson.*;
import gg.tater.bedrock.json.WrappedJsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.List;

public class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private final String enchantKey = "enchant_key";
    private final String enchantLevel = "enchant_level";

    @Override
    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) element;

        Material material = Material.valueOf(object.get("material").getAsString());
        int amount = object.get("amount").getAsInt();

        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            JsonElement loreElement = object.get("lore");

            if (loreElement != null) {
                List<String> list = Lists.newArrayList();

                loreElement.getAsJsonArray()
                        .forEach(loreArrayElement -> {
                            JsonObject loreObject = (JsonObject) loreArrayElement;
                            list.add(loreObject.get("line_data").getAsString());
                        });

                meta.setLore(list);
            }

            JsonElement nameElement = object.get("display_name");
            if (nameElement != null) {
                meta.setDisplayName(nameElement.getAsString());
            }

            stack.setItemMeta(meta);
        }


        JsonElement enchantElement = object.get("enchants");

        if (enchantElement != null) {

            enchantElement.getAsJsonArray()
                    .forEach(enchantArrayElement -> {

                        JsonObject enchantObject = (JsonObject) enchantArrayElement;
                        NamespacedKey key = NamespacedKey.minecraft(enchantObject.get(enchantKey).getAsString());
                        int level = enchantObject.get(enchantLevel).getAsInt();

                        Enchantment found = Enchantment.getByKey(key);
                        if (found != null) {
                            stack.addEnchantment(found, level);
                        } else {
                            Bukkit.getLogger().info("Could not find enchantment by namespaced key.");
                        }
                    });
        }

        return stack;
    }

    @Override
    public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext context) {
        WrappedJsonObject object = new WrappedJsonObject()
                .add("material", stack.getType().name())
                .add("amount", stack.getAmount());

        if (!stack.getEnchantments().isEmpty()) {
            JsonArray enchantArray = new JsonArray();

            stack.getEnchantments()
                    .forEach((enchantment, integer) ->
                            enchantArray.add(new WrappedJsonObject()
                                    .add(enchantKey, enchantment.getKey().getKey())
                                    .add(enchantLevel, integer)
                                    .toRegularJsonObject()));

            object.add("enchants", enchantArray);
        }

        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            if (meta.hasDisplayName()) {
                object.add("display_name", meta.getDisplayName());
            }

            if (meta.getLore() != null) {
                JsonArray loreArray = new JsonArray();
                meta.getLore().forEach(string ->
                        loreArray.add(new WrappedJsonObject()
                                .add("line_data", string)
                                .toRegularJsonObject()));

                object.add("lore", loreArray);
            }
        }

        return object.toRegularJsonObject();
    }
}
