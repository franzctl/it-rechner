package itrechner;

import java.awt.Color;
import java.util.prefs.Preferences;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

/**
 * Helles und dunkles Design. Die Farben der eigenen Flaechen und Tasten stehen hier,
 * die Standardelemente von Swing (Menues, Reiter, Eingabefelder, Dialoge) faerbt ein
 * eigenes Metal-Theme ein. Die Wahl wird in den Benutzereinstellungen gespeichert.
 */
final class Design {

	private static final Preferences EINSTELLUNGEN = Preferences.userNodeForPackage(Design.class);
	private static final String SCHLUESSEL = "dunkel";

	static Color hintergrund;
	static Color flaeche;
	static Color taste;
	static Color text;
	static Color textLeise;
	static final Color TEXT_HELL = new Color(0xFF, 0xFF, 0xFF);
	/** Auf Orange bleibt die Schrift in beiden Designs dunkel, sonst wird sie schlecht lesbar. */
	static final Color TEXT_AUF_OPERATOR = new Color(0x1A, 0x1A, 0x1A);
	static final Color OPERATOR = new Color(0xF5, 0x8A, 0x1F);
	static final Color GLEICH = new Color(0x1E, 0x88, 0xE5);

	private static boolean dunkel;

	static {
		setzeFarben(false);
	}

	private Design() {
	}

	static boolean istDunkel() {
		return dunkel;
	}

	/** Liest die gespeicherte Wahl und setzt das Design, bevor das erste Fenster entsteht. */
	static void ausEinstellungen() {
		setze(EINSTELLUNGEN.getBoolean(SCHLUESSEL, false), false);
	}

	/** Setzt das Design und merkt es sich auf Wunsch fuer den naechsten Start. */
	static void setze(boolean neuDunkel, boolean speichern) {
		setzeFarben(neuDunkel);
		MetalLookAndFeel.setCurrentTheme(dunkel ? new DunklesTheme() : new OceanTheme());
		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException fehler) {
			// Metal gibt es in jeder Java-Installation, dieser Fall tritt praktisch nicht ein.
		}
		if (speichern) {
			EINSTELLUNGEN.putBoolean(SCHLUESSEL, dunkel);
		}
	}

	private static void setzeFarben(boolean neuDunkel) {
		dunkel = neuDunkel;
		if (dunkel) {
			hintergrund = new Color(0x15, 0x17, 0x1A);
			flaeche = new Color(0x1E, 0x21, 0x26);
			taste = new Color(0x2C, 0x30, 0x36);
			text = new Color(0xE8, 0xEA, 0xED);
			textLeise = new Color(0x9A, 0xA0, 0xB4);
		} else {
			hintergrund = new Color(0xF2, 0xF3, 0xF5);
			flaeche = new Color(0xFF, 0xFF, 0xFF);
			taste = new Color(0xE4, 0xE6, 0xEA);
			text = new Color(0x1A, 0x1A, 0x1A);
			textLeise = new Color(0x8A, 0x90, 0xA8);
		}
	}

	/** Dunkles Metal-Theme: "white" ist bei Metal die Flaeche von Eingabefeldern, "black" die Schrift. */
	private static final class DunklesTheme extends DefaultMetalTheme {

		private static ColorUIResource farbe(int rgb) {
			return new ColorUIResource(rgb);
		}

		@Override
		public String getName() {
			return "Dunkel";
		}

		@Override
		protected ColorUIResource getPrimary1() {
			return farbe(0x6C9FD8);
		}

		@Override
		protected ColorUIResource getPrimary2() {
			return farbe(0x3A6EA8);
		}

		@Override
		protected ColorUIResource getPrimary3() {
			return farbe(0x2D4A6B);
		}

		@Override
		protected ColorUIResource getSecondary1() {
			return farbe(0x5A606B);
		}

		@Override
		protected ColorUIResource getSecondary2() {
			return farbe(0x3A3F47);
		}

		@Override
		protected ColorUIResource getSecondary3() {
			return farbe(0x1E2126);
		}

		@Override
		protected ColorUIResource getWhite() {
			return farbe(0x2A2E35);
		}

		@Override
		protected ColorUIResource getBlack() {
			return farbe(0xE8EAED);
		}
	}
}
