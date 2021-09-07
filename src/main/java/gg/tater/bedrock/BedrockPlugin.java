package gg.tater.bedrock;

import gg.tater.bedrock.database.BedrockDatabase;
import gg.tater.bedrock.database.Credentials;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BedrockPlugin extends JavaPlugin {

    private static final AtomicReference<BedrockDatabase> DATABASE_REFERENCE = new AtomicReference<>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        DATABASE_REFERENCE.set(new BedrockDatabase((new Credentials(
                getConfig().getString("DATABASE.host"),
                getConfig().getString("DATABASE.password"),
                getConfig().getInt("DATABASE.port"),
                getConfig().getInt("DATABASE.database")))));
    }

    @Override
    public void onDisable() {
        getDatabase().ifPresent(database -> database.getClient().shutdown());
    }

    public static Optional<BedrockDatabase> getDatabase() {
        return Optional.ofNullable(DATABASE_REFERENCE.get());
    }
}
