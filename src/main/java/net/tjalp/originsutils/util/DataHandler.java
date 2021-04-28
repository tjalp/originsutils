package net.tjalp.originsutils.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class DataHandler {

    private final Path dataPath;

    public DataHandler(Path path) {
        this.dataPath = path;
    }

    public JsonObject readData() throws IOException {
        File dataFile = dataPath.toFile();
        FileReader fileReader = new FileReader(dataFile);
        return new JsonParser().parse(fileReader).getAsJsonObject();
    }

    public void writeData(JsonObject data) throws IOException {
        File dataFile = dataPath.toFile();
        dataFile.mkdirs();
        if (!dataFile.exists()) dataFile.createNewFile();
        FileWriter fileWriter = new FileWriter(dataFile);
        fileWriter.write(data.toString());
        fileWriter.flush();
        fileWriter.close();
    }
}
