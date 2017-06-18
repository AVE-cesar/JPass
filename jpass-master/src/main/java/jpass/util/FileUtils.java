package jpass.util;

import java.io.File;
import java.io.IOException;

public class FileUtils {

	public static String findJPassFile() throws IOException {
		File file = new File("MesPasswords.jpass");
		if (file.exists()) {
		   return file.getCanonicalPath().toString();
		}
		
		return null;
	}
}
