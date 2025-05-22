package com.bawnorton.mcchatgpt.config;

import com.bawnorton.mcchatgpt.MCChatGPT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path configPath = FMLPaths.CONFIGDIR.get().resolve(MCChatGPT.MODID + ".json");

    public static void loadConfig() {
        Config config = load();

        if (config.token == null || config.secret == null) {
            config.token = "";
            config.secret = "";
            MCChatGPT.LOGGER.info("Token or secret not found, resetting");
        }
        if (config.contextLevel == null || config.contextLevel < 0 || config.contextLevel > 3) config.contextLevel = 0;
        if (config.temperature == null || config.temperature < 0 || config.temperature > 2) config.temperature = 1.0;
        if (config.estimatedCostPerToken == null || config.estimatedCostPerToken < 0) config.estimatedCostPerToken = 2e-6F;
        if (config.model == null) config.model = "gpt-3.5-turbo";
        if (config.baseurl == null)
            config.baseurl = "https://api.openai.com/";

        Config.update(config);
        save();
        MCChatGPT.LOGGER.info("Loaded config");
    }

    private static Config load() {
        Config config = Config.getInstance();
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.createFile(configPath);
                return config;
            }
            try {
                config = GSON.fromJson(Files.newBufferedReader(configPath), Config.class);
            } catch (JsonSyntaxException e) {
                MCChatGPT.LOGGER.error("Failed to parse config file, using default config");
                config = Config.getInstance();
            }
        } catch (IOException e) {
            MCChatGPT.LOGGER.error("Failed to load config", e);
        }
        return config == null ? Config.getInstance() : config;
    }

    private static void save() {
        try {
            Files.write(configPath, GSON.toJson(Config.getInstance()).getBytes());
        } catch (IOException e) {
            MCChatGPT.LOGGER.error("Failed to save config", e);
        }
    }

    public static void saveConfig() {
        save();
        MCChatGPT.LOGGER.info("Saved config");
    }
}
