package com.example.mindmate.service;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    String API_KEY = System.getenv("GROQ_API_KEY");

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String getChatResponse(String userMessage) {
        try {
            OkHttpClient client = new OkHttpClient();

            // create chat request
            JSONArray messages = new JSONArray()
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", userMessage)
                    );

            JSONObject requestData = new JSONObject()
                    .put("model", "llama-3.1-8b-instant")
                    .put("messages", messages);

            RequestBody body = RequestBody.create(
                    requestData.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(GROQ_URL)
                    .header("Authorization", "Bearer " + API_KEY)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                return "⚠️ API failed: " + response.code();
            }

            String json = response.body().string();
            JSONObject resp = new JSONObject(json);

            // Parse according to Groq format with "choices"
            String reply = resp
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return reply;

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Server error: " + e.getMessage();
        }
    }
}
