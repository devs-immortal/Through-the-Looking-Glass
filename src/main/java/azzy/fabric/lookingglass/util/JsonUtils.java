package azzy.fabric.lookingglass.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    private static final JsonParser parser = new JsonParser();

    public static JsonObject fromInputStream(InputStream in) {
        return parser.parse(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))).getAsJsonObject();
    }
}
