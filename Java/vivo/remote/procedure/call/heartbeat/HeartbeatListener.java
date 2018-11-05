package vivo.remote.procedure.call.heartbeat;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HeartbeatListener {
	 
    public final ConcurrentHashMap<String, Object> nodes = new ConcurrentHashMap<String, Object>();
    public final ConcurrentHashMap<String, Long> nodeStatus = new ConcurrentHashMap<String, Long>();
    private long timeout = 10 * 1000;
    
    private static class SingleHolder {
        private static final HeartbeatListener INSTANCE = new HeartbeatListener();
    }
 
    private HeartbeatListener() {
    }
 
    public static HeartbeatListener getInstance() {
        return SingleHolder.INSTANCE;
    }
 
    public ConcurrentHashMap<String, Object> getNodes() {
        return nodes;
    }
 
    public void registerNode(String nodeId, Object nodeInfo) {
        nodes.put(nodeId, nodeInfo);
        nodeStatus.put(nodeId, System.currentTimeMillis());
    }
 
    public void removeNode(String nodeID) {
        if (nodes.containsKey(nodeID)) {
            nodes.remove(nodeID);
        }
    }
 
    public boolean checkNodeValid(String key) {
        if (!nodes.containsKey(key) || !nodeStatus.containsKey(key)) return false;
        if ((System.currentTimeMillis() - nodeStatus.get(key)) > timeout) return false;
        return true;
    }
 
    public void removeInvalidNode() {
        Iterator<Map.Entry<String, Long>> it = nodeStatus.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Long> e = it.next();
            if ((System.currentTimeMillis() - nodeStatus.get(e.getKey())) > timeout) {
                nodes.remove(e.getKey());
            }
        }
    }
 
}