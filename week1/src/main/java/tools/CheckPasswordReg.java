package tools;

import java.util.regex.Pattern;

public class CheckPasswordReg {
	//检查密码是否符合格式要求
		public static boolean validatePassword(String password) {
	      return 	Pattern.compile("[0-9]").matcher(password).find() &&
	              Pattern.compile("[a-z]").matcher(password).find() &&
	              Pattern.compile("[A-Z]").matcher(password).find() &&
	              Pattern.compile("[a-zA-Z0-9]{5,}").matcher(password).find();
	  }
}
