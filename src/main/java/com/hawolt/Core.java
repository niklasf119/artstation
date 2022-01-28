package com.hawolt;

import java.util.concurrent.Executors;

/**
 * Created: 28.01.2022 08:49
 * Author: Twitter @hawolt
 **/

public class Core {
    public static void main(String[] args) {
        SocketServer server = new SocketServer(Integer.parseInt(args[0]));
        Executors.newSingleThreadExecutor().execute(server);
    }
}
