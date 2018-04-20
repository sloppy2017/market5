package org.sanchain.client.ws;

/**
 * Created by A
 * since 14/12/25.
 */

import org.sanchain.client.Config;
import org.sanchain.client.api.exception.APIException;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongUnaryOperator;

/**
 * Websocket client based on Netty.
 * switch server_type
 * simple: node that do not contains tx database;
 * full: node that contains tx database;
 * backup: node which is the backup server
 */
public class CoralWebSocketClient {
    private static final Logger logger = LogManager.getLogger(CoralWebSocketClient.class);

    public static final ConcurrentHashMap<Long, BlockingQueue<String>> queues = new ConcurrentHashMap<>();
    public static final BlockingQueue<String> subscribeQueue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);

    private static final ConcurrentMap<String, WSClient> clientMap = new ConcurrentHashMap<>();
    private static final AtomicLong requestID = new AtomicLong(100L);



    public static enum WebsocketServerType {
        SIMPLE("simple"),
        FULL("full"),
        BACKUP("backup");

        private String type;

        WebsocketServerType(String type) {
            this.type = type;
        }

        public String type() {
            return this.type;
        }
    }

    public static void updateClient(WSClient client) {
        clientMap.get(client.getType()).setStatus(client.getStatus());
    }

    static {
        String simpleServer = Config.getInstance().getServer(WebsocketServerType.SIMPLE.type());
        String fullServer = Config.getInstance().getServer(WebsocketServerType.FULL.type());
        String backupServer = Config.getInstance().getServer(WebsocketServerType.BACKUP.type());
        logger.info("load server from properties: simple=" + simpleServer + ", full=" + fullServer + ", backup=" + backupServer);
        initServer(WebsocketServerType.SIMPLE, simpleServer);
        initServer(WebsocketServerType.FULL, fullServer);
        initServer(WebsocketServerType.BACKUP, backupServer);

        WebSocketWatcher watchDog = new WebSocketWatcher();
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(watchDog, 0, 5, TimeUnit.SECONDS);
    }

    public static Collection<WSClient> clients(){
        return Collections.unmodifiableCollection(clientMap.values());
    }

    /**
     * initialize a websocket server
     *
     * @param type   server type, may be 1 of the 3 values: simple, full, backup
     * @param server server url
     */
    public static void initServer(WebsocketServerType type, String server) {
        try {
            URI uri = new URI(server);
            WSClient wsClient = new WSClient(uri, type.type());
            wsClient.connect();
            clientMap.put(type.type(), wsClient);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static void initServer(String type, URI uri){
        try {
            WSClient wsClient = new WSClient(uri, type);
            wsClient.connect();
            clientMap.put(type, wsClient);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Send requests to simple server, if server if offline, then use backup;
     *
     * @param data data to send
     * @return websocket result
     * @throws APIException
     */
    public static String request(String data) throws APIException {
        WSClient client = clientMap.get(WebsocketServerType.SIMPLE.type);
        if (client.getStatus() == 1) {
            return request(WebsocketServerType.SIMPLE, data);
        } else {
            client = clientMap.get(WebsocketServerType.BACKUP.type);
            if (client.getStatus() == 1)
                return request(WebsocketServerType.BACKUP, data);
            else{
                client = clientMap.get(WebsocketServerType.FULL.type);
                if(client.getStatus() == 1){
                    return request(WebsocketServerType.FULL, data);
                }
            }
        }
        throw new APIException(APIException.ErrorCode.REMOTE_ERROR, "No websocket server available.");
    }


    public static String request(WebsocketServerType serverType, String data) throws APIException {
        logger.info("request server ID:" + serverType.type());
        String serverId = serverType.type();
        Long requestId = requestID.getAndUpdate(new Counter());

        Long currentDataRequestId;
        try {
            JSONObject json = new JSONObject(data);
            currentDataRequestId = json.getLong("id");
            json.put("id", requestId);
            data = json.toString();
        } catch (Exception ex) {
            throw new APIException(APIException.ErrorCode.MALFORMED_REQUEST_DATA, "Invalid \"id\" property, must be a number");
        }
        queues.put(requestId, new LinkedBlockingQueue<>(1));
        WSClient client = clientMap.containsKey(serverId) ? clientMap.get(serverId) : clientMap.get(WebsocketServerType.SIMPLE.type);


        String result = null;
        Channel channel = client.getChannel();
        try {
            if (channel == null || !channel.isActive() || !channel.isOpen()) {
                channel = client.connect();
            }
            WebSocketFrame frame = new TextWebSocketFrame(data);
            channel.writeAndFlush(frame);
            result = WebSocketClientHandler.getMessage(requestId);
        }catch (Exception ex){
        }

        if (result == null) {
            client.updateStatus();
            logger.info("request data:"+data);
            throw new APIException(APIException.ErrorCode.REMOTE_ERROR, "websocket error on reponse, server:" + client.getUri());
        }
        JSONObject json = new JSONObject(result);
        json.put("id", currentDataRequestId);
        if (json.has("status") && !json.getString("status").equals("error"))
            return json.toString();
        else {
            String error = json.getString("error");
            if ("actNotFound".equals(error))
                throw new APIException(APIException.ErrorCode.ADDRESS_NOT_FOUND, error);
            else if ("noCurrent".equals(error) || "noNetwork".equals(error)) {
                client.updateStatus();
                throw new APIException(APIException.ErrorCode.REMOTE_ERROR, error);
            } else {
                logger.info("error request:"+json);
                throw new APIException(APIException.ErrorCode.UNKNOWN_ERROR, error);
            }
        }
    }

    /**
     * Counter for atomic, if id is 1M, then restart from 100;
     */
    private static class Counter implements LongUnaryOperator {
        @Override
        public long applyAsLong(long operand) {
            if(operand > 999999){
                return 100;
            }else{
                return ++operand;
            }
        }
    }

    @Test
    public void testEnum() {
        for(int i=0;i<1000;i++) {
            System.out.println(requestID.getAndUpdate(new Counter()));
        }
        System.out.printf(WebsocketServerType.BACKUP.type());
    }

}
