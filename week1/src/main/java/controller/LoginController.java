package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.UserDAO;
import vo.User;

/**
 * Servlet implementation class LoginController
 */

@WebServlet("/User/Login.do") // 地址
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginController() {
		super();
		// TODO Auto-generated constructor stub
	}

//  接受来自客户端post请求
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		通过request对象接收
		String userName = request.getParameter("userName"); // 获取前端传来的用户名
		String password = request.getParameter("password"); // 获取前端传来的密码
		User user = UserDAO.get(userName); // 通过查询userName，返回根据用户名查询的对象
//		执行处理

		response.setContentType("text/html;charset=utf-8");
		HashMap map = new HashMap<>();
		if (user == null) {// 先判断user是否为空
			map.put("code", 2);
			map.put("info", "用户名不存在！");
			/* 此处调用函数，将登录信息发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = userName + "login" + " error";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);
			/*----------------------------------*/
		} else {
			String userFind = user.getPassword();// 得到数据库中的密码
			if (userFind != null) {// 再判断userFind是否为空
				String MD5password = tools.Md5.MD5(password);// 将输入的密码加密
				if (userFind.equals(MD5password)) {// 最后判断是否相等
					map.put("code", 1);
					map.put("info", "登陆成功");
					/* 此处调用函数，将登录信息发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = user.getRole() + " " + user.getUserName() + " " + "login";
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);
					/*----------------------------------*/
				} else {
					map.put("code", 2);
					map.put("info", "您输入的密码不正确！");
					/* 此处调用函数，将登录信息发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = user.getRole() + " " + user.getUserName() + " " + "login" + " error";
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);
					/*----------------------------------*/
				}
			} else {
				map.put("code", 2);
				map.put("info", "用户名或密码不正确！");
			}
		}
		String retInfo = new Gson().toJson(map);
		PrintWriter out = response.getWriter();// 获取输出对象
		out.print(retInfo);
		out.flush();// 刷新缓冲区，立即发送
		out.close();
	}
}
