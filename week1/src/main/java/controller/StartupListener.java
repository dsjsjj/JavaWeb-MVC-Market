package controller;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import dao.MultiThreadServerDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;


@WebListener
public class StartupListener implements ServletContextListener {
    private ServerSocket serverSocket;
    private static final int PORT = 7777;
    private static final String PARAMNAME = "message";
    
    //两种实现方法 BlockingQueue/Set
    BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(100);
    HashSet<String> eventSet = new HashSet<>();
    
    // 初始化方法，在Tomcat启动时执行
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // 在端口PORT启动Socket服务
            serverSocket = new ServerSocket(PORT);
            System.out.println("Socket服务已启动，监听端口号：" + serverSocket.getLocalPort());
            ServletContext contextGetter = sce.getServletContext();
            new Thread(new MultiThreadServerDAO(serverSocket,contextGetter)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 销毁方法，在Tomcat停止时执行
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            // 关闭Socket服务
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 处理客户端请求的方法
    private void handleRequest(Socket socket,ServletContext contextGetter) throws IOException, InterruptedException {
        // TODO: 实时处理逻辑
    	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    	while (true) {
        	//接收message
	        String message = (String) contextGetter.getAttribute(PARAMNAME);
	        if (message != null) {
	        	out.println(message);
	        	contextGetter.removeAttribute(PARAMNAME);
	        }
        }
    }
}