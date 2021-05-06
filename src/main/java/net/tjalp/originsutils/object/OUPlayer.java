package net.tjalp.originsutils.object;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.world.GameMode;
import net.tjalp.originsutils.OriginsUtils;
import net.tjalp.originsutils.util.DataHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OUPlayer {

    private static final Map<UUID, OUPlayer> ouPlayerMap = new HashMap<>();

    public static OUPlayer getFromUuid(UUID uuid) {
        return ouPlayerMap.get(uuid);
    }

    private final UUID uuid;
    private boolean inShop = false;
    private int coins = 0;

    public OUPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public OUPlayer register() {
        ouPlayerMap.put(uuid, this);
        return this;
    }

    public OUPlayer load() {
        try {
            File coinsFile = new File(OriginsUtils.DATA_DIRECTORY + File.separator + "coins.json");
            JsonObject coinsObject = DataHandler.readData(coinsFile);
            if (coinsObject.get(uuid.toString()) == null) {
                System.out.println("No account was found when looking for coins");
                setCoins(this.coins, true);
                coinsObject = DataHandler.readData(coinsFile);
            }
            this.coins = coinsObject.get(uuid.toString()).getAsInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setCoins(int coins, boolean save) {
        this.coins = coins;
        if (save) {
            File coinsFile = new File(OriginsUtils.DATA_DIRECTORY + File.separator + "coins.json");
            try {
                JsonObject coinsObject = DataHandler.readData(coinsFile);
                coinsObject.addProperty(uuid.toString(), coins);
                DataHandler.writeData(coinsFile, coinsObject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInShop(boolean inShop, boolean executeEvents) {
        this.inShop = inShop;
        if (executeEvents) executeEvents();
    }

    private void executeEvents() {
        ServerPlayerEntity player = OriginsUtils.INSTANCE.getServer().getPlayerManager().getPlayer(this.uuid);
        if (player == null) return;
        ServerPlayerInteractionManager interactionManager = player.interactionManager;

        if (this.inShop) {
            if (interactionManager.getGameMode() == GameMode.SURVIVAL) {
                player.setGameMode(GameMode.ADVENTURE);
            }
            return;
        }

        if (interactionManager.getGameMode() == GameMode.ADVENTURE) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    public boolean isInShop() {
        return this.inShop;
    }

    public int getCoins() {
        return this.coins;
    }
}
