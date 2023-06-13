package controller;

import java.io.File;
import java.io.FileInputStream;
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


import dao.SaledetailDAO;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import vo.Saledetail;


@WebServlet("/Saledetail/DownloadToXls.do")
public class WriteToXlsController extends HttpServlet{

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
			String fileName = "saleDetail" + writeTime + ".xls";
			
			
			//写入xls表
			//创建工作薄对象
			WritableWorkbook book = Workbook.createWorkbook(new File(fileName));
			//创建工作表对象
			WritableSheet sheet=book.createSheet("流水信息", 0);
			String title[]={"流水号","条形码","商品名称","价格","数量","收银员","销售时间"};
			for(int i=0;i<title.length;i++){
	            WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
				CellView cellView = new CellView();
	            cellView.setAutosize(true); //设置自动大小  
	            //sheet.setColumnView(i, cellView); //根据内容自动设置列宽  
	            WritableCellFormat format = new WritableCellFormat(font);
	            format.setAlignment(Alignment.CENTRE); //居中对齐
	            format.setBackground(Colour.YELLOW); //背景色
	            format.setBorder(Border.ALL, BorderLineStyle.THICK, Colour.BLACK);//边框
				Label label = new Label(i,0,title[i]);
				sheet.addCell(label);//将流水号，条形码，商品名称，价格，数量，收银员，销售时间标题依次加入单元格
			}
			//写数据，按行添加
			int count1 =1;
			for(Saledetail sd:listSaledetail){
				Label label = new Label(0, count1, sd.getLsh());
				sheet.addCell(label);//将listSaledetail中的count行的lsh加入（0，1）位置的单元格
				label = new Label(1, count1, sd.getBarCode());
				sheet.addCell(label);//将listSaledetail中的count行的条形码加入（1，1）位置的单元格
				label =new Label(2, count1, sd.getProductName());
				sheet.addCell(label);//将listSaledetail中的count行的商品名称加入（2，1）位置的单元格
				label = new Label(3, count1,String.valueOf(sd.getPrice()));
				sheet.addCell(label);//将listSaledetail中的count行的单价加入（3，1）位置的单元格
				label = new Label(4, count1,String.valueOf(sd.getCount()));
				sheet.addCell(label);//将listSaledetail中的count行的数量加入（4，1）位置的单元格
				label = new Label(5, count1,String.valueOf(sd.getOperator()));
				sheet.addCell(label);//将listSaledetail中的count行的收银员加入（4，1）位置的单元格
				label = new Label(6, count1,String.valueOf(sd.getSaleTime()));
				sheet.addCell(label);//将listSaledetail中的count行的收银时间加入（4，1）位置的单元格
				count1++;//进入下一行
			}
			book.write();//写入表格
			book.close();
			

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
			String message = "成功导出到xls文件";
			contextSetter.setAttribute("message", message);
			System.out.println("send:" + message);				
			/*----------------------------------*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
