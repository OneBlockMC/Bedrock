package gg.tater.bedrock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.tater.bedrock.adapter.ItemStackAdapter;
import gg.tater.bedrock.adapter.LocationAdapter;
import gg.tater.bedrock.database.BedrockDatabase;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface Bedrock {

    Gson SHARED_GSON = new GsonBuilder()
            .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .serializeNulls()
            .create();
}
