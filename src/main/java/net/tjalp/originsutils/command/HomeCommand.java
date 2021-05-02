package net.tjalp.originsutils.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.tjalp.originsutils.manager.HomeManager;
import net.tjalp.originsutils.object.Location;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("home")
                .executes(context -> execute(context.getSource()))
                .then(literal("set")
                    .executes(context -> executeSet(context.getSource())));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source) {
        if (source.getEntity() == null || source.getEntity().getType() != EntityType.PLAYER) {
            source.sendError(new TranslatableText("permissions.requires.player"));
            return Command.SINGLE_SUCCESS;
        }
        PlayerEntity player = (PlayerEntity) source.getEntity();
        Location home = HomeManager.getFromUuid(player.getUuid());
        if (home == null) {
            source.sendError(new TranslatableText("You do not have a home!"));
            return Command.SINGLE_SUCCESS;
        }
        home.teleport(player);
        source.sendFeedback(new LiteralText("Teleported to your home"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeSet(ServerCommandSource source) {
        if (source.getEntity() == null || source.getEntity().getType() != EntityType.PLAYER) {
            source.sendError(new TranslatableText("permissions.requires.player"));
            return Command.SINGLE_SUCCESS;
        }
        PlayerEntity player = (PlayerEntity) source.getEntity();
        HomeManager.setHome(player.getUuid(), new Location((ServerWorld) player.world, player.getX(), player.getY(), player.getZ(), player.pitch, player.yaw));
        source.sendFeedback(new LiteralText("Set home to current location"), true);
        return Command.SINGLE_SUCCESS;
    }
}
