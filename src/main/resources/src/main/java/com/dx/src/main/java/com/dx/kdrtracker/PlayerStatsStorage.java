package com.dx.kdrtracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class PlayerStatsStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File file;

    private static final Map<UUID, PlayerStats> stats = new HashMap<>();

    public static class PlayerStats {
        public int kills = 0;
        public int deaths = 0;
        public List<String> killLog = new ArrayList<>();
    }

    public static void load(MinecraftServer server) {
        file = server.getSavePath(net.minecraft.util.WorldSavePath.ROOT).resolve("kdr_stats.json").toFile();

        try {
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Map<String, PlayerStats> loaded = GSON.fromJson(reader, Map.class);
                reader.close();

                if (loaded != null) {
                    loaded.forEach((uuid, data) -> {
                        PlayerStats ps = GSON.fromJson(GSON.toJson(data), PlayerStats.class);
                        stats.put(UUID.fromString(uuid), ps);
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            FileWriter writer = new FileWriter(file);
            Map<String, PlayerStats> saveMap = new HashMap<>();
            stats.forEach((uuid, data) -> saveMap.put(uuid.toString(), data));
            GSON.toJson(saveMap, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PlayerStats get(UUID uuid) {
        return stats.computeIfAbsent(uuid, u -> new PlayerStats());
    }

    public static void addKill(UUID killer, UUID victim) {
        PlayerStats s = get(killer);
        s.kills++;
        s.killLog.add("Killed " + victim + " at " + new Date());
        save();
    }

    public static void addDeath(UUID player) {
        PlayerStats s = get(player);
        s.deaths++;
        save();
    }
}
