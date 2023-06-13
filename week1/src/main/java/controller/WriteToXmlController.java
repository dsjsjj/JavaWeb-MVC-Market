package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import dao.SaledetailDAO;
import vo.Saledetail;

@WebServlet("/Saledetail/DownloadToXml.do")
public class WriteToXmlController extends HttpServlet{

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
			String fileName = "saleDetail" + writeTime + ".xml";
			
			
			//将list集合中的内写入xml文件中
			Document doc = DocumentHelper.createDocument();
			//增加根节点
			Element saledetail = doc.addElement("Saledetail");
			//增加子元素
			for(int i=0;i<listSaledetail.size();i++) {
				Saledetail sd=listSaledetail.get(i);
				Element lsh= saledetail.addElement("流水号");
				Element barCode=saledetail.addElement("barCode");
				Element productName=saledetail.addElement("productName");
				Element price=saledetail.addElement("price");
				Element count=saledetail.addElement("count");
				Element operator=saledetail.addElement("operator");
				Element saleTime=saledetail.addElement("saleTime");
				//为子节点添加属性
				lsh.addAttribute("lsh",sd.getLsh());
				//为元素添加属性
				barCode.setText(sd.getBarCode());
				productName.setText(sd.getProductName());
				price.setText(String.valueOf(sd.getPrice()));
				count.setText(String.valueOf(sd.getCount()));
				operator.setText(sd.getOperator());
				saleTime.setText(String.valueOf(sd.getSaleTime()));
				//实例化输出格式对象
				OutputFormat format = OutputFormat.createPrettyPrint();
				//设置输出编码
				format.setEncoding("UTF-8");
				//创建要写入的File对象
				File file = new File(fileName);
				//生成XMLWriter对象，构造函数中的参数为需要输出的文件流和格式
				XMLWriter writer = new XMLWriter(new FileOutputStream(file),format);
				//开始写入，writer方法中包含上面创建的Docu对象
				writer.write(doc);
			}
			

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
			String message = "成功导出到xml文件";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
