package ru.spbau.nonograms.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ru.spbau.nonograms.logic.NonogramImage;

/**
 * Implements interaction with server.
 */
public class ClientManager {

    /**
     * Loads nonogram to server.
     * @param nonogram specified nonogram
     * @return {@code true} if nonogram was loaded to server successfully;
     * {@code false} otherwise
     */
    public static boolean loadNonogram(NonogramImage nonogram) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = createJsonObject("loadNonogram");
        json.getAsJsonObject("data").addProperty("name", "default");
        json.getAsJsonObject("data").addProperty("author", "default");
        json.getAsJsonObject("data").addProperty("height", nonogram.getHeight());
        json.getAsJsonObject("data").addProperty("width", nonogram.getWidth());
        json.getAsJsonObject("data").add("data", gson.toJsonTree(nonogram));

        String response = sendToServer("loadNonogram", json.toString());
        return response != null && processResponse("loadNonogram", response) != null;
    }

    /**
     * Downloads nonogram with specified id from server.
     * @param id id of the nonogram
     * @return nonogram image with specified id or null if error occurred
     */
    public static NonogramImage getNonogramById(int id) {
        JsonObject json = createJsonObject("getNonogramById");
        json.getAsJsonObject("data").addProperty("id", id);

        String response = sendToServer("getNonogramById", json.toString());
        if (response != null) {
            JsonElement data = processResponse("getNonogramById", response);
            Gson gson = new GsonBuilder().create();
            return data == null ? null : gson.fromJson(data.toString(), NonogramImage.class);
        }
        return null;
    }

    /**
     * Checks if nonogram is solvable or not.
     * @param nonogram specified nonogram
     * @return {@code true} if nonogram is solvable; {@code false} otherwise
     */
    public static boolean solveNonogram(NonogramImage nonogram) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = createJsonObject("solveNonogram");
        json.add("data", gson.toJsonTree(nonogram));

        String response = sendToServer("solveNonogram", json.toString());
        if (response != null) {
            JsonElement data = processResponse("solveNonogram", response);
            return data != null && data.getAsBoolean();
        }
        return false;
    }

    private static String sendToServer(String fromMethod, String data) {
        Logger.getGlobal().logp(Level.INFO, "ClientManager", fromMethod, "Processing...");
        try {
            return Client.send(data);
        } catch (IOException err) {
            Logger.getGlobal().logp(Level.WARNING, "ClientManager", fromMethod, "Failed");
            return null;
        }
    }

    /**
     * Transforms response to json object.
     * @param fromMethod method this function was executed from
     * @param response response from server
     * @return response as json object or {@code null} if error occurred
     */
    private static JsonElement processResponse(String fromMethod, String response) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(response, JsonObject.class);
        if (json.get("response").toString().equals("fail")) {
            Logger.getGlobal().logp(Level.WARNING, "ClientManager", fromMethod,
                    json.get("desc").toString());
            return null;
        }
        return json.get("data");
    }

    /**
     * Creates json with specified query type to send to server.
     * All data should be added separately.
     * @param type type of json query
     * @return json with specified type
     */
    private static JsonObject createJsonObject(String type) {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        json.add("data", new JsonObject());
        return json;
    }

}
