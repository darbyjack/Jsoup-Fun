package me.glaremasters;

import com.google.gson.reflect.TypeToken;
import net.reflxction.simplejson.json.JsonFile;
import net.reflxction.simplejson.json.JsonReader;
import net.reflxction.simplejson.json.JsonWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by GlareMasters
 * Date: 1/25/2019
 * Time: 12:54 AM
 */
public class JsoupFun {

    private static JsonFile jsonFile;
    private static JsonReader jsonReader;
    private static JsonWriter jsonWriter;

    /**
     * Create the json file that we will be using
     * @throws IOException
     */
    private static void init() throws IOException {
        File data = new File("data.json");
        jsonFile = new JsonFile(data);
        jsonReader = new JsonReader(jsonFile);
        jsonWriter = new JsonWriter(jsonFile);
    }

    /**
     * Load the data from the json file
     * @param reader the json reader
     * @return map of data
     * @throws IOException
     */
    private static Map<String, SpigotObject> loadPlugin(JsonReader reader) throws IOException {
        Map<String, SpigotObject> loadedPlugins = reader.deserialize("list", new TypeToken<Map<String, SpigotObject>>(){}.getType());
        reader.close();
        return loadedPlugins;
    }

    /**
     * Save the data to the json file
     * @param plugins the list of data
     * @param writer the json writer
     * @throws IOException
     */
    private static void savePlugin(Map<String, SpigotObject> plugins, JsonWriter writer) throws IOException {
        writer.add("list", plugins, true, true);
    }

    public static void main(String[] args) throws IOException {

        init();

        Map<String, SpigotObject> pluginList = loadPlugin(jsonReader) == null ? new HashMap<>() : loadPlugin(jsonReader);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Input Plugin ID: ");
        String num = scanner.next();

        Document doc = Jsoup.connect(Constants.URL.replace("{id}", num)).userAgent(Constants.USER_AGENT).get();

        SpigotObject.SpigotObjectBuilder sob = SpigotObject.builder();

        create(sob, doc);

        SpigotObject so = sob.build();
        pluginList.put(num, so);
        savePlugin(pluginList, jsonWriter);
    }

    /**
     * Creates the object builder
     * @param sob the object builder
     * @param doc jsoup document
     */
    private static void create(SpigotObject.SpigotObjectBuilder sob, Document doc) {
        sob.name(doc.select(Constants.NAME).text());
        sob.author(doc.select(Constants.AUTHOR).text());
        sob.downloads(doc.select(Constants.DOWNLOADS).text());
    }

}
