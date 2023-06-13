package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import com.google.gson.Gson;

import dao.ProductDAO;
import dao.SaledetailDAO;
import vo.Saledetail;
import vo.User;

@WebServlet("/Saledeta/Cash.do") // 地址
public class CashController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CashController() {
        super();
        // TODO Auto-generated constructor stub
    }

//  接受来自客户端post请求
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		通过request对象接收
		String barCode = request.getParameter("barCode");		//获取前端传来的条形码
		int count = Integer.parseInt(request.getParameter("count"));		//获取前端传来的数量
		String userName = request.getParameter("userName");	//获取前端传来的userName
		User user = new User();
		user.setUserName(userName);
		
		response.setContentType("text/html;charset=utf-8");
		HashMap map = new HashMap<>();
		
		//判断格式是否正确
		String regex = "\\d{6}";
		if (!barCode.matches(regex)) {// 格式不正确
			map.put("code", 2);
			map.put("info", "请输入正确的条形码格式！（e.g.:000001）");
			/* 此处调用函数，将收银日志发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = user.getRole()+ " " + user.getUserName()+ " " + "cash error";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		}else// 格式正确 后核对数据库中是否存在该条形码
		{
			if (ProductDAO.get(barCode) == null) {// 不存在
				map.put("code", 3);
				map.put("info", "此商品不存在");
				/* 此处调用函数，将收银日志发送到spark streaming */
				/*----------------------------------*/
				ServletContext contextSetter = getServletContext();
				String message = user.getRole()+ " " + user.getUserName()+ " " + "cash error";
				contextSetter.setAttribute("message", message);
				System.out.println("send:" + message);				
				/*----------------------------------*/
			}else {// 存在
				String productName = ProductDAO.get(barCode).getProductName();// 得到对应的名称
				double price = ProductDAO.get(barCode).getPrice();// 得到对应的单价
				String operator = user.getUserName();// 得到对应登录用户名
				Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 收银时间
				SimpleDateFormat lshdf = new SimpleDateFormat("yyyyMMdd"); // 流水号前8位
				String saleTime = df.format(time); // 收银时间
				Date date = null;
				try {
					date = df.parse(saleTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Saledetail s = SaledetailDAO.getMaxLsh();
				if (s == null) {// 流水号记录为空
					String lsh_fomr8 = lshdf.format(time);// 今天流水号前8位
					String lsh = lsh_fomr8 + "0000";
					Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
					SaledetailDAO.insert(sd);
					map.put("code", 1);
					map.put("info", "收银成功");
					/* 此处调用函数，将收银日志发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = sd.getSaleTime()+" "+sd.getOperator()+" "+" cash "+" "+sd.getCount()+" "+sd.getProductName();
					contextSetter.setAttribute("message", message);
					System.out.println("send:" + message);				
					/*----------------------------------*/
				}else {// 不为空
					String MaxLsh = s.getLsh();// 最大流水号
					String ymd = MaxLsh.substring(0, 8);// 最大流水号前8位
					String last_4 = MaxLsh.substring(9, 12);// 最大流水号后4位
					int intlast_4 = Integer.parseInt(last_4);
					String newlast_4 = String.format("%04d", intlast_4 + 1);
					String lsh_fomr8 = lshdf.format(time);// 今天流水号前8位
					String lsh;
					if (lsh_fomr8.equals(ymd)) {// 日期相同
						lsh = lsh_fomr8 + newlast_4;
					} else// 日期不相同
						{
							lsh = lsh_fomr8 + "0000";
						}
					Saledetail sd = new Saledetail(lsh, barCode, productName, price, count, operator, date);
					SaledetailDAO.insert(sd);
					map.put("code", 1);
					map.put("info", "收银成功");
					
					/* 此处调用函数，将收银日志发送到spark streaming */
					/*----------------------------------*/
					ServletContext contextSetter = getServletContext();
					String message = sd.getSaleTime()+" "+sd.getOperator()+" "+" cash "+" "+sd.getCount()+" "+sd.getProductName();
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
