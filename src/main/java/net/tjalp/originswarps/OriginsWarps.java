package net.tjalp.originswarps;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.tjalp.originswarps.command.SetWarpCommand;
import net.tjalp.originswarps.command.WarpCommand;
import net.tjalp.originswarps.manager.WarpManager;
import net.tjalp.originswarps.util.DataHandler;

import java.io.File;
import java.io.IOException;

public class OriginsWarps implements ClientModInitializer, DedicatedServerModInitializer {

	public static OriginsWarps INSTANCE;
	public static final String MOD_ID = "originswarps";

	private final WarpManager warpManager = new WarpManager();
	private final DataHandler dataHandler = new DataHandler(new File(FabricLoader.getInstance().getConfigDir() + File.separator + OriginsWarps.MOD_ID + File.separator + "warps.json").toPath());
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
				warpManager.importFromJsonObject(getDataHandler().readData());
			} catch (IOException e) {
				System.out.println("OriginsWarps: failed to import warps from warps.json: " + e.getMessage());
			}
		});
	}

	public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		SetWarpCommand.register(dispatcher);
		WarpCommand.register(dispatcher);
	}

	public WarpManager getWarpManager() {
		return this.warpManager;
	}

	public DataHandler getDataHandler() {
		return this.dataHandler;
	}

	public MinecraftServer getServer() {
		return this.server;
	}
}
