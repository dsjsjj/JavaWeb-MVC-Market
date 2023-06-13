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

import dao.ProductDAO;
import dao.UserDAO;
import vo.Product;
import vo.User;

@WebServlet("/Product/maintainFromKeyboard.do") // 地址
public class ReadFromKerboardController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public ReadFromKerboardController() {
		super();
		// TODO Auto-generated constructor stub
	}

//  接受来自客户端post请求
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String[] arr = new String[4];
		//定义格式
		String barCodeRge="[\\d]{6}";
		String produceNameRge=".";
		String priceRge="^[0-9]\\d*(\\.\\d{1,2})?$";
		String supplyRge="\\d+";
//		通过request对象接收
		arr[0]= request.getParameter("newBarCode"); // 获取前端传来的条形码
		arr[1]= request.getParameter("newProductName"); // 获取前端传来的产品名称
		arr[2]= request.getParameter("newPrice"); // 获取前端传来的价格
		arr[3]= request.getParameter("newSupply"); // 获取前端传来的供应商
		String userName = request.getParameter("userName"); // 获取前端传来的操作员
		
		User user = UserDAO.get(userName); // 通过查询userName，返回根据用户名查询的对象
		
//		执行处理
		response.setContentType("text/html;charset=utf-8");
		HashMap map = new HashMap<>();
		
		if(user.getRole().equals("收银员")) {
			map.put("code", 2);
			map.put("info", "您（收银员）没有执行该项功能的权限，请联系管理员！");
			/* 此处调用函数，将收银日志发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = user.getRole()+ " " + user.getUserName()+ " " + "import error";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		}
		else {
			Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
			if(!(arr[0].matches(barCodeRge))||arr[1].matches(produceNameRge)||
					!(arr[2].matches(priceRge))||arr[3].matches(supplyRge)) {
				map.put("code", 3);
				map.put("info", "请输入正确的数据格式！");
			}
			else {
				if(tools.CheckIsRepeat.check(pro)) {//不重复
					ProductDAO.insert(pro);//向数据库中添加一条
					map.put("code", 1);
					map.put("info", "导入成功!");
					/* 此处调用函数，将收银日志发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = user.getRole()+ " " + user.getUserName()+ " " + "import 1 条商品信息";
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);				
					/*----------------------------------*/
				}
				else {//条形码重复
					map.put("code", 4);
					map.put("info", "条形码已存在!");
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
