package com.hawolt.data;

import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 28.01.2022 08:49
 * Author: Twitter @hawolt
 **/

public class Artwork {

    private static final Pattern PATTERN = Pattern.compile("https://cdn.?\\.artstation\\.com/p/assets/images/images/[0-9]{3}/[0-9]{3}/[0-9]{3}/([0-9]{14})/4k/(.*)");

    private final String username, resource, hash, link;

    public static Artwork create(String username, JSONObject meta) {
        return new Artwork(username, meta);
    }

    private Artwork(String username, JSONObject meta) {
        JSONObject cover = meta.getJSONObject("cover");
        this.hash = meta.getString("hash_id");
        this.link = meta.getString("permalink");
        String base = cover.getString("micro_square_image_url");
        String resource = base.replace("/micro_square/", "/4k/");
        Matcher matcher = PATTERN.matcher(resource);
        if (matcher.find()) {
            resource = resource.replace(matcher.group(1) + "/", "");
        }
        resource = resource.substring(0, resource.lastIndexOf('?'));
        this.resource = resource;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getResource() {
        return resource;
    }

    public String getHash() {
        return hash;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artwork artwork = (Artwork) o;
        return Objects.equals(username, artwork.username) && Objects.equals(resource, artwork.resource) && Objects.equals(hash, artwork.hash) && Objects.equals(link, artwork.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, resource, hash, link);
    }

    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("artist", username);
        object.put("resource", resource);
        object.put("link", link);
        return object;
    }
}
