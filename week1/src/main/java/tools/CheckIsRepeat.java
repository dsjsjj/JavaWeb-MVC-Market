package tools;

import dao.ProductDAO;
import vo.Product;

public class CheckIsRepeat {
	//判断该对象和数据库中的对象是否重复
		public static boolean check(Product pro) {
			Product product=ProductDAO.get(pro.getBarCode());//在数据库中查找是否存在相同条形码
			if(product!=null) {//product列表不为空，说明存在相同条形码
				return false;
			}
			return true;
		}
}
