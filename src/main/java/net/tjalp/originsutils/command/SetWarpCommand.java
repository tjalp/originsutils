package net.tjalp.originswarps.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.tjalp.originswarps.OriginsWarps;
import net.tjalp.originswarps.manager.WarpManager;
import net.tjalp.originswarps.object.Location;
import net.tjalp.originswarps.object.Warp;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetWarpCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("setwarp")
                .requires(source -> source.hasPermissionLevel(2))
                    .then(argument("warp name", string())
                        .executes(context -> execute(context.getSource(), getString(context, "warp name"))));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, String warpName) {
        if (source.getEntity() == null) {
            source.sendFeedback(new TranslatableText("permissions.requires.player"), false);
            return Command.SINGLE_SUCCESS;
        }
        Entity entity = source.getEntity();
        WarpManager warpManager = OriginsWarps.INSTANCE.getWarpManager();
        if (warpManager.getWarp(warpName) != null) {
            source.sendError(new LiteralText("Warp " + warpName + " already exists!"));
            return Command.SINGLE_SUCCESS;
        }
        warpManager.addWarp(new Warp(warpName, new Location((ServerWorld) entity.world, entity.getX(), entity.getY(), entity.getZ())));
        source.sendFeedback(new LiteralText("Added warp named " + warpName), true);
        return Command.SINGLE_SUCCESS;
    }
}
