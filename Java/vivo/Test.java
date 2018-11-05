package vivo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import vivo.remote.procedure.call.client.Client;
import vivo.remote.procedure.call.heartbeat.HeartbeatClient;
import vivo.remote.procedure.call.heartbeat.HeartbeatEntity;
import vivo.remote.procedure.call.server.ConfigService;
import vivo.remote.procedure.call.server.ConfigServiceImpl;
import vivo.remote.procedure.call.server.HeartbeatService;
import vivo.remote.procedure.call.server.HeartbeatServiceImpl;
import vivo.remote.procedure.call.server.HelloService;
import vivo.remote.procedure.call.server.HelloServiceImpl;
import vivo.remote.procedure.call.server.ServerCenter;
import vivo.util.Ipconfig;

public class Test {

	private static ServerCenter server = null;
	private static HeartbeatClient hClient = null;
	private static HeartbeatService heartService = null;
	
	public static void main(String[] args) {
		Thread serverThread = new Thread(new Runnable() {
            public void run() {
                server = ServerCenter.getInstance();
				server.register(HeartbeatService.class, HeartbeatServiceImpl.class);
				server.register(HelloService.class, HelloServiceImpl.class);
				server.register(ConfigService.class, ConfigServiceImpl.class);
				System.out.println("ServerCenter started!");
				server.start();
				System.out.println("ServerCenter stopped!");
            }
        });
		serverThread.start();
		
		try {
			HelloService hService = Client.getRemoteProxyObject(Class.forName("sample.remote.procedure.call.server.HelloService" ), new InetSocketAddress("127.0.0.1", 9999));
			ConfigService cService = Client.getRemoteProxyObject(Class.forName("sample.remote.procedure.call.server.ConfigService" ), new InetSocketAddress("127.0.0.1", 9999));
			heartService = Client.getRemoteProxyObject(Class.forName("sample.remote.procedure.call.server.HeartbeatService" ), new InetSocketAddress("127.0.0.1", 9999));
			System.out.println(hService.sayHello("chelmx"));
			System.out.println(cService.read());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		Thread clientThread = new Thread(new Runnable() {
            public void run() {
            	hClient = new HeartbeatClient("127.0.0.1", 9999);
				System.out.println("HeartbeatClient started!");
				hClient.start();
				System.out.println("HeartbeatClient stopped!");
            }
        });
		clientThread.start();
		
		while (true) {
			try {
				char c = (char) System.in.read();
				if ('c' == c) {
					server.stop();
					hClient.stop();
					break;
				} else if ('i' == c) {
					ConcurrentHashMap<String, Object> nodes = heartService.getNodes();
					if (nodes == null) {
						continue;
					}
					for (Iterator<Entry<String, Object>> iterator = nodes.entrySet().iterator(); iterator.hasNext();) {
						Entry<String, Object> element = iterator.next();
						Map<String, Object> info = ((HeartbeatEntity)element.getValue()).getInfo();
						System.out.println(element.getKey() + " : \n\tHostIp : " + info.get("HostIp") + "\n\tHostMac: " + info.get("HostMac"));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				server.stop();
				hClient.stop();
				break;
			}
		}
		
		try {
			serverThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			clientThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
