package net.tjalp.originsutils.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.tjalp.originsutils.object.OUPlayer;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CoinsCommand {

    private static final DecimalFormat numberFormatter = new DecimalFormat("#,###");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("coins")
                .then(literal("give")
                    .then(argument("target", player())
                        .then(argument("amount", integer())
                            .executes(context -> giveCoins(context.getSource(), getPlayer(context, "target"), getInteger(context, "amount"))))))
                .then(literal("set")
                    .then(argument("target", player())
                        .then(argument("amount", integer())
                            .executes(context -> setCoins(context.getSource(), getPlayer(context, "target"), getInteger(context, "amount"))))))
                .then(literal("take")
                    .then(argument("target", player())
                        .then(argument("amount", integer())
                            .executes(context -> takeCoins(context.getSource(), getPlayer(context, "target"), getInteger(context, "amount"))))))
                .then(literal("get")
                    .then(argument("target", player())
                        .executes(context -> getCoins(context.getSource(), getPlayer(context, "target")))));

        dispatcher.register(literalArgumentBuilder);
    }

    private static int giveCoins(ServerCommandSource source, ServerPlayerEntity target, int amount) {
        OUPlayer ouPlayer = OUPlayer.getFromUuid(target.getUuid());
        ouPlayer.setCoins(ouPlayer.getCoins() + amount, true);
        source.sendFeedback(new LiteralText("Gave " + numberFormatter.format(amount) + " coin(s) to " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int setCoins(ServerCommandSource source, ServerPlayerEntity target, int amount) {
        OUPlayer ouPlayer = OUPlayer.getFromUuid(target.getUuid());
        ouPlayer.setCoins(amount, true);
        source.sendFeedback(new LiteralText("Set " + target.getName().getString() + "'s coins to " + numberFormatter.format(amount)), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int takeCoins(ServerCommandSource source, ServerPlayerEntity target, int amount) {
        OUPlayer ouPlayer = OUPlayer.getFromUuid(target.getUuid());
        ouPlayer.setCoins(ouPlayer.getCoins() - amount, true);
        source.sendFeedback(new LiteralText("Took " + numberFormatter.format(amount) + " coin(s) from " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int getCoins(ServerCommandSource source, ServerPlayerEntity target) {
        OUPlayer ouPlayer = OUPlayer.getFromUuid(target.getUuid());
        source.sendFeedback(new LiteralText(target.getName().getString() + " has " + numberFormatter.format(ouPlayer.getCoins()) + " coin(s)"), false);
        return Command.SINGLE_SUCCESS;
    }
}
