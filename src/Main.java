import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;

import Connection.Connection;
import Patterns.ServerStatus;
import Utils.Access.AccessManager;
import Utils.Config.Config;
import Utils.File.FavIcon.IconManager;
import Utils.File.Log.Log;

public class Main {

	public static void main(String[] args) {
		Log.init();
		for (int i = 0; i < 4; i++)
			System.out.println(Config.rat[i]);
		System.out.println("            Minecraft Agent");
		System.out.println("============================================");
		Config.readConfig();
		IconManager.readIcons();
		AccessManager.init();

		int srcPort = Config.srcPort;
		String dstIP = Config.targetHost;
		int dstPort = Config.targetPort;

		String rewHost = Config.rewrittenHost;
		int rewPort = Config.rewrittenPort;
		checkProperties(srcPort, dstIP, dstPort);

		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		hashtable.put("java.naming.provider.url", "dns:");
		try {
			Attribute attribute = (new InitialDirContext(hashtable))
					.getAttributes("_Minecraft._tcp." + dstIP, new String[] { "SRV" }).get("srv");
			if (attribute != null) {
				String[] redir = attribute.get().toString().split(" ", 4);
				dstIP = redir[3];
				Log.save("SRV重定向: " + dstIP);
				dstPort = Integer.parseInt(redir[2]);
				Log.save("Port: " + dstPort);
			}
		} catch (Exception e) {
			// owo
		}

		try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
			serverSocketChannel.socket().bind(new InetSocketAddress(srcPort));
			Log.save("监听Port: " + srcPort);
			Log.save("目标IP: " + dstIP);
			Log.save("目标Port: " + dstPort);
			checkHostRewrite(rewHost, rewPort);
			Log.save("Motds: " + ServerStatus.motds);
			Log.save("List: " + ServerStatus.list);
			Config.targetHost = dstIP;
			Config.targetPort = dstPort;
			while (true) {
				try {
					SocketChannel socketChannel = serverSocketChannel.accept();
					Thread.ofVirtual().start(new Connection(Config.targetHost, Config.targetPort, socketChannel));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			Log.saveException(e);
		}
		Log.save("Server stopped.");
	}

	private static void checkProperties(int srcPort, String dstIP, int dstPort) {
		if (srcPort < 0 || srcPort > 65535) {
			System.err.println("srcPort超出有效范围(0-65535)");
			System.exit(1);
		} else if (dstIP == null || dstIP.equals("")) {
			System.err.println("targetHost不能为空");
			System.exit(1);
		} else if (dstPort < 0 || dstPort > 65535) {
			System.err.println("dstPort超出有效范围(0-65535)");
			System.exit(1);
		}
	}

	private static void checkHostRewrite(String rewHost, int rewPort) {
		if (!Config.rewriteHost) {
			Log.save("hostRewrite = false");
			return;
		}
		Log.save("hostRewrite = true");
		if (rewHost != null && !(rewPort < 0 || rewPort > 65535)) {
			Log.save("RewrittenHost = " + Config.rewrittenHost);
			Log.save("RewrittenPort = " + Config.rewrittenPort);
		} else if (rewPort < 0 || rewPort > 65535) {
			System.err.println("rewritetenPort超出有效范围(0-65535)");
			System.exit(1);
		} else if (rewHost == null) {
			System.err.println("rewritetenHost不能为空");
			System.exit(1);
		}
	}
}