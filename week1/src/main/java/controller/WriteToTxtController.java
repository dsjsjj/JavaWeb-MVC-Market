package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.SaledetailDAO;
import vo.Saledetail;

@WebServlet("/Saledetail/DownloadToTxt.do")
public class WriteToTxtController extends HttpServlet {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			ArrayList<Saledetail> listSaledetail = SaledetailDAO.queryAll();
			Timestamp time = new Timestamp(System.currentTimeMillis());// 获取系统当前时间
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd"); // 取得日期格式
			String writeTime = df.format(time); // 导出日期
			String fileName = "saleDetail" + writeTime + ".txt";
			// 下水管
			File file = new File(fileName);
			// 下水道
			FileWriter fw = new FileWriter(file);
			// 水龙头
			PrintWriter pw = new PrintWriter(fw);
			// 标题栏
			pw.println("流水号\t条形码\t商品名称\t价格\t数量\t收银员\t销售时间");

			for (Saledetail sd : listSaledetail) {
				String info = sd.toString();
				pw.println(info);
			}
			fw.close();

			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

			FileInputStream fis = new FileInputStream(fileName);
			OutputStream os = response.getOutputStream();

			byte[] buffer = new byte[1024];
			int len;
			while ((len = fis.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			fis.close();
			os.flush();
			os.close();
			
			/* 此处调用函数，将收银日志发送到spark streaming */
			/*----------------------------------*/
			ServletContext contextSetter = getServletContext();
			String message = "成功导出到txt文件";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
