package vivo.remote.procedure.call.server;

import java.util.concurrent.ConcurrentHashMap;

public interface Server {
	public void start();
	public void stop();
	public boolean isRunning();
	public int getPort();
	public void setPort(int port);
	public void register(Class<?> service, Class<?> serviceImpl);
	public void unregister(Class<?> service);
	public ConcurrentHashMap<String, Class<?>> getServiceRegistry();
}
