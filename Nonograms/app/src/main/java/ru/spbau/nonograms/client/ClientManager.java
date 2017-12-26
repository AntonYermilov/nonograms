package ru.spbau.nonograms.client;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ru.spbau.nonograms.local_database.CurrentCrosswordState;
import ru.spbau.nonograms.model.Image;

import static ru.spbau.nonograms.local_database.CurrentCrosswordState.ColoredValue;

/**
 * Implements interaction with server.
 */
public class ClientManager {

    private static AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        @Override
        protected String doInBackground(String... params) {
            try {
                return Client.send(params[1]);
            } catch (IOException e) {
                Logger.getGlobal().logp(Level.WARNING, "ClientManager", params[0],
                        "Failed:\n" + e.getMessage());
                return null;
            }
        }
    };

    /**
     * Loads nonogram to server.
     * @param nonogram specified nonogram
     * @return {@code true} if nonogram was loaded to server successfully;
     * {@code false} otherwise
     */
    public static boolean loadNonogram(CurrentCrosswordState nonogram) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = createJsonObject("loadNonogram");
        json.getAsJsonObject("data").addProperty("name", "default");
        json.getAsJsonObject("data").addProperty("author", "default");
        json.getAsJsonObject("data").addProperty("height", nonogram.getHeight());
        json.getAsJsonObject("data").addProperty("width", nonogram.getWidth());
        json.getAsJsonObject("data").add("data", nonogramToJson(nonogram));

        String response = sendToServer("loadNonogram", json.toString());
        return response != null && processResponse("loadNonogram", response) != null;
    }

    /**
     * Downloads nonogram with specified id from server.
     * @param id id of the nonogram
     * @return nonogram image with specified id or null if error occurred
     */
    public static CurrentCrosswordState getNonogramById(int id) {
        JsonObject json = createJsonObject("getNonogramById");
        json.getAsJsonObject("data").addProperty("id", id);

        String response = sendToServer("getNonogramById", json.toString());
        if (response != null) {
            JsonElement data = processResponse("getNonogramById", response);
            if (data != null) {
                return jsonToNonogram(data.getAsJsonObject());
            }
        }
        return null;
    }

    /**
     * Checks if nonogram is solvable or not.
     * @param nonogram specified nonogram
     * @return {@code true} if nonogram is solvable; {@code false} otherwise
     */
    public static boolean solveNonogram(Image nonogram) {
        JsonObject json = createJsonObject("solveNonogram");
        json.add("data", nonogram.toJson());

        String response = sendToServer("solveNonogram", json.toString());
        if (response != null) {
            JsonElement data = processResponse("solveNonogram", response);
            if (data != null) {
                return data.getAsBoolean();
            }
        }
        return false;
    }

    public static Image createNonogram(Image image) {
        JsonObject json = createJsonObject("createNonogram");
        json.add("data", image.toJson());

        String response = sendToServer("createNonogram", json.toString());
        if (response != null) {
            JsonElement data = processResponse("createNonogram", response);
            if (data != null) {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(data.getAsJsonObject().get("image"), Image.class);
            }
        }
        return null;
    }

    /**
     * Sends data to server, return response from it.
     * @param fromMethod method this function was called from
     * @param data data to send
     * @return response from server
     */
    private static String sendToServer(String fromMethod, String data) {
        Logger.getGlobal().logp(Level.INFO, "ClientManager", fromMethod, "Processing...");
        try {
            return task.execute(fromMethod, data).get();
        } catch (InterruptedException | ExecutionException e) {
            Logger.getGlobal().logp(Level.INFO, "ClientManager", fromMethod,
                    "Error when executing async task occurred:\n" + e.getMessage());
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

    /**
     * Transforms nonogram to json.
     * @param nonogram specified nonogram
     * @return json object that describes nonogram configuration
     */
    private static JsonObject nonogramToJson(CurrentCrosswordState nonogram) {
        Gson gson = new GsonBuilder().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("colors", gson.toJsonTree(nonogram.getColors()));
        jsonObject.add("rows", gson.toJsonTree(nonogram.getRows()));
        jsonObject.add("columns", gson.toJsonTree(nonogram.getColumns()));
        //TODO add backgroundColor
        //jsonObject.add("backgroundColor", nonogram.getBackgroundColor());
        return jsonObject;
    }

    /**
     * Transforms json object to nonogram.
     * @param jsonObject specified json object
     * @return nonogram that was stored as json object
     */
    private static CurrentCrosswordState jsonToNonogram(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().create();
        int[] colors = gson.fromJson(jsonObject.get("colors"), int[].class);
        ColoredValue[][] rows = gson.fromJson(jsonObject.get("rows"), ColoredValue[][].class);
        ColoredValue[][] columns = gson.fromJson(jsonObject.get("columns"), ColoredValue[][].class);
        int backgroundColor = jsonObject.get("backgroundColor").getAsInt();
        //TODO add backgroundColor
        //return new CurrentCrosswordState(rows, columns, colors,  null);
        return new CurrentCrosswordState(rows, columns, colors, null);
    }

}
