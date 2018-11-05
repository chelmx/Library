package vivo.remote.procedure.call.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

	@SuppressWarnings("unchecked")
	public static <Type> Type getRemoteProxyObject(Class<?> serviceInterface, InetSocketAddress address) {

		return  (Type)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[] {serviceInterface}, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Socket socket = new Socket();
				ObjectOutputStream output = null ; 
				ObjectInputStream input = null ; 
				try {
					socket.connect(address);
					output = new ObjectOutputStream(socket.getOutputStream()) ;
					output.writeUTF(serviceInterface.getName());
					output.writeUTF(method.getName());
					output.writeObject(method.getParameterTypes());
					output.writeObject(args);
					input = new ObjectInputStream(socket.getInputStream());

					return input.readObject();
				} catch (Exception e) {
					e.printStackTrace();
					
					return null;
				} finally {
					try {
						if (null != output) {
							output.close(); 
						}
						if (null != input) {
							input.close() ; 
						}
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
