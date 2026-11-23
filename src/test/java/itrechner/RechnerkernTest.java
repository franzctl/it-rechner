package itrechner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class RechnerkernTest {

	private final Rechnerkern kern = new Rechnerkern();

	private void pruefe(String eingabe, double erwartet) {
		assertEquals(erwartet, kern.auswerten(eingabe), 1e-9, eingabe);
	}

	@Test
	void punktVorStrich() {
		pruefe("2+3*4", 14);
		pruefe("2*3+4", 10);
		pruefe("10-2*3", 4);
	}

	@Test
	void klammern() {
		pruefe("(2+3)*4", 20);
		pruefe("((2+3)*2)-1", 9);
		pruefe("10/(2+3)", 2);
	}

	@Test
	void negativeZahlen() {
		pruefe("-5+3", -2);
		pruefe("2*-3", -6);
	}

	@Test
	void zahlensysteme() {
		pruefe("0xFF", 255);
		pruefe("0b1010", 10);
		pruefe("0o17", 15);
		pruefe("0xFF+1", 256);
		pruefe("0xF*0x10", 240);
		pruefe("0xA-0b10", 8);
	}

	@Test
	void bitOperationen() {
		pruefe("0xFF&0x0F", 15);
		pruefe("12|3", 15);
		pruefe("12^10", 6);
		pruefe("~5", -6);
		pruefe("1<<8", 256);
		pruefe("256>>4", 16);
	}

	@Test
	void potenzUndRest() {
		pruefe("2**8", 256);
		pruefe("2**3**2", 512);
		pruefe("17%5", 2);
	}

	@Test
	void funktionenUndKonstanten() {
		pruefe("sin(90)", 1);
		pruefe("cos(0)", 1);
		pruefe("wurzel(16)", 4);
		pruefe("log(1000)", 3);
		pruefe("abs(-7)", 7);
		pruefe("pi", Math.PI);
	}

	@Test
	void ansMerktDasLetzteErgebnis() {
		kern.setAns(42);
		pruefe("Ans+1", 43);
	}

	@Test
	void leerzeichenWerdenIgnoriert() {
		pruefe(" 2 + 3 ", 5);
	}

	@Test
	void fehlerWerdenGemeldet() {
		assertThrows(ArithmeticException.class, () -> kern.auswerten("1/0"));
		assertThrows(ArithmeticException.class, () -> kern.auswerten("wurzel(-1)"));
		assertThrows(ArithmeticException.class, () -> kern.auswerten("1.5&1"));
		assertThrows(IllegalArgumentException.class, () -> kern.auswerten("(2+3"));
		assertThrows(IllegalArgumentException.class, () -> kern.auswerten("2+"));
		assertThrows(IllegalArgumentException.class, () -> kern.auswerten("foo(1)"));
	}

	@Test
	void formatierung() {
		assertEquals("14", Rechnerkern.formatiere(14));
		assertEquals("0.5", Rechnerkern.formatiere(0.5));
		assertEquals("3.14", Rechnerkern.formatiere(Math.PI));
		assertEquals("unendlich", Rechnerkern.formatiere(Double.POSITIVE_INFINITY));
		assertEquals("nicht definiert", Rechnerkern.formatiere(Double.NaN));
	}
}
