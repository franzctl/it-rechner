package itrechner;

public class Subnetting {

	public static long alsZahl(String adresse) {
		String[] teile = adresse.split("\\.");
		if (teile.length != 4) {
			throw new IllegalArgumentException("IP braucht vier durch Punkte getrennte Zahlen");
		}
		long wert = 0;
		for (int i = 0; i < 4; i++) {
			int block;
			try {
				block = Integer.parseInt(teile[i].trim());
			} catch (NumberFormatException fehler) {
				throw new IllegalArgumentException("Block " + (i + 1) + " Ist keine Zahl");
			}
			if (block < 0 || block > 255) {
				throw new IllegalArgumentException("Block " + (i + 1) + " liegt nicht zwischen 0 und 255");
			}
			wert = wert * 256 + block;
		}
		return wert;
	}
	/** Aus dem Praefix wird die Maske: 24 fuehrende Einsen, Rest Nullen. */
	public static long maske(int praefix) {
		if (praefix < 0 || praefix > 32) {
			throw new IllegalArgumentException("Praefix muss zwischen 0 und 32 liegen");
		}
		if (praefix == 0) {
			return 0;
		}
		return (0xFFFFFFFFL << (32 - praefix)) & 0xFFFFFFFFL;
	}

	/** Und wieder zurueck. */
	public static String alsText(long wert) {
		return ((wert >> 24) & 255) + "." + ((wert >> 16) & 255) + "."
				+ ((wert >> 8) & 255) + "." + (wert & 255);
	}

	/** Stellt alle Angaben zu einem Netz als Text zusammen. */
	public static String bericht(String adresse, int praefix) {
		long ip = alsZahl(adresse);
		long maske = maske(praefix);
		long netz = ip & maske;                          // UND blendet den Hostteil aus
		long broadcast = netz | (~maske & 0xFFFFFFFFL);  // ODER setzt den Hostteil auf Einsen

		long gesamt = 1L << (32 - praefix);
		long nutzbar = gesamt > 2 ? gesamt - 2 : 0;

		StringBuilder text = new StringBuilder();
		text.append("Adresse       ").append(alsText(ip)).append("/").append(praefix).append("\n");
		text.append("Subnetzmaske  ").append(alsText(maske)).append("\n");
		text.append("Wildcard      ").append(alsText(~maske & 0xFFFFFFFFL)).append("\n");
		text.append("\n");
		text.append("Netzadresse   ").append(alsText(netz)).append("\n");
		text.append("Broadcast     ").append(alsText(broadcast)).append("\n");
		if (nutzbar > 0) {
			text.append("Erster Host   ").append(alsText(netz + 1)).append("\n");
			text.append("Letzter Host  ").append(alsText(broadcast - 1)).append("\n");
		} else {
			text.append("Hostbereich   keiner (Praefix zu gross)\n");
		}
		text.append("Hosts nutzbar ").append(nutzbar).append("\n");
		text.append("\n");
		text.append("Klasse        ").append(klasse(ip)).append("\n");
		text.append("Bereich       ").append(istPrivat(ip) ? "privat" : "oeffentlich").append("\n");
		text.append("Maske binaer  ").append(maskeBinaer(maske)).append("\n");
		return text.toString();
	}

	public static String klasse(long ip) {
		long erster = (ip >> 24) & 255;
		if (erster < 128) {
			return "A";
		}
		if (erster < 192) {
			return "B";
		}
		if (erster < 224) {
			return "C";
		}
		if (erster < 240) {
			return "D (Multicast)";
		}
		return "E (reserviert)";
	}

	public static boolean istPrivat(long ip) {
		long erster = (ip >> 24) & 255;
		long zweiter = (ip >> 16) & 255;
		if (erster == 10) {
			return true;
		}
		if (erster == 172 && zweiter >= 16 && zweiter <= 31) {
			return true;
		}
		return erster == 192 && zweiter == 168;
	}

	/** 11111111.11111111.11111111.00000000 - macht das Praefix sichtbar. */
	public static String maskeBinaer(long wert) {
		StringBuilder text = new StringBuilder();
		for (int block = 3; block >= 0; block--) {
			long teil = (wert >> (block * 8)) & 255;
			String bits = Long.toBinaryString(teil);
			while (bits.length() < 8) {
				bits = "0" + bits;
			}
			text.append(bits);
			if (block > 0) {
				text.append('.');
			}
		}
		return text.toString();
	}
}
