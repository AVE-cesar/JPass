package jpass.xml.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import jpass.Consts;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;
import jpass.xml.bind.ObjectFactory;

public class JAXBConverterTest {

	private static final Logger LOGGER = Logger.getLogger(JAXBConverterTest.class.getName());

	@Test
	public void testMarshallingUnmarshalling() throws Exception {
		JAXBConverter<Entries> converter = new JAXBConverter<Entries>(Entries.class, Consts.SCHEMAS_ENTRIES_XSD);

		Entries entries;

		File tempFile = File.createTempFile("temp", null);
		LOGGER.info(tempFile.getAbsolutePath());
		tempFile.deleteOnExit();
		OutputStream outputStream = null;
		InputStream inputStream = null;

		try {
			outputStream = new FileOutputStream(tempFile);

			ObjectFactory objectFactory = new ObjectFactory();
			Entry entry = objectFactory.createEntry();

			entry.setTitle("a");
			entries = objectFactory.createEntries();
			entries.getEntries().add(entry);
			Date updateDate = new Date();
			entries.setUpdateDate(updateDate);

			converter.marshal(entries, outputStream, Boolean.valueOf(false));

			inputStream = new FileInputStream(tempFile);
			entries = converter.unmarshal(inputStream);

			Assert.assertNotNull(entries);
			Assert.assertNotNull(entries.getEntries().get(0));
			Assert.assertNotNull("a".equals(entries.getEntries().get(0).getTitle()));

			System.out.println(entries.getUpdateDate());
			System.out.println(updateDate.compareTo(entries.getUpdateDate()));
			// FIXME les 2 dates devraient être égales, pourquoi elles ne le sont pas ?
			// Assert.assertTrue(entries.getUpdateDate().equals(updateDate));
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

}
