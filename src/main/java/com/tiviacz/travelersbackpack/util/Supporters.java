package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Supporters {
    public static final List<String> SUPPORTERS_REFERENCE = new ArrayList<>();
    public static final List<String> SUPPORTERS = new ArrayList<>();

    public static void fetchSupporters() {
        String fileContents = "";
        try {
            fileContents = getGistFile();
        } catch(IOException exception) {
            TravelersBackpack.LOGGER.error("Failed to fetch Traveler's Backpack Supporters from Gist!");
        }
        fileContents = fileContents.replace("\n", "").replace(" ", "");
        String[] names = fileContents.split(",");
        SUPPORTERS.clear();
        SUPPORTERS.addAll(Arrays.asList(names));
        SUPPORTERS_REFERENCE.clear();
        SUPPORTERS_REFERENCE.addAll(Arrays.asList(names));
    }

    public static void updateSupporters() {
        String fileContents = "";
        try {
            fileContents = getGistFile();
        } catch(IOException exception) {
            TravelersBackpack.LOGGER.error("Failed to fetch Traveler's Backpack Supporters from Gist!");
        }
        fileContents = fileContents.replace("\n", "").replace(" ", "");
        String[] names = fileContents.split(",");
        SUPPORTERS_REFERENCE.clear();
        SUPPORTERS_REFERENCE.addAll(Arrays.asList(names));
    }

    public static String getGistFile() throws IOException {
        StringBuilder content = new StringBuilder();

        // Create a URL object
        URL url = URI.create("https://gist.githubusercontent.com/Tiviacz1337/b27d7acf7c50e5dbfb716608b31ebfe4/raw/Supporters").toURL();

        // Open connection
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");

        // Check for successful response code
        int responseCode = connection.getResponseCode();
        if(responseCode == 200) { // HTTP OK
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
        } else {
            throw new IOException("Failed to fetch Gist: HTTP response code " + responseCode);
        }

        // Close connection
        connection.disconnect();

        return content.toString();
    }
}
