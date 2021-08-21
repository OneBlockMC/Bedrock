package gg.tater.bedrock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Bedrock {
    Gson SHARED_GSON = new GsonBuilder().create();
}
