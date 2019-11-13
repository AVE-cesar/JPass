package jpass.xml.converter;

import java.math.BigInteger;
import java.util.Date;

import jpass.data.DocumentHelper;
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

	public static void main(String[] args) throws Exception {
		System.out.println("On convertit le fichier: " + args[0]);
		DocumentHelper documentHelper = new DocumentHelper(args[0], CryptUtils.getPKCS5Sha256Hash(args[1].toCharArray()));

		Entries entries = documentHelper.readDocument();
		// convert entries to new model
		for (Entry entry : entries.getEntries()) {
			System.out.println("Title: " + entry.getTitle());
			System.out.println("URL: " + entry.getUrl());
			System.out.println("Username: " + entry.getUser());
			System.out.println("Password: " + entry.getPassword());
			System.out.println("Creation date: " + entry.getCreationDate());
			System.out.println("Update date: " + entry.getUpdateDate());
			System.out.println("Disable: " + entry.isDisabled());
			System.out.println("Hit: " + entry.getHit());

			System.out.println("\r\n");

			if (entry.getCreationDate() == null) {
				entry.setCreationDate(new Date());
			}
			if (entry.getUpdateDate() == null) {
				entry.setUpdateDate(new Date());
			}
			if (entry.getHit() == null) {
				entry.setHit(new BigInteger("0"));
			}
		}

		documentHelper.writeDocument(entries);
		System.out.println("Les éléments sans date ont maintenant une date.");
	}

}
