package jpass.util;

import java.io.File;
import java.io.IOException;

public class FileUtils {
	
	public static String findJPassFile(String filename) throws IOException {
		File file = new File(filename);
		if (file.exists()) {
		   return file.getCanonicalPath().toString();
		}
		
		return null;
	}
}
