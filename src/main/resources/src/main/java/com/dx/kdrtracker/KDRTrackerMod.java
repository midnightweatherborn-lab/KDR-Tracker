package com.dx.kdrtracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerKillEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerDeathCallback;
import net.minecraft.server.MinecraftServer;

public class KDRTrackerMod implements ModInitializer {

    public static MinecraftServer server;

    @Override
    public void onInitialize() {

        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            server = s;
            PlayerStatsStorage.load(s);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(s -> {
            PlayerStatsStorage.save();
        });

        PlayerKillEntityCallback.EVENT.register((player, entity) -> {
            if (entity instanceof net.minecraft.entity.player.PlayerEntity victim) {
                PlayerStatsStorage.addKill(player.getUuid(), victim.getUuid());
            }
        });

        PlayerDeathCallback.EVENT.register((player, source) -> {
            PlayerStatsStorage.addDeath(player.getUuid());
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            KDRCommands.register(dispatcher);
        });

        System.out.println("KDR Tracker Loaded!");
    }
}
