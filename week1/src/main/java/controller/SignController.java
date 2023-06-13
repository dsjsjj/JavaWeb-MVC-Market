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
import tools.SendInfo;
import vo.User;

@WebServlet("/User/Sign.do") // 地址
public class SignController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SignController() {
		super();
		// TODO Auto-generated constructor stub
	}

//  接受来自客户端post请求
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
//		通过request对象接收
		String userName = request.getParameter("newUserName"); // 获取前端传来的用户名
		String password = request.getParameter("newPassword"); // 获取前端传来的密码
		String passwordTwice = request.getParameter("newPassword_twice"); // 获取前端传来的第二次密码
		String name = request.getParameter("name"); // 获取前端传来的姓名
		String role = request.getParameter("role"); // 获取前端传来的角色

//		执行处理
		response.setContentType("text/html;charset=utf-8");
		HashMap map = new HashMap<>();
		// 判断该用户名是否已存在
		User user = UserDAO.get(userName); // 通过查询userName，返回根据用户名查询的对象
		if (user != null) {// user不为空，说明已经存在该用户名
			map.put("code", 2);
			map.put("info", "用户名已存在！");
			/* 此处调用函数，将注册信息发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = userName+ " " + userName + " " + "sign"+" error";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		} else {// user为空，说明要注册的用户名不存在
			/* 此处耦合性较强，将md5加密算法和密码格式要求检查算法单独写在一个类中 */
			if (!tools.CheckPasswordReg.validatePassword(password)) {// 不符合格式
				map.put("code", 3);
				map.put("info", "您的密码不符合复杂性要求！（密码长度不少于6个字符，至少有一个小写字母，至少有一个大写字母，至少一个数字）");
				/* 此处调用函数，将注册信息发送到spark streaming */
				/*----------------------------------*/
				ServletContext contextSetter = getServletContext();
				String message = userName+ " " + userName + " " + "sign"+" error";
				contextSetter.setAttribute("message", message);
				System.out.println("send:" + message);				
				/*----------------------------------*/
				
			} else {//符合格式要求
				if (!password.equals(passwordTwice)) {// 两次密码不一致
					map.put("code", 4);
					map.put("info", "两次输入的密码必须一致！");
					/* 此处调用函数，将注册信息发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = userName+ " " + userName + " " + "sign"+" error";
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);				
					/*----------------------------------*/
				} else {
					String MD5Password = tools.Md5.MD5(password);
					User user1 = new User(userName, MD5Password, name, role);
					UserDAO.insert(user1);
					map.put("code", 1);
					map.put("info", "注册成功");
					/* 此处调用函数，将注册信息发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = user1.getRole() + " " + user1.getUserName() + " " + "sign";
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);
					
//					String message = user1.getRole() + " " + user1.getUserName() + " " + "sign";
//					SendInfo sd = new SendInfo();
//					sd.sendToServer(message);
					/*----------------------------------*/
				}
			}
		}
		String retInfo = new Gson().toJson(map);
		PrintWriter out = response.getWriter();// 获取输出对象
		out.print(retInfo);
		out.flush();// 刷新缓冲区，立即发送
		out.close();
	}
}
