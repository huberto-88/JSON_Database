package client;

import com.beust.jcommander.Parameter;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Request {

    public Request() {
        this.request = "file";
    }

    @Parameter(names = "-t", description = "type of the request")
    private String request;

    @Parameter(names = "-k", description = "some key")
    private String key;

    @Parameter(names = "-v", description = "value to save in the database")
    private String value;

    @Parameter(names = "-in", description = "reading request from file")
    private String fileName;

    /**
     * this method returns input as JSON OBJECT
     * if parameter equals "-in" data is reading from
     * file given in request and returned (expected JSON format)
     */
    public String toJson() {
        Map<String, String> mapToJson = new HashMap<>();
        mapToJson.put("type", request);
        StringBuilder msg = new StringBuilder();

        if (request.equals("get") || request.equals("delete")) {
            mapToJson.put("key", key);
        } else if (request.equals("set")) {
            mapToJson.put("key", key);
            mapToJson.put("value", value);
        } else if (request.equals("exit")) {
            // ignore
        } else {

            File file = new File("src/client/data/" + fileName);
//            File file = new File("C:\\Users\\hmich\\IdeaProjects\\JSON Database\\JSON Database\\task\\src\\client\\data\\testDelete.json");

            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNext()) {
                    msg.append(scanner.nextLine());
                }
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
            }
        }

        /**
         * if msg is empty it means that input from command line
         * has to be parsed to JSON form
         */
        if (msg.length() == 0) {
            Gson gson = new Gson();
            msg.append(gson.toJson(mapToJson));
        }
        return msg.toString();
    }
}

