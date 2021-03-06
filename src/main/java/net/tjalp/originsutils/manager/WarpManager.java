package net.tjalp.originsutils.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.tjalp.originsutils.OriginsUtils;
import net.tjalp.originsutils.object.Location;
import net.tjalp.originsutils.object.Warp;
import net.tjalp.originsutils.util.DataHandler;

import java.io.File;
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
        if (save) save();
    }

    public void deleteWarp(Warp warp) {
        deleteWarp(warp, true);
    }

    public void deleteWarp(Warp warp, boolean save) {
        this.warpMap.remove(warp.getName());
        if (save) save();
    }

    public void save() {
        try {
            DataHandler.writeData(new File(OriginsUtils.DATA_DIRECTORY + File.separator + "warps.json"), exportToJsonObject());
        } catch (IOException e) {
            System.out.println("OriginUtils: failed to save warps: " + e.getMessage());
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
            addWarp(new Warp(name, Location.getFromJsonObject(jsonElement.getAsJsonObject())), false);
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
            warpJsonObject.addProperty("pitch", location.getPitch());
            warpJsonObject.addProperty("yaw", location.getYaw());
            jsonArray.add(warpJsonObject);
        }
        jsonObject.add("warps", jsonArray);
        return jsonObject;
    }
}
