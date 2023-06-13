package dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.servlet.ServletContext;

public class MultiThreadServerDAO implements Runnable {
	private volatile boolean isRunning = true;
	private static final String PARAMNAME = "message";

	ServerSocket server;
	ServletContext contextGetter;

	public MultiThreadServerDAO(ServerSocket server,ServletContext contextGetter) {
		this.server = server;
		this.contextGetter = contextGetter;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				System.out.println("等待连接...");
				Socket client = server.accept();
				System.out.println("客户端成功连接！" + client.getInetAddress().getHostAddress());
				PrintWriter pstream = new PrintWriter(client.getOutputStream(), true);
		    	while (isRunning) {
		        	//接收message
			        String message = (String) contextGetter.getAttribute(PARAMNAME);
			        if (message != null) {
			        	pstream.println(message);
			        	contextGetter.removeAttribute(PARAMNAME);
			        }
		        }
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}


//<listener>
//        <listener-class>controller.StartupListener</listener-class>
//    </listener>
