package com.hawolt.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 28.01.2022 08:48
 * Author: Twitter @hawolt
 **/

public class Profile implements Iterable<Artwork> {

    private final List<Artwork> list = new ArrayList<>();
    private final String username;

    public Profile(String username, String content) {
        JSONObject object = new JSONObject(content);
        JSONArray data = object.getJSONArray("data");
        for (int i = 0; i < data.length(); i++) {
            JSONObject meta = data.getJSONObject(i);
            list.add(Artwork.create(username, meta));
        }
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public Artwork at(int index) {
        return list.get(index);
    }

    public int getCount() {
        return list.size();
    }

    public void remove(Artwork artwork) {
        list.remove(artwork);
    }

    public String getLatestHash() {
        if (list.isEmpty()) return null;
        return list.get(0).getHash();
    }

    @Override
    public Iterator<Artwork> iterator() {
        return list.iterator();
    }
}
