package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.gson.Gson;

import dao.ProductDAO;
import dao.UserDAO;
import vo.Product;
import vo.User;


@MultipartConfig
@WebServlet("/Product/maintainFromXml.do") // 地址
public class ReadFromXmlController extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ReadFromXmlController() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// 获取上传的文件
		Part filePart = request.getPart("file");
		
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
			ArrayList<Product> product =new ArrayList<Product>();
			try {
				// 获取文件输入流
			    InputStream inputStream = filePart.getInputStream();
			    //1.从xml文件中生成document对象
			    SAXReader read =new SAXReader();
			    Document doc =read.read(inputStream);
			    //2.获取根元素
				Element root=doc.getRootElement();
				List list=root.elements("product");
			    
				//3.对集合进行遍历
				//采用迭代器的方法进行遍历
				int countSuccess = 0;
				int countError = 0;
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					Element sdElement = (Element) iterator.next();
					//取条形码子节点，获取内容
			    	String barCode = sdElement.element("barCode").getText();
					String productName = sdElement.element("productName").getText();
					String price = sdElement.element("price").getText();
					String supply = sdElement.element("supply").getText();
			    	Product pro = new Product(barCode, productName,Double.parseDouble(price),supply);
					product.add(pro);//将商品信息加入列表
				}	
				for(int i=0;i<product.size();i++) {
					Product pro=product.get(i);
					if(tools.CheckIsRepeat.check(pro)) {//商品表中不存在
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
				
			} catch (Exception e) {
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
