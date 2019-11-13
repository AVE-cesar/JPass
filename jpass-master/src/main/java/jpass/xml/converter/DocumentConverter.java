package jpass.xml.converter;

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
		DocumentHelper documentHelper = new DocumentHelper(args[0],
				CryptUtils.getPKCS5Sha256Hash(args[1].toCharArray()));

		Entries entries = documentHelper.readDocument();
		// convert entries to new model
		for (Entry entry : entries.getEntries()) {
			System.out.println(entry.getCreationDate());
			entry.setCreationDate(new Date());
			entry.setUpdateDate(new Date());
		}

		documentHelper.writeDocument(entries);
	}

}
