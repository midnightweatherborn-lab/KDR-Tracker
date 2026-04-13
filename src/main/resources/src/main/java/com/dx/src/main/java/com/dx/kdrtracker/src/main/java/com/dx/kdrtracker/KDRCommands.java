package com.dx.kdrtracker;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.UUID;

public class KDRCommands {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

        dispatcher.register(CommandManager.literal("kdr")
            .then(CommandManager.argument("player", net.minecraft.command.argument.GameProfileArgumentType.gameProfile())
                .executes(ctx -> {
                    var profile = net.minecraft.command.argument.GameProfileArgumentType.getProfileArgument(ctx, "player").iterator().next();
                    UUID uuid = profile.getId();

                    var stats = PlayerStatsStorage.get(uuid);
                    double kdr = stats.deaths == 0 ? stats.kills : (double) stats.kills / stats.deaths;

                    ctx.getSource().sendFeedback(() ->
                        Text.literal(profile.getName() + " KDR: " + kdr + " (Kills: " + stats.kills + ", Deaths: " + stats.deaths + ")"), false);

                    return 1;
                })
            )
        );

        dispatcher.register(CommandManager.literal("killlog")
            .then(CommandManager.argument("player", net.minecraft.command.argument.GameProfileArgumentType.gameProfile())
                .executes(ctx -> {
                    var profile = net.minecraft.command.argument.GameProfileArgumentType.getProfileArgument(ctx, "player").iterator().next();
                    UUID uuid = profile.getId();

                    var stats = PlayerStatsStorage.get(uuid);

                    ctx.getSource().sendFeedback(() ->
                        Text.literal("Kill Log for " + profile.getName() + ":"), false);

                    for (String entry : stats.killLog) {
                        ctx.getSource().sendFeedback(() -> Text.literal(" - " + entry), false);
                    }

                    return 1;
                })
            )
        );

        dispatcher.register(CommandManager.literal("deathcount")
            .then(CommandManager.argument("player", net.minecraft.command.argument.GameProfileArgumentType.gameProfile())
                .executes(ctx -> {
                    var profile = net.minecraft.command.argument.GameProfileArgumentType.getProfileArgument(ctx, "player").iterator().next();
                    UUID uuid = profile.getId();

                    var stats = PlayerStatsStorage.get(uuid);

                    ctx.getSource().sendFeedback(() ->
                        Text.literal(profile.getName() + " has died " + stats.deaths + " times."), false);

                    return 1;
                })
            )
        );
    }
}
