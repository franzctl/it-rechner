package itrechner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SubnettingTest {

	@Test
	void adresseHinUndZurueck() {
		assertEquals(3232281098L, Subnetting.alsZahl("192.168.178.10"));
		assertEquals("192.168.178.10", Subnetting.alsText(3232281098L));
	}

	@Test
	void maskeAusPraefix() {
		assertEquals("255.255.255.0", Subnetting.alsText(Subnetting.maske(24)));
		assertEquals("255.255.255.192", Subnetting.alsText(Subnetting.maske(26)));
		assertEquals("255.255.0.0", Subnetting.alsText(Subnetting.maske(16)));
		assertEquals("0.0.0.0", Subnetting.alsText(Subnetting.maske(0)));
		assertEquals("255.255.255.255", Subnetting.alsText(Subnetting.maske(32)));
	}

	@Test
	void berichtFuerEinSlash26() {
		String bericht = Subnetting.bericht("10.0.5.130", 26);
		assertTrue(bericht.contains("Netzadresse   10.0.5.128"), bericht);
		assertTrue(bericht.contains("Broadcast     10.0.5.191"), bericht);
		assertTrue(bericht.contains("Erster Host   10.0.5.129"), bericht);
		assertTrue(bericht.contains("Letzter Host  10.0.5.190"), bericht);
		assertTrue(bericht.contains("Hosts nutzbar 62"), bericht);
	}

	@Test
	void slash30HatZweiHosts() {
		assertTrue(Subnetting.bericht("172.20.1.1", 30).contains("Hosts nutzbar 2"));
	}

	@Test
	void slash32HatKeinenHostbereich() {
		assertTrue(Subnetting.bericht("10.0.0.1", 32).contains("Hostbereich   keiner"));
	}

	@Test
	void privateBereiche() {
		assertTrue(Subnetting.istPrivat(Subnetting.alsZahl("10.1.2.3")));
		assertTrue(Subnetting.istPrivat(Subnetting.alsZahl("172.16.0.1")));
		assertTrue(Subnetting.istPrivat(Subnetting.alsZahl("172.31.255.255")));
		assertTrue(Subnetting.istPrivat(Subnetting.alsZahl("192.168.0.1")));
		assertFalse(Subnetting.istPrivat(Subnetting.alsZahl("172.32.0.1")));
		assertFalse(Subnetting.istPrivat(Subnetting.alsZahl("8.8.8.8")));
	}

	@Test
	void klassen() {
		assertEquals("A", Subnetting.klasse(Subnetting.alsZahl("10.0.0.1")));
		assertEquals("B", Subnetting.klasse(Subnetting.alsZahl("172.20.1.1")));
		assertEquals("C", Subnetting.klasse(Subnetting.alsZahl("192.168.1.1")));
		assertEquals("D (Multicast)", Subnetting.klasse(Subnetting.alsZahl("224.0.0.1")));
	}

	@Test
	void ungueltigeEingaben() {
		assertThrows(IllegalArgumentException.class, () -> Subnetting.alsZahl("192.168.1"));
		assertThrows(IllegalArgumentException.class, () -> Subnetting.alsZahl("192.168.1.256"));
		assertThrows(IllegalArgumentException.class, () -> Subnetting.alsZahl("a.b.c.d"));
		assertThrows(IllegalArgumentException.class, () -> Subnetting.maske(33));
	}
}
