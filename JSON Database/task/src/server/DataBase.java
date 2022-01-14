package server;

import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataBase {
    private JsonObject databaseJson;
    private final String OK = "OK";
    private final String ERROR = "ERROR";
    private final String NO_SUCH_KEY = "No such key";


    private final String FILENAME = "src/server/data/db.json";
    // private final String FILENAME = "C:\\Users\\hmich\\IdeaProjects\\JSON Database\\JSON Database\\task\\src\\server\\data\\data.json";
    private final Path PATH = Path.of(FILENAME);


    public DataBase() throws IOException {
        try (Reader reader = Files.newBufferedReader(PATH, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            this.databaseJson = gson.fromJson(reader, JsonObject.class);
        }
    }

    /**
     * retrieving data from database according to key in JSON notation
     *
     * @param jsonElementFromClient should contain key in form: {"key":[value,value, value]}
     *                              where every next value is ascending element
     * @return requested data or response witch cause why retrieving was failed
     */
    public Response getData(JsonElement jsonElementFromClient) {
        // converting request to array of keys
        JsonArray keyArray = jsonElementFromClient.getAsJsonObject().get("key").getAsJsonArray();

        // temporary json object which contain retrieved data or null if data is not founded
        JsonElement answer = databaseJson.getAsJsonObject().get(keyArray.get(0).getAsString());

        // if array contains more than one key, loop starts to delving into array
        for (int i = 1; i < keyArray.size(); i++) {
            if (answer.isJsonObject()) {
                //retrieving next layer of json data if exist or null
                answer = answer.getAsJsonObject().get(keyArray.get(i).getAsString());
                // else means that answer reach "end" of data and found primitive value
            } else {
                // retrieving primitive data if exists or null
                answer = answer.getAsJsonPrimitive();
            }
        }

        // data wasn't founded, such key doesn't exist
        if (answer == null) {
            return new Response.GetResponse(ERROR)
                    .setReason(NO_SUCH_KEY)
                    .build();
        }

        // data was founded and can be returned to user with appropriate response
        return new Response.GetResponse(OK)
                .setValue(answer)
                .build();
    }

    /**
     * adding or updating new data to existing json database
     *
     * @param jsonElementFromClient json element which has to be added to database
     *                              its form contain array of keys in form:
     *                              {"type":"set","key":["value1","value2","value3"],"value":"newValue"}
     *                              it means that value3 has to be update to newValue
     * @return information to user if set was done successfully or failed with reason
     */
    public Response setData(JsonElement jsonElementFromClient) {
        // user send arrays of keys to update database, update method will be invoked
        if (jsonElementFromClient.getAsJsonObject().get("key").isJsonArray()) {
            return update(jsonElementFromClient);
        }

        // adding new data to json database
        // retrieving key value, it will be key of whole data in that jason
        String key = jsonElementFromClient.getAsJsonObject().get("key").getAsString();

        // retrieving data from given crude form (value "key" and "value") is not part of data
        jsonElementFromClient.getAsJsonObject().remove("key");
        JsonElement value = jsonElementFromClient.getAsJsonObject().get("value");

        JsonObject tempObject = new JsonObject();
        // new json with retrieved key and value
        // now data is cleaned from instruction which were not be part of data
        tempObject.add(key, value);
        saveDatabaseInFile(tempObject);
        return new Response.GetResponse(OK)
                .build();

    }

    /**
     * updating existing json database
     *
     * @param jsonElementFromClient contains array of key where consecutive key means deeper part of
     *                              json element. If requested value exist, will be updated, if not
     *                              will be added to database in required place in json structure
     * @return information to user if update was done successfully or failed with reason
     */
    private Response update(JsonElement jsonElementFromClient) {
        JsonArray keyArray = jsonElementFromClient.getAsJsonObject().get("key").getAsJsonArray();
        JsonElement valueToAdd = jsonElementFromClient.getAsJsonObject().get("value");

        // removing and retrieving last key, it will be added after loop
        String keyToAdd = keyArray.remove(keyArray.size() - 1).getAsString();

        // temporary reference to json database. It is required to getting deeper part of json
        // database structure. Impossible to work on original reference because we lost current data in database
        JsonElement tempDatabaseJson = this.databaseJson;

        for (JsonElement key : keyArray) {
            // for each key, if database hasn't key, add this key and new JsonObject
            if (!tempDatabaseJson.getAsJsonObject().has(key.getAsString())) {
                tempDatabaseJson.getAsJsonObject().add(key.getAsString(), new JsonObject());
            }
            // retrieving next layer of jason structure
            tempDatabaseJson = tempDatabaseJson.getAsJsonObject().get(key.getAsString());
        }

        // update requested data
        // keyToAdd and valueToAdd was extracted at the te begin of method
        tempDatabaseJson.getAsJsonObject().add(keyToAdd, valueToAdd);

        // save data into file and return message to user
        saveDatabaseInFile(databaseJson);
        return new Response.GetResponse(OK)
                .build();
    }

    /**
     * @param jsonFromClient contains array of key where consecutive key means deeper part of
     *                       *                              json element. If requested value exist, will be deleted
     * @return information to user if deletion was done successfully or failed with reason
     */
    public Response deleteData(JsonElement jsonFromClient) {

        // temporary reference to json database. It is required to getting deeper part of json
        // database structure. Impossible to work on original reference because we lost current data in database
        JsonElement tempDatabaseJson = this.databaseJson;

        JsonElement keys = jsonFromClient.getAsJsonObject().get("key");
        String toRemove;

        // key is primitive it means user sent only one value
        if (keys.isJsonPrimitive()) {
            toRemove = keys.getAsString();

            // else means user sent array of keys
        } else {
            JsonArray keyArray = keys.getAsJsonArray();

            // retrieve key to delete at the end of method, we cannot do it in method because it will be
            // primitive value we get error
            toRemove = keyArray.remove(keyArray.size() - 1).getAsString();

            for (JsonElement key : keyArray) {
                // searching  required key in database
                if (tempDatabaseJson.getAsJsonObject().has(key.getAsString())) {
                    tempDatabaseJson = tempDatabaseJson.getAsJsonObject().get(key.getAsString());
                }
            }
        }


        // if key was founded, remove it, save on file and sent response to user
        if (tempDatabaseJson.getAsJsonObject().has(toRemove)) {
            tempDatabaseJson.getAsJsonObject().remove(toRemove);

            saveDatabaseInFile(databaseJson);

            return new Response.GetResponse(OK)
                    .build();

            // if key wasn't file, sending response with reason to user
        } else {
            return new Response.GetResponse(ERROR)
                    .setReason(NO_SUCH_KEY)
                    .build();
        }
    }

    private void saveDatabaseInFile(JsonElement jsonElementFromClient) {
        try (Writer writer = Files.newBufferedWriter(PATH, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            gson.toJson(jsonElementFromClient, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
