package jpass.xml.bind;

import java.util.Date;

import org.junit.Test;

import junit.framework.Assert;

public class EntryTest {

	@Test
	public void test() {
		ObjectFactory objectFactory = new ObjectFactory();
		Entry entry = objectFactory.createEntry();

		entry.setTitle("a");
		entry.setUrl("a");
		entry.setUser("a");
		entry.setPassword("a");
		entry.setNotes("a");
		entry.setCreationDate(new Date());
		entry.setUpdateDate(new Date());

		Assert.assertNotNull(entry);
		Assert.assertNotNull(entry.getTitle());

		Entries entries = objectFactory.createEntries();
		entries.getEntries().add(entry);

		Assert.assertNotNull(entries);
		Assert.assertNotNull(entries.getEntries());
		Assert.assertNotNull("a".equals(entries.getEntries().get(0).getTitle()));
	}

}
