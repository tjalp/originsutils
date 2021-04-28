package net.tjalp.originsutils.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.tjalp.originsutils.OriginsUtils;

import static net.minecraft.command.argument.ColorArgumentType.color;
import static net.minecraft.command.argument.ColorArgumentType.getColor;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ColorCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("color")
                .then(argument("color", color())
                    .executes(context -> execute(context.getSource(), getColor(context, "color"))));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(ServerCommandSource source, Formatting formatting) {
        if (source.getEntity() == null || source.getEntity().getType() != EntityType.PLAYER) {
            source.sendError(new TranslatableText("permissions.requires.player"));
            return Command.SINGLE_SUCCESS;
        }
        PlayerEntity player = (PlayerEntity) source.getEntity();
        Scoreboard scoreboard = OriginsUtils.INSTANCE.getServer().getScoreboard();
        String name = player.getName().getString();
        Team team = scoreboard.getTeam(name) == null ? scoreboard.addTeam(name) : scoreboard.getTeam(name);
        if (!team.getPlayerList().contains(name)) scoreboard.addPlayerToTeam(name, team);
        team.setColor(formatting);
        source.sendFeedback(new LiteralText("Color changed to " + formatting.getName()), true);
        return Command.SINGLE_SUCCESS;
    }
}
