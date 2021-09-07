package gg.tater.bedrock.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@UtilityClass
public class ItemUtil {

    @SneakyThrows
    public ItemStack deserialize(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream input = new ByteArrayInputStream(bytes);
        BukkitObjectInputStream stream = new BukkitObjectInputStream(input);
        return (ItemStack) stream.readObject();
    }

    @SneakyThrows
    public String serialize(ItemStack stack) {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);

        os.writeObject(stack);
        os.flush();
        byte[] bytes = io.toByteArray();

        return new String(Base64.getEncoder().encode(bytes));
    }
}
