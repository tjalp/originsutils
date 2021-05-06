package net.tjalp.originsutils.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataHandler {

    public static JsonObject readData(File file) throws IOException {
        FileReader fileReader = new FileReader(file);
        return new JsonParser().parse(fileReader).getAsJsonObject();
    }

    public static void writeData(File file, JsonObject data) throws IOException {
        file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(data.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
