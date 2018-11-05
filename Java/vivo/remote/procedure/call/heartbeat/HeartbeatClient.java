package vivo.remote.procedure.call.heartbeat;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import vivo.remote.procedure.call.client.Client;
import vivo.remote.procedure.call.server.HeartbeatService;
import vivo.util.Ipconfig;

public class HeartbeatClient {
	 
    private String ip = "127.0.0.1";
    private int port = 10000;
    private String nodeID = UUID.randomUUID().toString();
    private boolean isRunning = true;
    private long lastHeartbeat;
    private long heartBeatInterval = 5 * 1000;
 
    public HeartbeatClient(String ip, int port) {
    	this.ip = ip;
    	this.port = port;
    }
    
    public void start() {
        try {
            while (isRunning) {
                long startTime = System.currentTimeMillis();
                Thread.sleep(1000);
                if (startTime - lastHeartbeat > heartBeatInterval) {
                	HeartbeatService service = Client.getRemoteProxyObject(HeartbeatService.class, new InetSocketAddress(ip, port));
                    HeartbeatEntity entity = new HeartbeatEntity();
                    // Map<String, Object> info = new HashMap<String, Object>();
                    // info.put("HostIp", Ipconfig.getHostIpAddress());
                    // info.put("MacIp", Ipconfig.getHostMacAddress());
                    entity.setTime(startTime);
                    entity.setNodeID(nodeID);
                    // entity.setInfo(info);
                    System.out.println("send a heart beat");
                    service.sendHeartBeat(entity);
                    lastHeartbeat = startTime;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void stop() {
    	isRunning = false;
    }

}
