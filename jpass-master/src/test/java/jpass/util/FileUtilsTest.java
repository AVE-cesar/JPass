package jpass.util;

import java.io.IOException;

import org.junit.Test;

import junit.framework.Assert;

public class FileUtilsTest {

	@Test
	public void test_findJPassFile() throws IOException {
		Assert.assertNotNull(FileUtils.findJPassFile());
	}
}
