package com.hawolt;

import com.hawolt.data.Artwork;
import com.hawolt.data.Profile;
import com.hawolt.logging.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: 28.01.2022 08:56
 * Author: Twitter @hawolt
 **/

public class Cache {

    private final static Map<String, String> INTERNAL_CACHE = new HashMap<>();

    public static boolean has(String username) {
        return INTERNAL_CACHE.containsKey(username);
    }

    public static void update(String username, String hash) {
        INTERNAL_CACHE.put(username, hash);
    }

    public static String latest(String username) {
        return INTERNAL_CACHE.getOrDefault(username, "null");
    }

    public static void filter(Profile profile) {
        try {
            String latest = latest(profile.getUsername());
            for (int i = 0; i < profile.getCount(); i++) {
                Artwork artwork = profile.at(i);
                if (artwork == null) continue;
                boolean match = latest.equals(artwork.getHash());
                if (match) {
                    for (int j = profile.getCount() - 1; j >= i; j--) {
                        profile.remove(profile.at(j));
                    }
                    break;
                }
            }
            if (profile.getCount() > 0) Cache.update(profile.getUsername(), profile.getLatestHash());
        } catch (Exception e) {
            Logger.error(e);
        }
    }
}
