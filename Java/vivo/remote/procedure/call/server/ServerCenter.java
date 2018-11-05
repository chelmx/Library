package vivo.remote.procedure.call.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import vivo.remote.procedure.call.heartbeat.HeartbeatEntity;
import vivo.remote.procedure.call.heartbeat.HeartbeatListener;

public class ServerCenter implements Server {

	private int port = 9999;
	private ServerSocket server = null;
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private ConcurrentHashMap<String, Class<?>> serviceRegistry = new ConcurrentHashMap<String, Class<?>>();
	private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) ;
	
    private ServerCenter() {
    }
    
    private static class SingleHolder {
        private static final ServerCenter INSTANCE = new ServerCenter();
    }
    
    public static ServerCenter getInstance() {
        return SingleHolder.INSTANCE;
    }
	
	@Override
	public void start() {
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(port));
			while (true) {
	            executor.execute(new ServiceTask(server.accept()));
	        }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != server) {
				try {
					server.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void stop() {
		isRunning.set(false);
		executor.shutdown();
		if (null != server) {
			try {
				server.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void register(Class<?> service, Class<?> serviceImpl) {
		serviceRegistry.put(service.getName(), serviceImpl);
	}
	
	@Override
	public void unregister(Class<?> service) {
		serviceRegistry.remove(service.getName());
	}
	
	@Override
	public boolean isRunning() {
        return isRunning.get();
    } 
	
	@Override
    public int getPort() {
        return port;
    }
    
	@Override
    public void setPort(int port) {
        this.port = port;
    }
    
	@Override
    public ConcurrentHashMap<String, Class<?>> getServiceRegistry() {
        return serviceRegistry;
    }

	private class ServiceTask implements Runnable {
		private Socket socket;
		
		public ServiceTask(Socket socket) {
			this.socket = socket;
		}
		
		@Override
		public void run() {
			ObjectOutputStream output = null;
			ObjectInputStream input = null;
			try {
				input = new ObjectInputStream(socket.getInputStream());
				String serviceName = input.readUTF();
				String methodName = input.readUTF();
				Class<?>[] parameterTypes = (Class[]) input.readObject();
				Object[] arguments = (Object[]) input.readObject();
				Class<?> ServiceClass = serviceRegistry.get(serviceName);
				Method method = ServiceClass.getMethod(methodName, parameterTypes);
				if (serviceName.indexOf("remote.procedure.call.server.HeartbeatService") >= 0 && methodName.indexOf("sendHeartBeat") >= 0) {
					HeartbeatEntity entity = (HeartbeatEntity)arguments[0];
					Map<String, Object> info = entity.getInfo();
					info.put("InetAddress", socket.getInetAddress());
					entity.setInfo(info);
					arguments[0] = entity;
				}
				Object result = method.invoke(ServiceClass.newInstance(), arguments);
				output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject(result);
			} catch (IOException | ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			} finally {
				try {
					if (null != output) {
						output.close(); 
					}
					if (null != input) {
						input.close() ; 
					}
					if (null != socket) {
						socket.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
