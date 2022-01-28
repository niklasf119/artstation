package com.hawolt;

import com.hawolt.data.Profile;

import javax.net.ssl.HttpsURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created: 28.01.2022 09:09
 * Author: Twitter @hawolt
 **/

public class Crawler {

    private static String readStream(InputStream stream) throws IOException {
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        }
    }

    private static String fetch(String resource) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(resource).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "ArtStationBot-1.0");
        try (InputStream stream = connection.getInputStream()) {
            return readStream(stream);
        }
    }

    public static Profile crawl(String username) throws IOException {
        Profile profile = new Profile(username, fetch("https://www.artstation.com/users/" + username + "/projects.json"));
        if (!Cache.has(username)) Cache.update(username, profile.getLatestHash());
        Cache.filter(profile);
        return profile;
    }
}
