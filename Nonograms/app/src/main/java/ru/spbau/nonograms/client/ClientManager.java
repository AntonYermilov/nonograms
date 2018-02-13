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
import ru.spbau.nonograms.model.NonogramPreview;

import static ru.spbau.nonograms.local_database.CurrentCrosswordState.ColoredValue;

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
    public static boolean saveNonogram(CurrentCrosswordState nonogram, String name, String author) {
        Gson gson = new GsonBuilder().create();
        JsonObject json = createJsonObject("saveNonogram");
        json.getAsJsonObject("data").addProperty("name", name);
        json.getAsJsonObject("data").addProperty("author", author);
        json.getAsJsonObject("data").add("height", gson.toJsonTree(nonogram.getHeight()));
        json.getAsJsonObject("data").add("width", gson.toJsonTree(nonogram.getWidth()));
        json.getAsJsonObject("data").add("data", nonogramToJson(nonogram));

        String response = sendToServer("saveNonogram", json.toString());
        return response != null && processResponse("saveNonogram", response) != null;
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
     * Gets basic info about all nonograms on server.
     * @return array of previews of nonograms that are stored on server
     */
    public static NonogramPreview[] getNonogramPreviewInfo() {
        JsonObject json = createJsonObject("getNonogramPreviewInfo");

        String response = sendToServer("getNonogramPreviewInfo", json.toString());
        if (response != null) {
            JsonElement data = processResponse("getNonogramPreviewInfo", response);
            if (data != null) {
                int size = data.getAsJsonArray().size();
                NonogramPreview[] previews = new NonogramPreview[size];
                for (int i = 0; i < size; i++) {
                    previews[i] = jsonToPreview(data.getAsJsonArray().get(i).getAsJsonObject());
                }
                return previews;
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

    /**
     * Sends image to server and creates nonogram by transforming it.
     * @param image image that we want to create nonogram from
     * @return transformed to nonogram image
     */
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
            return new AsyncTask<String, Void, String>() {
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
            }.execute(fromMethod, data).get();
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
        jsonObject.addProperty("backgroundColor", nonogram.getBackgroundColor());
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
        return new CurrentCrosswordState(rows, columns, colors, backgroundColor, null);
    }

    /**
     * Transforms json object to nonogram preview (basic info about nonogram).
     * @param jsonObject specified json object
     * @return nonogram preview info that was stored as json object
     */
    private static NonogramPreview jsonToPreview(JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String author = jsonObject.get("author").getAsString();
        int height = jsonObject.get("height").getAsInt();
        int width = jsonObject.get("width").getAsInt();
        return new NonogramPreview(id, name, author, height, width);
    }

}
