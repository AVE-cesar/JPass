package jpass.util;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;

public class FileUtilsTest {

	public static final String TEST_RESOURCE_DIRECTORY = "src/test/resources/";
	
	public static final String DEFAULT_FILENAME = "test.jpass";
	
	public static final String DEFAULT_PASSWORD = "test";
	
	@Test
	public void test_findJPassFile() throws IOException {
		Assert.assertNotNull(FileUtils.findJPassFile(TEST_RESOURCE_DIRECTORY + DEFAULT_FILENAME));
	}
}
