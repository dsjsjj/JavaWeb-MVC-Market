package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.gson.Gson;

import dao.ProductDAO;
import dao.UserDAO;
import vo.Product;
import vo.User;

@MultipartConfig
@WebServlet("/Product/maintainFromTxt.do") // 地址
public class ReadFromTxtController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public ReadFromTxtController() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 获取上传的文件
		Part filePart = request.getPart("file");
		// 获取文件输入流
		
		response.setContentType("text/html;charset=utf-8");
		HashMap map = new HashMap<>();
		
		String userName = request.getParameter("userName"); // 获取前端传来的操作员
		User user = UserDAO.get(userName); // 通过查询userName，返回根据用户名查询的对象
		
		if(user.getRole().equals("收银员")) {
			map.put("code", 2);
			map.put("info", "您（收银员）没有执行该项功能的权限,请联系管理员！");
			/* 此处调用函数，将收银日志发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = user.getRole()+ " " + user.getUserName()+ " " + "import error ";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		}
		else {
			try(
				InputStream inputStream = filePart.getInputStream();
				BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))  {
				
				ArrayList<Product> product =new ArrayList<Product>();
		        String info = bf.readLine();//第一行，忽略
		        int countSuccess=0;
		        int countError=0;
		        info=bf.readLine();//第二行开始是数据
		        while (info!=null) {
		            // 处理每一行数据，例如按制表符分割并插入到数据库中
		        	//分离数据
					String arr[]=info.split("\t");
					Product pro = new Product(arr[0], arr[1],Double.parseDouble(arr[2]), arr[3]);
					product.add(pro);//将读取的数据循环加入product集合中
					info=bf.readLine();//读取下一行
				}
				for(int i=0;i<product.size();i++) {
					Product pro=product.get(i);
					if( tools.CheckIsRepeat.check(pro)) {//商品表中不存在
						ProductDAO.insert(pro);//向数据库中添加一条
						countSuccess++;//记录数+1
					}
					else {
						countError++;
					}
		        }
				map.put("code", 1);
				map.put("info", "从txt文件成功导入 "+countSuccess+" 条商品数据， "+ countError+" 条数据导入失败。"); 
				/* 此处调用函数，将收银日志发送到spark streaming */
				/*----------------------------------*/
				ServletContext contextSetter = getServletContext();
				String message = user.getRole()+ " " + user.getUserName()+ " " + "import "+countSuccess+" 条商品数据， "+ countError+" 条数据导入失败。";
				contextSetter.setAttribute("message", message);
				System.out.println("send:" + message);				
				/*----------------------------------*/
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String retInfo = new Gson().toJson(map);
		PrintWriter out = response.getWriter();// 获取输出对象
		out.print(retInfo);
		out.flush();// 刷新缓冲区，立即发送
		out.close();
    }
}

