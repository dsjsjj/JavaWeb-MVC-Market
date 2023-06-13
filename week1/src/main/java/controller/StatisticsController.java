package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import dao.SaledetailDAO;
import vo.Saledetail;


@WebServlet("/Saledeta/statistics.do") // 地址
public class StatisticsController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	    /**
	     * @see HttpServlet#HttpServlet()
	     */
	   public StatisticsController() {
	       super();
	        // TODO Auto-generated constructor stub
	   }

	//  接受来自客户端post请求
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		通过request对象接收
		String date = request.getParameter("date");		//获取前端传来的日期
//		System.out.println(date);
		String saleTime = date + "%";
		ArrayList<Saledetail> list = SaledetailDAO.query(saleTime);
		response.setContentType("text/html;charset=utf-8");
			String retInfo = new Gson().toJson(list);
			PrintWriter out = response.getWriter();//获取输出对象
			out.print(retInfo);
			out.flush();//刷新缓冲区，立即发送
			out.close();	
			
			/* 此处调用函数，将收银日志发送到spark streaming */
//			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = "statistics success";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
	}

}

