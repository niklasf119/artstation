package com.hawolt;

import com.hawolt.data.Artwork;
import com.hawolt.data.Profile;
import com.hawolt.logging.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created: 28.01.2022 09:01
 * Author: Twitter @hawolt
 **/

public class SocketServer extends WebSocketServer {

    private static final Map<String, List<WebSocket>> SUBSCRIPTIONS = new HashMap<>();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (String username : new ArrayList<>(SUBSCRIPTIONS.keySet())) {
                if (SUBSCRIPTIONS.get(username).isEmpty()) {
                    continue;
                }
                try {
                    Profile profile = Crawler.crawl(username);
                    for (Artwork artwork : profile) {
                        notify(artwork);
                    }
                } catch (IOException e) {
                    Logger.error(e);
                }
            }
        }, TimeUnit.SECONDS.toMillis(5), TimeUnit.MINUTES.toMillis(10), TimeUnit.MILLISECONDS);
    }

    public SocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    public static void notify(Artwork artwork) {
        for (WebSocket socket : new ArrayList<>(SUBSCRIPTIONS.get(artwork.getUsername()))) {
            socket.send(artwork.asJSON().toString());
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        Logger.info("OPEN SESSION");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Logger.info("CLOSE SESSION");
        for (String username : SUBSCRIPTIONS.keySet()) {
            SUBSCRIPTIONS.get(username).remove(webSocket);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        if (s.length() < 2) return;
        char instruction = s.charAt(0);
        String username = s.substring(1);
        switch (instruction) {
            case '+':
                if (!SUBSCRIPTIONS.containsKey(username)) SUBSCRIPTIONS.put(username, new ArrayList<>());
                SUBSCRIPTIONS.get(username).add(webSocket);
                break;
            case '-':
                if (SUBSCRIPTIONS.containsKey(username)) SUBSCRIPTIONS.get(username).remove(webSocket);
                break;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Logger.error(e);
    }

    @Override
    public void onStart() {
        Logger.info("START @ {}", new Date());
    }
}
