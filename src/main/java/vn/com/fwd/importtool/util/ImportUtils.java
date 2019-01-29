package vn.com.fwd.importtool.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class ImportUtils {
	public static UserDetails getLoginUser() {
		UserDetails result = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            result = (UserDetails) principal;
        }
        return result;
    }
	
	public static String getExtensionFile(String fileName) throws Exception {
		try {
			int i = fileName.lastIndexOf('.');
			if (i > 0) {
			    return fileName.substring(i+1);
			} else {
				throw new Exception(fileName + "is not format file name !");
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
