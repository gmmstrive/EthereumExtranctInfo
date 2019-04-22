package com.gikee.ethereum.config;

import com.neovisionaries.ws.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebSocketConfig {

    private static final int DEFAULT_SOCKET_CONNECTTIMEOUT = 3000;
    private static final int FRAME_QUEUE_SIZE = 5;
    private static WebSocket ws = null;

    @Value(value = "${websocket.url}")
    private String wsUrl;

    public WebSocketConfig() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (ws != null) {
                        ws.sendText("keepalive");
                    }
                    try {
                        Thread.sleep(1000 * 30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    @Bean
    public WebSocket getWebSocket() {
        try {
            ws = new WebSocketFactory().setConnectionTimeout(DEFAULT_SOCKET_CONNECTTIMEOUT)
                    .createSocket(wsUrl)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)
                    .setMissingCloseFrameAllowed(false)
                    .addListener(new SocketListener())
                    .connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ws;
    }

    class SocketListener extends WebSocketAdapter {

        /**
         * 连接成功
         *
         * @param websocket
         * @param headers
         * @throws Exception
         */
        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
            log.info("WebSocket onConnected");
        }

        /**
         * 连接失败
         *
         * @param websocket
         * @param exception
         * @throws Exception
         */
        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception) {
            log.info("WebSocket onConnectError");
            try {
                ws = null;
                Thread.sleep(20000);
                ws = getWebSocket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * @param websocket
         * @param serverCloseFrame
         * @param clientCloseFrame
         * @param closedByServer
         * @throws Exception
         */
        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
            log.info("WebSocket onDisconnected");
            ws = getWebSocket();
        }

        /**
         * 返回的消息
         *
         * @param websocket
         * @param text
         */
        @Override
        public void onTextMessage(WebSocket websocket, String text) {
            log.info("WebSocket onTextMessage : {}", text);
        }

    }

}
