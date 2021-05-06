package net.tjalp.originsutils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import net.tjalp.originsutils.command.CoinsCommand;
import net.tjalp.originsutils.command.ColorCommand;
import net.tjalp.originsutils.command.HomeCommand;
import net.tjalp.originsutils.command.WarpCommand;
import net.tjalp.originsutils.manager.WarpManager;
import net.tjalp.originsutils.util.DataHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class OriginsUtils implements ClientModInitializer, DedicatedServerModInitializer {

	public static OriginsUtils INSTANCE;
	public static final String MOD_ID = "originsutils";
	public static Path DATA_DIRECTORY = new File(FabricLoader.getInstance().getGameDir() + File.separator + OriginsUtils.MOD_ID + File.separator).toPath();

	private final WarpManager warpManager = new WarpManager();
	private MinecraftServer server;

	@Override
	public void onInitializeClient() {

	}

	@Override
	public void onInitializeServer() {
		start();
	}

	private void start() {
		INSTANCE = this;

		registerEvents();
	}

	public void registerEvents() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			this.server = server;
			setupAfterServer();
		});
		CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> registerCommands(dispatcher)));

		AttackBlockCallback.EVENT.register(new net.tjalp.originsutils.callback.AttackBlockCallback());
		UseBlockCallback.EVENT.register(new net.tjalp.originsutils.callback.UseBlockCallback());
		UseEntityCallback.EVENT.register(new net.tjalp.originsutils.callback.UseEntityCallback());
	}

	private void setupAfterServer() {
		DATA_DIRECTORY = new File(this.server.getSavePath(WorldSavePath.ROOT) + File.separator + MOD_ID).toPath();
		createFiles();
		try {
			warpManager.importFromJsonObject(DataHandler.readData(new File(OriginsUtils.DATA_DIRECTORY + File.separator + "warps.json")));
		} catch (IOException e) {
			System.out.println("OriginsUtils: failed to import warps from warps.json: " + e.getMessage());
		}
	}

	private void createFiles() {
		File warps = new File(OriginsUtils.DATA_DIRECTORY + File.separator + "warps.json");
		if (!warps.exists()) {
			try {
				JsonObject warpsObject = new JsonObject();
				warpsObject.add("warps", new JsonArray());
				DataHandler.writeData(warps, warpsObject);
			} catch (IOException e) {
				System.out.println("OriginsUtils: failed to create warps file: " + e.getMessage());
			}
		}
		File homes = new File(OriginsUtils.DATA_DIRECTORY + File.separator + "homes.json");
		if (!homes.exists()) {
			try {
				DataHandler.writeData(homes, new JsonObject());
			} catch (IOException e) {
				System.out.println("OriginsUtils: failed to create homes file: " + e.getMessage());
			}
		}
		File coins = new File(OriginsUtils.DATA_DIRECTORY + File.separator + "coins.json");
		if (!coins.exists()) {
			try {
				DataHandler.writeData(coins, new JsonObject());
			} catch (IOException e) {
				System.out.println("OriginsUtils: failed to create coins file: " + e.getMessage());
			}
		}
	}

	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		CoinsCommand.register(dispatcher);
		ColorCommand.register(dispatcher);
		HomeCommand.register(dispatcher);
		WarpCommand.register(dispatcher);
	}

	public WarpManager getWarpManager() {
		return this.warpManager;
	}

	public MinecraftServer getServer() {
		return this.server;
	}
}
