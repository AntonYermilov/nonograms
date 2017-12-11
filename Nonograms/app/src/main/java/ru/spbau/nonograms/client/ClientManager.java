package ru.spbau.nonograms.client;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ru.spbau.nonograms.logic.NonogramImage;

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
        System.out.println(json.toString());

        String response = Client.send(json.toString());
        if (response == null) {
            return false;
        }

        json = gson.fromJson(response, JsonObject.class);
        if (json.get("response").toString().equals("fail")) {
            Log.e("Client::loadNonogram", json.get("desc").toString());
            return false;
        }
        Log.i("Client::loadNonogram", "nonogram was successfully loaded to server");
        return true;
    }

    /**
     * Downloads nonogram with specified id from server.
     * @param id id of the nonogram
     * @return nonogram image with specified id or null if error occurred
     */
    public static NonogramImage getNonogramById(int id) {
        JsonObject json = createJsonObject("getNonogramById");
        json.getAsJsonObject("data").addProperty("id", id);

        String response = Client.send(json.toString());
        System.out.println(response);
        if (response == null) {
            return null;
        }

        Gson gson = new GsonBuilder().create();
        json = gson.fromJson(response, JsonObject.class);
        if (json.get("response").toString().equals("fail")) {
            Log.e("Client::getNonogramById", json.get("desc").toString());
            return null;
        }
        NonogramImage nonogram = gson.fromJson(json.get("data").getAsString(), NonogramImage.class);
        Log.i("Client::getNonogramById", "nonogram was successfully received from server");
        return nonogram;
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
