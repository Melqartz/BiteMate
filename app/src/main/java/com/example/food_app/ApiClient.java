package com.example.food_app;

import static com.example.food_app.GlobalVariable.baseUrl;

import java.io.IOException;

import okhttp3.*;

public class ApiClient {
    private static final OkHttpClient client = new OkHttpClient();

    public static void callSecureApi(String firebaseIdToken) {
        Request request = new Request.Builder()
                .url(baseUrl + "/api/admin/secure")  // your endpoint
                .addHeader("Authorization", "Bearer " + firebaseIdToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace(); // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    System.out.println("✅ Success: " + responseData);
                } else {
                    System.out.println("❌ Error: " + response.code());
                }
            }
        });
    }
}
