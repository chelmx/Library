package vivo.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Ipconfig {
	public static String getHostIpAddress() {
		try {
			return getIpAddress(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getHostMacAddress() {
		try {
			return getMacAddress(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getIpAddress(InetAddress address) {
		return address.getHostAddress();
	}
	
	public static String getMacAddress(InetAddress address) {
		try {
			byte[] mac = NetworkInterface.getByInetAddress(address).getHardwareAddress();
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < mac.length; i++){  
	            if(i != 0) {
	            	buffer.append(":");  
	            }   
	            String s = Integer.toHexString(mac[i] & 0xFF);  
	            buffer.append(s.length() == 1 ? 0 + s : s);  
	        }  
	           
	        return buffer.toString().toUpperCase();
		} catch (SocketException e) {
			e.printStackTrace();
			return null;
		}
	}
}
