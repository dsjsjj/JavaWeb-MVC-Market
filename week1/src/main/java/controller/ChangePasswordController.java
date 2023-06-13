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


@WebServlet("/User/changePassword.do") // 地址
public class ChangePasswordController extends HttpServlet{
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
   public ChangePasswordController() {
       super();
        // TODO Auto-generated constructor stub
   }

//  接受来自客户端post请求
@SuppressWarnings({ "unchecked", "rawtypes" })
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	// TODO Auto-generated method stub
//	通过request对象接收
	String userName = request.getParameter("userName");		//获取前端传来用户名
	String oldPassword = request.getParameter("old_password");	//获取前端传来的当前密码
	String newPassword = request.getParameter("new_password");	//获取前端传来的新密码
	String newPasswordTwice = request.getParameter("new_password_twice");	//获取前端传来的第二次新密码
	User user = UserDAO.get(userName);		//获取用户名对应的对象
	response.setContentType("text/html;charset=utf-8");
	HashMap map = new HashMap<>();
	String MD5oldPassword=tools.Md5.MD5(oldPassword);
	if(!MD5oldPassword.equals(user.getPassword())) {//原密码和输入的密码一样
		map.put("code", 2);
		map.put("info", "当前密码不正确！");
		/* 此处调用函数，将注册信息发送到spark streaming */
		/*----------------------------------*/
		ServletContext contextSetter = getServletContext();
		String message = user.getRole()+ " " + user.getUserName()+ " " + "change password error";
		contextSetter.setAttribute("message", message);
		System.out.println("send:" + message);				
//		/*----------------------------------*/
	}
	else {//密码正确
		if(!tools.CheckPasswordReg.validatePassword(newPassword)) {//不匹配
			map.put("code", 3);
			map.put("info", "您的密码不符合复杂性要求（密码长度不少于6个字符，至少有一个小写字母，至少有一个大写字母，至少一个数字）！");
			/* 此处调用函数，将注册信息发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = user.getRole()+ " " + user.getUserName()+ " " + "change password error";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		}
		else {
			if(!newPassword.equals(newPasswordTwice)) {
				map.put("code", 4);
				map.put("info", "两次输入的密码请保持一致！");
				/* 此处调用函数，将注册信息发送到spark streaming */
				/*----------------------------------*/
				ServletContext contextSetter = getServletContext();
				String message = user.getRole()+ " " + user.getUserName()+ " " + "change password error";
				contextSetter.setAttribute("message", message);
				System.out.println("send:" + message);				
				/*----------------------------------*/
			}
			else {
				String MD5Password=tools.Md5.MD5(newPassword);
				UserDAO.update(user.getUserName(), MD5Password);
				map.put("code", 1);
				map.put("info", "修改成功！");
				/* 此处调用函数，将修改日志发送到spark streaming */
				/*----------------------------------*/
				ServletContext contextSetter = getServletContext();
				String message = user.getRole()+ " " + user.getUserName()+ " " + "change password";
				contextSetter.setAttribute("message", message);
				System.out.println("send:" + message);				
				/*----------------------------------*/
			}
		}
	}
		String retInfo = new Gson().toJson(map);
		PrintWriter out = response.getWriter();//获取输出对象
		out.print(retInfo);
		out.flush();//刷新缓冲区，立即发送
		out.close();	
	}
	
}
