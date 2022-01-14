package server;

import com.google.gson.JsonElement;

public class Response {

    private String response;
    private String reason;
    private JsonElement value;

    private Response(String response, String reason, JsonElement value) {
        this.response = response;
        this.reason = reason;
        this.value = value;
    }


    static class GetResponse {
        private String response;
        private String reason;
        private JsonElement value;

        public GetResponse(String response) {
            this.response = response;
        }

        public GetResponse setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public GetResponse setValue(JsonElement value) {
            this.value = value;
            return this;
        }

        public Response build() {
            return new Response(response, reason, value);
        }
    }

}