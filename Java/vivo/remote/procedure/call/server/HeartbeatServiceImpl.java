package vivo.remote.procedure.call.server;

import java.util.concurrent.ConcurrentHashMap;

import vivo.remote.procedure.call.heartbeat.HeartbeatEntity;
import vivo.remote.procedure.call.heartbeat.HeartbeatListener;

public class HeartbeatServiceImpl implements HeartbeatService {

	@Override
	public void sendHeartBeat(HeartbeatEntity info) {
		HeartbeatListener listener = HeartbeatListener.getInstance();

		if (!listener.checkNodeValid(info.getNodeID())) {
			listener.registerNode(info.getNodeID(), info);
        }
	}

	@Override
	public ConcurrentHashMap<String, Object> getNodes() {
		return HeartbeatListener.getInstance().nodes;
	}

}
