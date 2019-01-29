package vn.com.fwd.importtool.constants;

import java.util.Arrays;
import java.util.List;

public class ImportConstants {
	public static final String INSERT = "INSERT";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILURE = "FAILURE";
	
	public static class DBNames {
		public static final String Server24 = "Server24";
		public static final String Server22 = "Server22";
		
		public static List<String> getListDBName() {
			return Arrays.asList(new String[] {Server24, Server22});
		}
	}
}
