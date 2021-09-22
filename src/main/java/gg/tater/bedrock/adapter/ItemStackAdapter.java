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

    private final String displayNameField = "display_name";
    private final String enchantField = "enchants";
    private final String enchantKey = "enchant_key";
    private final String enchantLevel = "enchant_level";
    private final String materialField = "material";
    private final String amountField = "amount";
    private final String loreField = "lore";
    private final String lineDataField = "line_data";

    @Override
    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = (JsonObject) element;

        Material material = Material.valueOf(object.get(materialField).getAsString());
        int amount = object.get(amountField).getAsInt();

        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            JsonElement loreElement = object.get(loreField);

            if (loreElement != null) {
                List<String> list = Lists.newArrayList();

                loreElement.getAsJsonArray()
                        .forEach(loreArrayElement -> {
                            JsonObject loreObject = (JsonObject) loreArrayElement;
                            list.add(loreObject.get(lineDataField).getAsString());
                        });

                meta.setLore(list);
            }

            JsonElement nameElement = object.get(displayNameField);
            if (nameElement != null) {
                meta.setDisplayName(nameElement.getAsString());
            }

            stack.setItemMeta(meta);
        }


        JsonElement enchantElement = object.get(enchantField);

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
                .add(materialField, stack.getType().name())
                .add(amountField, stack.getAmount());

        if (!stack.getEnchantments().isEmpty()) {
            JsonArray enchantArray = new JsonArray();

            stack.getEnchantments()
                    .forEach((enchantment, integer) ->
                            enchantArray.add(new WrappedJsonObject()
                                    .add(enchantKey, enchantment.getKey().getKey())
                                    .add(enchantLevel, integer)
                                    .toRegularJsonObject()));

            object.add(enchantField, enchantArray);
        }

        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            if (meta.hasDisplayName()) {
                object.add(displayNameField, meta.getDisplayName());
            }

            if (meta.getLore() != null) {
                JsonArray loreArray = new JsonArray();
                meta.getLore().forEach(string ->
                        loreArray.add(new WrappedJsonObject()
                                .add(lineDataField, string)
                                .toRegularJsonObject()));

                object.add(loreField, loreArray);
            }
        }

        return object.toRegularJsonObject();
    }
}
