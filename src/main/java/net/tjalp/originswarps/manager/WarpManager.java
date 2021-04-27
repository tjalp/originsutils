package net.tjalp.originswarps.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.tjalp.originswarps.OriginsWarps;
import net.tjalp.originswarps.object.Location;
import net.tjalp.originswarps.object.Warp;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WarpManager {

    private final Map<String, Warp> warpMap = new HashMap<>();

    public void addWarp(Warp warp) {
        addWarp(warp, true);
    }

    public void addWarp(Warp warp, boolean save) {
        this.warpMap.put(warp.getName(), warp);
        if (save) {
            try {
                OriginsWarps.INSTANCE.getDataHandler().writeData(exportToJsonObject());
            } catch (IOException e) {
                System.out.println("OriginWarps: failed to save warps: " + e.getMessage());
            }
        }
    }

    public Collection<Warp> getWarps() {
        return Collections.unmodifiableCollection(this.warpMap.values());
    }

    public Warp getWarp(String name) {
        return this.warpMap.get(name);
    }

    public void importFromJsonObject(JsonObject jsonObject) {
        JsonArray array = jsonObject.get("warps").getAsJsonArray();
        for (JsonElement jsonElement : array) {
            JsonObject jsonWarp = jsonElement.getAsJsonObject();
            String name = jsonWarp.get("name").getAsString();
            String worldName = jsonWarp.get("world").getAsString();
            double x = jsonWarp.get("x").getAsDouble();
            double y = jsonWarp.get("y").getAsDouble();
            double z = jsonWarp.get("z").getAsDouble();
            addWarp(new Warp(name, new Location(OriginsWarps.INSTANCE.getServer().getWorld(RegistryKey.of(Registry.DIMENSION, new Identifier(worldName))), x, y, z)), false);
        }
    }

    public JsonObject exportToJsonObject() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (String warpName : warpMap.keySet()) {
            JsonObject warpJsonObject = new JsonObject();
            Warp warp = warpMap.get(warpName);
            Location location = warp.getLocation();
            warpJsonObject.addProperty("name", warpName);
            warpJsonObject.addProperty("world", location.getWorld().getRegistryKey().getValue().toString());
            warpJsonObject.addProperty("x", location.getX());
            warpJsonObject.addProperty("y", location.getY());
            warpJsonObject.addProperty("z", location.getZ());
            jsonArray.add(warpJsonObject);
        }
        jsonObject.add("warps", jsonArray);
        return jsonObject;
    }
}
