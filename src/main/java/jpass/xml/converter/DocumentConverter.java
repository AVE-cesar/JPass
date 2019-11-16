package jpass.xml.converter;

import java.io.File;
import java.util.Date;
import java.util.logging.Logger;

import jpass.data.DocumentHelper;
import jpass.ui.JPassFrame;
import jpass.util.CryptUtils;
import jpass.xml.bind.Entries;
import jpass.xml.bind.Entry;

/**
 * Cet outil permet de convertir les données contenues dans le fichier des
 * passwords quand le modèle change. Exemple: ajout de champs au modèle.
 * 
 * @author avebertrand
 *
 */
public class DocumentConverter {

	private static final Logger LOGGER = Logger.getLogger(JPassFrame.class.getName());

	public static void main(String[] args) throws Exception {
		LOGGER.info("On convertit le fichier: " + new File(args[0]).getCanonicalPath());
		DocumentHelper documentHelper = null;
		if (args.length == 2) {
			documentHelper = new DocumentHelper(args[0], CryptUtils.getPKCS5Sha256Hash(args[1].toCharArray()));
		} else {
			documentHelper = new DocumentHelper(args[0], null);
		}

		Entries entries = documentHelper.readDocument();
		// convert entries to new model
		for (Entry entry : entries.getEntries()) {
			LOGGER.info("Title: " + entry.getTitle());
			LOGGER.info("URL: " + entry.getUrl());
			LOGGER.info("Username: " + entry.getUser());
			LOGGER.info("Password: " + entry.getPassword());
			LOGGER.info("Creation date: " + entry.getCreationDate());
			LOGGER.info("Update date: " + entry.getUpdateDate());
			LOGGER.info("Disable: " + entry.isDisabled());
			LOGGER.info("Hit: " + entry.getHit());

			LOGGER.info("\r\n");

			if (entry.getCreationDate() == null) {
				entry.setCreationDate(new Date());
			}
			if (entry.getUpdateDate() == null) {
				entry.setUpdateDate(new Date());
			}
			if (entry.getHit() == null) {
				entry.setHit(0);
			}
		}

		documentHelper.writeDocument(entries);
		LOGGER.info("Les éléments sans date ont maintenant une date.");
	}

}
