package net.tjalp.originsutils;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
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
	public static final Path DATA_DIRECTORY = new File(FabricLoader.getInstance().getConfigDir() + File.separator + OriginsUtils.MOD_ID + File.separator).toPath();

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

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			this.server = server;
			try {
				warpManager.importFromJsonObject(DataHandler.readData(new File(OriginsUtils.DATA_DIRECTORY + File.separator + "warps.json")));
			} catch (IOException e) {
				System.out.println("OriginsUtils: failed to import warps from warps.json: " + e.getMessage());
			}
		});
	}

	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
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
