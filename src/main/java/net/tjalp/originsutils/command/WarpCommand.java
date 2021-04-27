package net.tjalp.originswarps.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.tjalp.originswarps.OriginsWarps;
import net.tjalp.originswarps.manager.WarpManager;
import net.tjalp.originswarps.object.Warp;

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
                    .executes(context -> executeList(context.getSource())));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int executeWarp(ServerCommandSource source, String warpName) {
        if (source.getEntity() == null) {
            source.sendFeedback(new TranslatableText("permissions.requires.player"), false);
            return Command.SINGLE_SUCCESS;
        }
        Entity entity = source.getEntity();
        WarpManager warpManager = OriginsWarps.INSTANCE.getWarpManager();
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
        WarpManager warpManager = OriginsWarps.INSTANCE.getWarpManager();
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
}
