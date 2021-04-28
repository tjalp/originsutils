package net.tjalp.originsutils.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.tjalp.originsutils.OriginsUtils;
import net.tjalp.originsutils.manager.WarpManager;
import net.tjalp.originsutils.object.Location;
import net.tjalp.originsutils.object.Warp;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WarpCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("warp")
                .then(argument("warp name", string())
                    .executes(context -> executeWarp(context.getSource(), getString(context, "warp name"))))
                .then(literal("list")
                    .executes(context -> executeList(context.getSource())))
                .then(literal("set")
                    .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("warp name", string())
                            .executes(context -> executeSet(context.getSource(), getString(context, "warp name")))))
                .then(literal("delete")
                    .requires(source -> source.hasPermissionLevel(2))
                        .then(argument("warp name", string())
                            .executes(context -> executeDelete(context.getSource(), getString(context, "warp name")))));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeWarp(ServerCommandSource source, String warpName) {
        if (source.getEntity() == null) {
            source.sendError(new TranslatableText("permissions.requires.player"));
            return Command.SINGLE_SUCCESS;
        }
        Entity entity = source.getEntity();
        WarpManager warpManager = OriginsUtils.INSTANCE.getWarpManager();
        Warp warp = warpManager.getWarp(warpName);
        if (warp == null) {
            source.sendError(new LiteralText("Warp " + warpName + " does not exist!"));
            return Command.SINGLE_SUCCESS;
        }
        warp.getLocation().teleport(entity);
        source.sendFeedback(new LiteralText("Warped to " + warpName), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeList(ServerCommandSource source) {
        WarpManager warpManager = OriginsUtils.INSTANCE.getWarpManager();
        StringBuilder stringBuilder = new StringBuilder();
        for (Warp warp : warpManager.getWarps()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ").append(warp.getName());
                continue;
            }
            stringBuilder.append(warp.getName());
        }
        if (stringBuilder.length() == 0) {
            source.sendFeedback(new LiteralText("There are no warps defined"), false);
            return Command.SINGLE_SUCCESS;
        }
        source.sendFeedback(new LiteralText("List of warps: " + stringBuilder), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSet(ServerCommandSource source, String warpName) {
        if (source.getEntity() == null) {
            source.sendError(new TranslatableText("permissions.requires.player"));
            return Command.SINGLE_SUCCESS;
        }
        Entity entity = source.getEntity();
        WarpManager warpManager = OriginsUtils.INSTANCE.getWarpManager();
        if (warpManager.getWarp(warpName) != null) {
            source.sendError(new LiteralText("Warp " + warpName + " already exists!"));
            return Command.SINGLE_SUCCESS;
        }
        warpManager.addWarp(new Warp(warpName, new Location((ServerWorld) entity.world, entity.getX(), entity.getY(), entity.getZ())));
        source.sendFeedback(new LiteralText("Added warp named " + warpName), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeDelete(ServerCommandSource source, String warpName) {
        WarpManager warpManager = OriginsUtils.INSTANCE.getWarpManager();
        if (warpManager.getWarp(warpName) == null) {
            source.sendError(new LiteralText("Warp " + warpName + " does not exist!"));
            return Command.SINGLE_SUCCESS;
        }
        Warp warp = warpManager.getWarp(warpName);
        warpManager.deleteWarp(warp);
        source.sendFeedback(new LiteralText("Deleted warp named " + warpName), true);
        return Command.SINGLE_SUCCESS;
    }
}
