package net.tjalp.originsutils.manager;

import com.google.gson.JsonObject;
import net.tjalp.originsutils.OriginsUtils;
import net.tjalp.originsutils.object.Location;
import net.tjalp.originsutils.util.DataHandler;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class HomeManager {

    public static Location getFromUuid(UUID uuid) {
        if (getHomesFromFile() == null) return null;
        JsonObject uuidObject = getHomesFromFile().getAsJsonObject(uuid.toString());
        return Location.getFromJsonObject(uuidObject);
    }

    public static JsonObject getHomesFromFile() {
        try {
            return DataHandler.readData(new File(OriginsUtils.DATA_DIRECTORY + File.separator + "homes.json"));
        } catch (IOException e) {
            System.out.println("OriginsUtils: failed to read homes from homes.json: " + e.getMessage());
        }
        return null;
    }

    public static void setHome(UUID uuid, Location location) {
        if (getHomesFromFile() == null) return;
        JsonObject object = getHomesFromFile();
        object.add(uuid.toString(), location.toJsonObject());
        saveHomes(object);
    }

    public static void saveHomes(JsonObject jsonObject) {
        try {
            DataHandler.writeData(new File(OriginsUtils.DATA_DIRECTORY + File.separator + "homes.json"), jsonObject);
        } catch (IOException e) {
            System.out.println("OriginsUtils: failed to write homes to homes.json: " + e.getMessage());
        }
    }
}
