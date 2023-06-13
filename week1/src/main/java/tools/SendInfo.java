package tools;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

public class SendInfo extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void sendToServer(String message) {
		ServletContext contextSetter = getServletContext();
		contextSetter.setAttribute("message", message);
		System.out.println("send:" + message);
	}
}
