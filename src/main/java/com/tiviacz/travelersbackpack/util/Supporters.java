package com.tiviacz.travelersbackpack.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Supporters {
    public static final List<String> SUPPORTERS_REFERENCE = new ArrayList<>();
    public static final List<String> SUPPORTERS = new ArrayList<>();

    public static void fetchSupporters() {
        getGistFileAsync().thenAccept(fetchedContents -> {
            if(fetchedContents.startsWith("Fail")) {
                return;
            }
            fetchedContents = fetchedContents.replace("\n", "");
            String[] names = fetchedContents.split(",");
            SUPPORTERS.clear();
            SUPPORTERS.addAll(Arrays.asList(names));
            SUPPORTERS_REFERENCE.clear();
            SUPPORTERS_REFERENCE.addAll(Arrays.asList(names));
        });
    }

    public static void updateSupporters() {
        getGistFileAsync().thenAccept(fetchedContents -> {
            if(fetchedContents.startsWith("Fail")) {
                return;
            }
            fetchedContents = fetchedContents.replace("\n", "");
            String[] names = fetchedContents.split(",");
            SUPPORTERS_REFERENCE.clear();
            SUPPORTERS_REFERENCE.addAll(Arrays.asList(names));
        });
    }

    public static CompletableFuture<String> getGistFileAsync() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://gist.githubusercontent.com/Tiviacz1337/b27d7acf7c50e5dbfb716608b31ebfe4/raw/Supporters"))
                .GET()
                .build();

        // Fetch file content asynchronously on the server thread
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response.body(); // Return the content
            } catch(Exception e) {
                //e.printStackTrace();
                return "Fail"; // Return fail if error occurs
            }
        });
    }
}