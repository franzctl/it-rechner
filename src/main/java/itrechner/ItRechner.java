package itrechner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComponent;

public class ItRechner extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Color HINTERGRUND 	= new Color(0xF2, 0xF3, 0xF5);
	private static final Color FLAECHE     	= new Color(0xFF, 0xFF, 0xFF);
	private static final Color TASTE       	= new Color(0xE4, 0xE6, 0xEA);
	private static final Color TEXT        	= new Color(0x1A, 0x1A, 0x1A);
	private static final Color TEXT_HELL	= new Color(0xFF, 0xFF, 0xFF);
	private static final Color GRAU        	= new Color(0x8A, 0x90, 0xA8);
	private static final Color OPERATOR 	= new Color(0xF5, 0x8A, 0x1F);
	private static final Color GLEICH   	= new Color(0x1E, 0x88, 0xE5);

	private final Map<String, JButton> tastenListe = new HashMap<String, JButton>();

	private static final String[] SCHREIBWEISE = {
			"Zahlensysteme",
			"  0xFF        Hexadezimal          255",
			"  0b1010      Binaer                10",
			"  0o17        Oktal                 15",
			"",
			"Rechnen",
			"  2**8        Potenz               256",
			"  17%5        Rest der Division      2",
			"",
			"Funktionen (Winkel in Grad)",
			"  sin(90)     Sinus                  1",
			"  cos(0)      Kosinus                1",
			"  tan(45)     Tangens                1",
			"  wurzel(16)  Quadratwurzel          4",
			"  ln(1)       natuerl. Logarithmus   0",
			"  log(1000)   Zehnerlogarithmus      3",
			"  abs(-7)     Betrag                 7",
			"  pi          Kreiszahl           3.14",
			"",
			"Bit-Operationen",
			"  12 & 10     UND                    8",
			"  12 | 10     ODER                  14",
			"  12 ^ 10     XOR                    6",
			"  ~5          NICHT                 -6",
			"  1 << 8      links schieben       256",
			"  256 >> 4    rechts schieben       16",
			"",
			"Sonstiges",
			"  Ans         letztes Ergebnis",
			"",
			"Achtung: ^ ist XOR, nicht die Potenz -",
			"genau wie in Java. Die Potenz ist **." };

	private static final String[] TASTENKUERZEL = {
			"Eingabe",
			"  0-9 . und + - * / ( )    direkt eintippen",
			"  a-f                      Hex-Ziffern (nur im Erweitert-Modus)",
			"  % & | ^ ~                Bit-Operationen",
			"  Komma                    wird zum Punkt",
			"",
			"Steuerung",
			"  Enter                    rechnen (wie =)",
			"  Entf                     alles loeschen (wie C)",
			"  Rueckschritt             letztes Zeichen weg",
			"  Escape                   Programm beenden",
			"",
			"Menue",
			"  Strg + N                 Neu",
			"  Strg + E                 Verlauf im Editor oeffnen",
			"  Strg + Q                 Beenden",
			"  Strg + C                 Ergebnis kopieren",
			"  Strg + V                 Einfuegen",
			"  Strg + I                 Erweitert ein/aus",
			"  Strg + H                 Verlauf anzeigen",
			"  F1                       Schreibweise",
			"  Shift + F1               diese Uebersicht" };

	private final JLabel zeileOben = new JLabel(" ", SwingConstants.RIGHT);

	private final JLabel zeileUnten = new JLabel("0", SwingConstants.RIGHT);

	private String eingabe = "";

	private boolean ergebnisSteht = false;

	private final Rechnerkern kern = new Rechnerkern();

	private final DefaultListModel<String> verlaufDaten = new DefaultListModel<String>();

	private final JPanel itBereich = new JPanel(new BorderLayout());

	private final JCheckBoxMenuItem erweitertModus = new JCheckBoxMenuItem("Erweitert");

	private final JLabel anzeigeHex = new JLabel("HEX  -");
	private final JLabel anzeigeDez = new JLabel("DEZ  0");
	private final JLabel anzeigeOkt = new JLabel("OKT  -");
	private final JLabel anzeigeBin = new JLabel("BIN  -");

	private final JTextField feldIp = new JTextField("192.168.178.10");
	private final JTextField feldPraefix = new JTextField("24");
	private final JTextArea ausgabeSubnetz = new JTextArea();

	private final JTextArea ausgabeAscii = new JTextArea();
	private final JTextField feldAscii = new JTextField();

	private final JTextField feldMenge = new JTextField("1");
	private final JComboBox<String> auswahlEinheit = new JComboBox<String>(
			new String[] { "Byte", "KiB", "MiB", "GiB", "TiB", "KB", "MB", "GB", "TB" });
	private final JTextArea ausgabeGroessen = new JTextArea();

	// ===================== Start ==========================================
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ItRechner().setVisible(true);
			}
		});
	}

	public ItRechner() {
		setTitle("IT-Rechner");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setJMenuBar(baueMenueleiste());

		JPanel inhalt = new JPanel(new BorderLayout(12, 12));
		inhalt.setBackground(HINTERGRUND);
		inhalt.setBorder(new EmptyBorder(14, 14, 14, 14));
		setContentPane(inhalt);

		itBereich.setBackground(HINTERGRUND);
		itBereich.setBorder(new EmptyBorder(0, 12, 0, 0));
		itBereich.setVisible(false);

		JTabbedPane reiter = new JTabbedPane();
		reiter.addTab("IT", baueItTasten());
		reiter.addTab("Wissenschaft", baueWissenschaftTasten());
		reiter.addTab("Subnetz", baueSubnetzReiter());
		reiter.addTab("ASCII", baueAsciiReiter());
		reiter.addTab("Größen", baueGroessenReiter());

		itBereich.add(baueZahlensysteme(), BorderLayout.NORTH);
		itBereich.add(reiter, BorderLayout.CENTER);

		inhalt.add(baueAnzeige(), BorderLayout.NORTH);
		inhalt.add(baueTastenbereich(), BorderLayout.CENTER);
		inhalt.add(itBereich, BorderLayout.EAST);

		pack();
		baueTastatur();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
	}

	// ===================== Menueleiste ====================================
	private JMenuBar baueMenueleiste() {
		JMenuBar leiste = new JMenuBar();

		JMenu datei = new JMenu("Datei");
		JMenuItem neu = new JMenuItem("Neu");
		neu.setAccelerator(KeyStroke.getKeyStroke("control N"));
		neu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				eingabe = "";
				zeileOben.setText(" ");
				zeigeEingabe();
			}
		});
		datei.add(neu);

		JMenuItem inEditor = new JMenuItem("Verlauf im Editor öffnen");
		inEditor.setAccelerator(KeyStroke.getKeyStroke("control E"));
		inEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder text = new StringBuilder();
				for (int i = 0; i < verlaufDaten.size(); i++) {
					text.append(verlaufDaten.get(i)).append("\n");
				}
				Editor fenster = new Editor();
				fenster.setzeText(text.toString());
				fenster.setVisible(true);
			}
		});
		datei.add(inEditor);

		JMenuItem beenden = new JMenuItem("Beenden");
		beenden.setAccelerator(KeyStroke.getKeyStroke("control Q"));
		beenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		datei.add(beenden);

		JMenu bearbeiten = new JMenu("Bearbeiten");

		JMenuItem kopieren = new JMenuItem("Ergebnis kopieren");
		kopieren.setAccelerator(KeyStroke.getKeyStroke("control C"));
		kopieren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kopiereErgebnis();
			}
		});
		bearbeiten.add(kopieren);

		JMenuItem einfuegen = new JMenuItem("Einfügen");
		einfuegen.setAccelerator(KeyStroke.getKeyStroke("control V"));
		einfuegen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fuegeEin();
			}
		});
		bearbeiten.add(einfuegen);

		JMenu ansicht = new JMenu("Ansicht");

		erweitertModus.setAccelerator(KeyStroke.getKeyStroke("control I"));
		erweitertModus.addActionListener(e -> klappeErweitert(erweitertModus.isSelected()));
		ansicht.add(erweitertModus);

		JMenuItem verlaufZeigen = new JMenuItem("Verlauf anzeigen");
		verlaufZeigen.setAccelerator(KeyStroke.getKeyStroke("control H"));
		verlaufZeigen.addActionListener(e -> {
			if (verlaufDaten.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Noch keine Rechnungen vorhanden.");
				return;
			}
			String[] zeilen = new String[verlaufDaten.size()];
			verlaufDaten.copyInto(zeilen);
			zeigeTextfenster("Verlauf", zeilen);
		});
		ansicht.add(verlaufZeigen);

		JMenu hilfe = new JMenu("Hilfe");
		JMenuItem schreibweise = new JMenuItem("Schreibweise");
		schreibweise.setAccelerator(KeyStroke.getKeyStroke("F1"));
		schreibweise.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zeigeTextfenster("Schreibweise", SCHREIBWEISE);
			}
		});
		hilfe.add(schreibweise);

		JMenuItem ueber = new JMenuItem("Ueber");
		ueber.setAccelerator(KeyStroke.getKeyStroke("shift F1"));
		ueber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(ItRechner.this,
						"IT-Rechner\nSchkommodau, Franz");
			}
		});
		hilfe.add(ueber);

		JMenuItem tastenkuerzel = new JMenuItem("Tastenkürzel");
		tastenkuerzel.setAccelerator(KeyStroke.getKeyStroke("shift F2"));
		tastenkuerzel.addActionListener(e -> zeigeTextfenster("Tastenkürzel", TASTENKUERZEL));
		hilfe.add(tastenkuerzel);

		leiste.add(datei);
		leiste.add(bearbeiten);
		leiste.add(ansicht);
		leiste.add(hilfe);

		return leiste;
	}

	// ===================== Anzeige ========================================
	private JPanel baueAnzeige() {
		JPanel feld = new JPanel(new GridLayout(2, 1, 4, 4));
		feld.setBackground(FLAECHE);
		feld.setBorder(new EmptyBorder(12, 16, 12, 16));

		zeileOben.setForeground(GRAU);
		zeileOben.setFont(new Font("SansSerif", Font.PLAIN, 15));

		zeileUnten.setForeground(TEXT);
		zeileUnten.setFont(new Font("SansSerif", Font.BOLD, 32));

		feld.add(zeileOben);
		feld.add(zeileUnten);
		return feld;
	}

	private void zeigeEingabe() {
		zeileUnten.setText(eingabe.isEmpty() ? "0" : eingabe);
	}

	private void zeigeErgebnis(String rechnung, String ergebnis) {
		zeileOben.setText(rechnung + " =");
		zeileUnten.setText(ergebnis);
	}

	private void fuegeEin() {
		try {
			Clipboard ablage = Toolkit.getDefaultToolkit().getSystemClipboard();
			String wert = (String) ablage.getData(DataFlavor.stringFlavor);
			eingabe = eingabe + wert.trim();
			zeigeEingabe();
		} catch (Exception fehler) {
			JOptionPane.showMessageDialog(this, "In der Zwischenablage steht kein Text.");
		}
	}

	private void kopiereErgebnis() {
		String wert = zeileUnten.getText();
		StringSelection inhalt = new StringSelection(wert);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(inhalt, null);
	}

	private void zeigeTextfenster(String titel, String[] zeilen) {
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < zeilen.length; i++) {
			text.append(zeilen[i]).append("\n");
		}

		JTextArea flaeche = new JTextArea(text.toString());
		flaeche.setEditable(false);
		flaeche.setFont(new Font("Monospaced", Font.PLAIN, 12));
		flaeche.setBackground(null);

		JOptionPane.showMessageDialog(this, flaeche, titel,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void klappeErweitert(boolean sichtbar) {
		itBereich.setVisible(sichtbar);
		erweitertModus.setSelected(sichtbar);

		setMinimumSize(null);
		pack();
		setMinimumSize(getSize());

	}

	private JPanel baueZahlensysteme() {
		JPanel feld = new JPanel(new GridLayout(4, 1, 0, 4));
		feld.setBackground(FLAECHE);
		feld.setBorder(new EmptyBorder(10, 14, 10, 14));

		JLabel[] alle = { anzeigeHex, anzeigeDez, anzeigeOkt, anzeigeBin };
		for (int i = 0; i < alle.length; i++) {
			alle[i].setForeground(TEXT);
			alle[i].setFont(new Font("Monospaced", Font.PLAIN, 13));
			feld.add(alle[i]);
		}
		return feld;
	}

	private void zeigeZahlensysteme(double wert) {
		if (wert != Math.floor(wert) || Double.isInfinite(wert) || Double.isNaN(wert)) {
			anzeigeHex.setText("HEX  -");
			anzeigeDez.setText("DEZ  " + Rechnerkern.formatiere(wert));
			anzeigeOkt.setText("OKT  -");
			anzeigeBin.setText("BIN  -");
			return;
		}
		long ganz = (long) wert;
		anzeigeHex.setText("HEX  " + Long.toHexString(ganz).toUpperCase());
		anzeigeDez.setText("DEZ  " + ganz);
		anzeigeOkt.setText("OKT  " + Long.toOctalString(ganz));
		anzeigeBin.setText("BIN  " + inViererGruppen(Long.toBinaryString(ganz)));
	}

	private String inViererGruppen(String bits) {
		StringBuilder gebaut = new StringBuilder();
		int erste = bits.length() % 4;
		for (int i = 0; i < bits.length(); i++) {
			if (i > 0 && (i - erste) % 4 == 0) {
				gebaut.append(' ');
			}
			gebaut.append(bits.charAt(i));
		}
		return gebaut.toString();
	}

	/** Reiter fuer den Subnetzrechner. */
	private JPanel baueSubnetzReiter() {
		JPanel eingaben = new JPanel(new GridLayout(2, 2, 6, 6));
		eingaben.setBackground(HINTERGRUND);
		eingaben.add(beschriftung("IP-Adresse"));
		eingaben.add(feldIp);
		eingaben.add(beschriftung("Praefix (CIDR)"));
		eingaben.add(feldPraefix);

		JButton rechnen = new JButton("Netz berechnen");
		rechnen.setBackground(OPERATOR);
		rechnen.setForeground(TEXT);
		rechnen.setFocusPainted(false);
		rechnen.addActionListener(e -> berechneNetz());

		JPanel kopf = new JPanel(new BorderLayout(0, 6));
		kopf.setBackground(HINTERGRUND);
		kopf.add(eingaben, BorderLayout.CENTER);
		kopf.add(rechnen, BorderLayout.SOUTH);

		ausgabeSubnetz.setEditable(false);
		((javax.swing.text.DefaultCaret) ausgabeSubnetz.getCaret()).setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
		ausgabeSubnetz.setBackground(FLAECHE);
		ausgabeSubnetz.setForeground(TEXT);
		ausgabeSubnetz.setFont(new Font("Monospaced", Font.PLAIN, 12));
		ausgabeSubnetz.setBorder(new EmptyBorder(8, 8, 8, 8));

		JScrollPane roller = new JScrollPane(ausgabeSubnetz);
		roller.setPreferredSize(new Dimension(300, 200));
		roller.setBorder(null);

		JPanel reiter = new JPanel(new BorderLayout(0, 8));
		reiter.setBackground(HINTERGRUND);
		reiter.setBorder(new EmptyBorder(10, 10, 10, 10));
		reiter.add(kopf, BorderLayout.NORTH);
		reiter.add(roller, BorderLayout.CENTER);
		return reiter;
	}

	/** Kleine graue Beschriftung fuer Eingabefelder. */
	private JLabel beschriftung(String text) {
		JLabel schild = new JLabel(text);
		schild.setForeground(GRAU);
		schild.setFont(new Font("SansSerif", Font.PLAIN, 12));
		return schild;
	}

	/** Liest die Eingabefelder und zeigt den Bericht an. */
	private void berechneNetz() {
		try {
			int praefix = Integer.parseInt(feldPraefix.getText().trim().replace("/", ""));
			ausgabeSubnetz.setText(Subnetting.bericht(feldIp.getText().trim(), praefix));
		} catch (NumberFormatException fehler) {
			ausgabeSubnetz.setText("Praefix muss eine Zahl von 0 bis 32 sein.");
		} catch (IllegalArgumentException fehler) {
			ausgabeSubnetz.setText(fehler.getMessage());
		}
	}

	/** Reiter fuer die ASCII-Tabelle. */
	private JPanel baueAsciiReiter() {
		JPanel eingaben = new JPanel(new BorderLayout(6, 0));
		eingaben.setBackground(HINTERGRUND);
		eingaben.add(beschriftung("Zahl oder Zeichen"), BorderLayout.WEST);
		eingaben.add(feldAscii, BorderLayout.CENTER);

		JButton zeigen = new JButton("Umrechnen");
		zeigen.setBackground(OPERATOR);
		zeigen.setForeground(TEXT);
		zeigen.setFocusPainted(false);
		zeigen.addActionListener(e -> zeigeAscii());

		JPanel kopf = new JPanel(new BorderLayout(0, 6));
		kopf.setBackground(HINTERGRUND);
		kopf.add(eingaben, BorderLayout.CENTER);
		kopf.add(zeigen, BorderLayout.SOUTH);

		ausgabeAscii.setEditable(false);
		((javax.swing.text.DefaultCaret) ausgabeAscii.getCaret()).setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
		ausgabeAscii.setBackground(FLAECHE);
		ausgabeAscii.setForeground(TEXT);
		ausgabeAscii.setFont(new Font("Monospaced", Font.PLAIN, 12));
		ausgabeAscii.setBorder(new EmptyBorder(8, 8, 8, 8));

		JScrollPane roller = new JScrollPane(ausgabeAscii);
		roller.setPreferredSize(new Dimension(300, 200));
		roller.setBorder(null);

		JPanel reiter = new JPanel(new BorderLayout(0, 8));
		reiter.setBackground(HINTERGRUND);
		reiter.setBorder(new EmptyBorder(10, 10, 10, 10));
		reiter.add(kopf, BorderLayout.NORTH);
		reiter.add(roller, BorderLayout.CENTER);
		return reiter;
	}

	/** Zeigt ein Zeichen in allen Zahlensystemen - oder umgekehrt. */
	private void zeigeAscii() {
		String eingegeben = feldAscii.getText().trim();
		if (eingegeben.isEmpty()) {
			return;
		}

		int code;
		try {
			code = Integer.parseInt(eingegeben);
		} catch (NumberFormatException fehler) {
			code = eingegeben.charAt(0);
		}

		if (code < 0 || code > 0x10FFFF) {
			ausgabeAscii.setText("Zahl liegt ausserhalb des gueltigen Bereichs.");
			return;
		}

		StringBuilder text = new StringBuilder();
		text.append("Zeichen   ").append(zeichenName(code)).append("\n");
		text.append("DEZ       ").append(code).append("\n");
		text.append("HEX       ").append(Integer.toHexString(code).toUpperCase()).append("\n");
		text.append("OKT       ").append(Integer.toOctalString(code)).append("\n");
		text.append("BIN       ").append(inViererGruppen(Integer.toBinaryString(code))).append("\n");
		ausgabeAscii.setText(text.toString());
	}

	/** Steuerzeichen haben keine sichtbare Darstellung. */
	private String zeichenName(int code) {
		if (code < 32) {
			return "(Steuerzeichen)";
		}
		if (code == 32) {
			return "(Leerzeichen)";
		}
		if (code == 127) {
			return "(Entfernen)";
		}
		return String.valueOf((char) code);
	}

	/** Reiter fuer Datenmengen: KiB rechnet mit 1024, KB mit 1000. */
	private JPanel baueGroessenReiter() {
		JPanel eingaben = new JPanel(new GridLayout(2, 2, 6, 6));
		eingaben.setBackground(HINTERGRUND);
		eingaben.add(beschriftung("Menge"));
		eingaben.add(feldMenge);
		eingaben.add(beschriftung("Einheit"));
		eingaben.add(auswahlEinheit);

		JButton rechnen = new JButton("Umrechnen");
		rechnen.setBackground(OPERATOR);
		rechnen.setForeground(TEXT);
		rechnen.setFocusPainted(false);
		rechnen.addActionListener(e -> berechneGroessen());

		JPanel kopf = new JPanel(new BorderLayout(0, 6));
		kopf.setBackground(HINTERGRUND);
		kopf.add(eingaben, BorderLayout.CENTER);
		kopf.add(rechnen, BorderLayout.SOUTH);

		ausgabeGroessen.setEditable(false);
		((javax.swing.text.DefaultCaret) ausgabeGroessen.getCaret()).setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
		ausgabeGroessen.setBackground(FLAECHE);
		ausgabeGroessen.setForeground(TEXT);
		ausgabeGroessen.setFont(new Font("Monospaced", Font.PLAIN, 12));
		ausgabeGroessen.setBorder(new EmptyBorder(8, 8, 8, 8));

		JScrollPane roller = new JScrollPane(ausgabeGroessen);
		roller.setPreferredSize(new Dimension(300, 200));
		roller.setBorder(null);

		JPanel reiter = new JPanel(new BorderLayout(0, 8));
		reiter.setBackground(HINTERGRUND);
		reiter.setBorder(new EmptyBorder(10, 10, 10, 10));
		reiter.add(kopf, BorderLayout.NORTH);
		reiter.add(roller, BorderLayout.CENTER);
		return reiter;
	}

	private void berechneGroessen() {
		double menge;
		try {
			menge = Double.parseDouble(feldMenge.getText().trim().replace(",", "."));
		} catch (NumberFormatException fehler) {
			ausgabeGroessen.setText("Bitte eine Zahl eingeben.");
			return;
		}

		String einheit = (String) auswahlEinheit.getSelectedItem();
		double byteWert = menge * faktor(einheit);

		StringBuilder text = new StringBuilder();
		text.append(groessenZeile("Byte", byteWert)).append("\n");
		text.append(groessenZeile("KiB", byteWert / 1024d));
		text.append(groessenZeile("MiB", byteWert / 1024d / 1024d));
		text.append(groessenZeile("GiB", byteWert / 1024d / 1024d / 1024d));
		text.append(groessenZeile("TiB", byteWert / 1024d / 1024d / 1024d / 1024d)).append("\n");
		text.append(groessenZeile("KB", byteWert / 1000d));
		text.append(groessenZeile("MB", byteWert / 1000d / 1000d));
		text.append(groessenZeile("GB", byteWert / 1000d / 1000d / 1000d));
		text.append(groessenZeile("TB", byteWert / 1000d / 1000d / 1000d / 1000d)).append("\n");
		text.append(groessenZeile("Bit", byteWert * 8));
		ausgabeGroessen.setText(text.toString());
	}

	/** Wie viele Byte steckt in einer Einheit? */
	private double faktor(String einheit) {
		if (einheit.equals("KiB")) return 1024d;
		if (einheit.equals("MiB")) return 1024d * 1024d;
		if (einheit.equals("GiB")) return 1024d * 1024d * 1024d;
		if (einheit.equals("TiB")) return 1024d * 1024d * 1024d * 1024d;
		if (einheit.equals("KB")) return 1000d;
		if (einheit.equals("MB")) return 1000d * 1000d;
		if (einheit.equals("GB")) return 1000d * 1000d * 1000d;
		if (einheit.equals("TB")) return 1000d * 1000d * 1000d * 1000d;
		return 1d;
	}

	private String groessenZeile(String einheit, double wert) {
		while (einheit.length() < 5) {
			einheit = einheit + " ";
		}
		return einheit + " " + Rechnerkern.formatiere(wert) + "\n";
	}

	// ===================== Tasten =============================
	private JPanel baueTastenbereich() {
		JPanel feld = new JPanel(new GridLayout(5, 4, 10, 10));
		feld.setBackground(HINTERGRUND);

		String[] beschriftungen = {
				"C",   "DEL", "(", ")",
				"7",   "8",  "9",  "/",
				"4",   "5",  "6",  "*",
				"1",   "2",  "3",  "-",
				"+/-", "0",  ".",  "+", };

		for (int i = 0; i < beschriftungen.length; i++) {
			feld.add(taste(beschriftungen[i]));
		}

		JButton gleich = taste("=");
		gleich.setMinimumSize(new Dimension(0, 40));

		JPanel bereich = new JPanel(new BorderLayout(0, 10));
		bereich.setBackground(HINTERGRUND);
		bereich.add(feld, BorderLayout.CENTER);
		bereich.add(gleich, BorderLayout.SOUTH);
		return bereich;
	}

	private JButton taste(String beschriftung) {
		final Color farbe = farbeFuer(beschriftung);

		JButton knopf = new JButton(beschriftung) {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(getModel().isArmed() ? farbe.darker() : farbe);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		knopf.setContentAreaFilled(false);
		knopf.setBorderPainted(false);
		knopf.setFocusPainted(false);
		knopf.setForeground(beschriftung.equals("=") ? TEXT_HELL : TEXT);
		knopf.setFont(new Font("SansSerif", Font.PLAIN, 20));
		knopf.addActionListener(e -> tasteGedrueckt(beschriftung));

		if (beschriftung.equals("C_")) {
			knopf.setText("C");
		}

		tastenListe.put(beschriftung, knopf);
		return knopf;
	}

	private Color farbeFuer(String beschriftung) {
		if (beschriftung.equals("=")) {
			return GLEICH;
		}
		String[] operatoren = { "C", "DEL", "(", ")", "%", "+/-", "/", "*", "-", "+",
				"&", "|", "^", "~", "<<", ">>", "**" };
		for (int i = 0; i < operatoren.length; i++) {
			if (beschriftung.equals(operatoren[i])) {
				return OPERATOR;
			}
		}
		return TASTE;
	}

	private void tasteGedrueckt(String beschriftung) {

		if (ergebnisSteht) {
			ergebnisSteht = false;
			if (!"+-*/%&|^".contains(beschriftung) && !beschriftung.equals("**")
					&& !beschriftung.equals("=") && !beschriftung.equals("DEL")) {
				eingabe = "";
				zeileOben.setText(" ");
			}
		}

		if (beschriftung.equals("C")) {
			eingabe = "";
			zeileOben.setText(" ");
		} else if (beschriftung.equals("DEL")) {
			if (eingabe.length() > 0) {
				eingabe = eingabe.substring(0, eingabe.length() - 1);
			}
		} else if (beschriftung.equals("=")) {
			berechne();
			return;
		} else if (beschriftung.equals("C_")) {
			eingabe = eingabe + "C";
		} else if (beschriftung.equals(".")) {
			if (erlaubtKomma()) {
				eingabe = eingabe + ".";
			}
		} else if (beschriftung.equals("+/-")) {
			if (eingabe.startsWith("-")) {
				eingabe = eingabe.substring(1);
			} else {
				eingabe = "-" + eingabe;
			}
		} else {
			if (eingabe.equals("0") && Character.isDigit(beschriftung.charAt(0))) {
				eingabe = beschriftung;
			} else {
				eingabe = eingabe + beschriftung;
			}
		}
		zeigeEingabe();
	}

	private JPanel baueItTasten() {
		JPanel feld = new JPanel(new GridLayout(4, 4, 8, 8));
		feld.setBackground(HINTERGRUND);
		feld.setBorder(new EmptyBorder(10, 0, 0, 0));

		String[] beschriftungen = {
				"A",  "B",  "C_", "D",
				"E",  "F",  "0x", "0b",
				"0o", "&",  "|",  "^",
				"~",  "<<", ">>", "%" };

		for (int i = 0; i < beschriftungen.length; i++) {
			feld.add(taste(beschriftungen[i]));
		}
		return feld;
	}

	private JPanel baueWissenschaftTasten() {
		JPanel feld = new JPanel(new GridLayout(3, 3, 8, 8));
		feld.setBackground(HINTERGRUND);
		feld.setBorder(new EmptyBorder(10, 10, 10, 10));

		String[] beschriftungen = {
				"sin(", "cos(", "tan(",
				"wurzel(", "ln(", "log(",
				"abs(", "pi", "**" };

		for (int i = 0; i < beschriftungen.length; i++) {
			feld.add(taste(beschriftungen[i]));
		}
		return feld;
	}

	private void baueTastatur() {
		JComponent wurzel = getRootPane();
		InputMap eingaben = wurzel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap aktionen = wurzel.getActionMap();

		String zeichen = "0123456789+-*/().%&|^~";
		for (int i = 0; i < zeichen.length(); i++) {
			char c = zeichen.charAt(i);
			verbinde(eingaben, aktionen, KeyStroke.getKeyStroke(c), String.valueOf(c));
		}

		String hex = "abcdef";
		for (int i = 0; i < hex.length(); i++) {
			char c = hex.charAt(i);
			String ziel = (c == 'c') ? "C_" : String.valueOf(c).toUpperCase();
			verbinde(eingaben, aktionen, KeyStroke.getKeyStroke(c), ziel);
			verbinde(eingaben, aktionen, KeyStroke.getKeyStroke(Character.toUpperCase(c)), ziel);
		}

		verbinde(eingaben, aktionen, KeyStroke.getKeyStroke("ESCAPE"), "BEENDEN");
		verbinde(eingaben, aktionen, KeyStroke.getKeyStroke("ENTER"), "=");
		verbinde(eingaben, aktionen, KeyStroke.getKeyStroke("DELETE"), "C");
		verbinde(eingaben, aktionen, KeyStroke.getKeyStroke("BACK_SPACE"), "DEL");
		verbinde(eingaben, aktionen, KeyStroke.getKeyStroke(','), ".");
	}

	private void verbinde(InputMap eingaben, ActionMap aktionen, KeyStroke taste,
			final String beschriftung) {
		if (taste == null) {
			return;
		}
		String name = "taste_" + beschriftung;
		eingaben.put(taste, name);
		aktionen.put(name, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				druecke(beschriftung);
			}
		});
	}

	private void druecke(String beschriftung) {
		if (beschriftung.equals("BEENDEN")) {
			System.exit(0);
			return;
		}
		JButton knopf = tastenListe.get(beschriftung);
		if (knopf != null && knopf.isShowing()) {
			knopf.doClick();
		}
	}

//===================== Rechnen ========================================

	private void berechne() {
		if (eingabe.isEmpty()) {
			return;
		}
		try {
			double wert = kern.auswerten(eingabe);
			String ergebnis = Rechnerkern.formatiere(wert);
			verlaufDaten.addElement(eingabe + " = " + ergebnis);
			zeigeErgebnis(eingabe, ergebnis);
			zeigeZahlensysteme(wert);
			kern.setAns(wert);
			eingabe = "Ans";
			ergebnisSteht = true;
		} catch (RuntimeException fehler) {
			zeileOben.setText(eingabe + " =");
			zeileUnten.setText(fehler.getMessage());
		}
	}

	private boolean erlaubtKomma() {
		for (int i = eingabe.length() - 1; i >= 0; i--) {
			char zeichen = eingabe.charAt(i);
			if (zeichen == '.') {
				return false;
			}
			if (!Character.isDigit(zeichen)) {
				return true;
			}
		}
		return true;
	}

}
