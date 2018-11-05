package vivo.remote.procedure.call.server;

import java.util.concurrent.ConcurrentHashMap;

import vivo.remote.procedure.call.heartbeat.HeartbeatEntity;

public interface HeartbeatService {
	public void sendHeartBeat(HeartbeatEntity info);
	public ConcurrentHashMap<String, Object> getNodes();
}
