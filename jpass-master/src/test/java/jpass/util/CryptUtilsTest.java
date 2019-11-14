package jpass.util;

import org.junit.Assert;
import org.junit.Test;

public class CryptUtilsTest {

	@Test
	public void test() throws Exception {
		byte[] result = "[B@58372a00".getBytes();

		System.out.println(result);
		System.out.println(CryptUtils.getPKCS5Sha256Hash(FileUtilsTest.DEFAULT_PASSWORD.toCharArray()));
	}

}
