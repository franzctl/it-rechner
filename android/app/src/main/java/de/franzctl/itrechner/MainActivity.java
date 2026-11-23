package de.franzctl.itrechner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Farben kommen aus res/values und res/values-night, je nach hellem oder dunklem Design
    private int farbeTaste;
    private int farbeOperator;
    private int farbeGleich;
    private int farbeText;
    private int farbeTextAufOperator;

    private final Rechnerkern kern = new Rechnerkern();
    private final Map<String, Button> tastenListe = new HashMap<>();

    private GridLayout tastenfeld;
    private LinearLayout modusleiste;
    private String modus = "Standard";

    private static final String[][] TASTEN = {
            // Standard
            { "C", "DEL", "(", ")",
                    "7", "8", "9", "/",
                    "4", "5", "6", "*",
                    "1", "2", "3", "-",
                    "+/-", "0", ".", "+" },
            // IT
            { "A", "B", "C_", "D",
                    "E", "F", "0x", "0b",
                    "0o", "&", "|", "^",
                    "~", "<<", ">>", "%" },
            // Wissenschaft
            { "sin(", "cos(", "tan(",
                    "wurzel(", "ln(", "log(",
                    "abs(", "pi", "**" } };

    private static final String SCHREIBWEISE =
            "Zahlensysteme\n" +
                    "  0xFF        Hexadezimal          255\n" +
                    "  0b1010      Binaer                10\n" +
                    "  0o17        Oktal                 15\n" +
                    "\n" +
                    "Rechnen\n" +
                    "  2**8        Potenz               256\n" +
                    "  17%5        Rest der Division      2\n" +
                    "\n" +
                    "Bit-Operationen\n" +
                    "  12 & 10     UND                    8\n" +
                    "  12 | 10     ODER                  14\n" +
                    "  12 ^ 10     XOR                    6\n" +
                    "  ~5          NICHT                 -6\n" +
                    "  1 << 8      links schieben       256\n" +
                    "  256 >> 4    rechts schieben       16\n" +
                    "\n" +
                    "Funktionen (Winkel in Grad)\n" +
                    "  sin(90)     Sinus                  1\n" +
                    "  wurzel(16)  Quadratwurzel          4\n" +
                    "  ln(1)       natuerl. Logarithmus   0\n" +
                    "  log(1000)   Zehnerlogarithmus      3\n" +
                    "  pi          Kreiszahl           3.14\n" +
                    "\n" +
                    "Sonstiges\n" +
                    "  Ans         letztes Ergebnis\n" +
                    "\n" +
                    "Achtung: ^ ist XOR, nicht die Potenz -\n" +
                    "genau wie in Java. Die Potenz ist **.";

    private static final String NACHSCHLAGEN =
            "Private Adressbereiche (RFC 1918)\n" +
                    "  10.0.0.0/8          10.0.0.0   - 10.255.255.255\n" +
                    "  172.16.0.0/12       172.16.0.0 - 172.31.255.255\n" +
                    "  192.168.0.0/16      192.168.0.0 - 192.168.255.255\n" +
                    "\n" +
                    "Sonderbereiche\n" +
                    "  127.0.0.0/8         Loopback\n" +
                    "  169.254.0.0/16      Link-Local (APIPA)\n" +
                    "  0.0.0.0/8           Dieses Netz\n" +
                    "\n" +
                    "Haeufige Ports\n" +
                    "  20/21   FTP           443   HTTPS\n" +
                    "  22      SSH           445   SMB\n" +
                    "  23      Telnet        3389  RDP\n" +
                    "  25      SMTP          3306  MySQL\n" +
                    "  53      DNS           5432  PostgreSQL\n" +
                    "  67/68   DHCP          8080  HTTP alt.\n" +
                    "  80      HTTP          514   Syslog\n" +
                    "  110     POP3          161   SNMP\n" +
                    "  143     IMAP          389   LDAP\n" +
                    "\n" +
                    "VLAN\n" +
                    "  0-1     reserviert\n" +
                    "  1-1005  Standardbereich\n" +
                    "  1006-4094  erweiterter Bereich\n" +
                    "\n" +
                    "Subnetzgroessen\n" +
                    "  /30   2 Hosts    /27   30 Hosts\n" +
                    "  /29   6 Hosts    /26   62 Hosts\n" +
                    "  /28   14 Hosts   /25   126 Hosts\n" +
                    "  /24   254 Hosts";

    private static final String[] MODI = { "Standard", "IT", "Wissenschaft" };


    private String eingabe = "";
    private boolean ergebnisSteht = false;

    private TextView zeileOben;
    private TextView zeileUnten;

    private TextView zahlensysteme;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        farbeTaste = getColor(R.color.taste);
        farbeOperator = getColor(R.color.operator);
        farbeGleich = getColor(R.color.gleich);
        farbeText = getColor(R.color.text);
        farbeTextAufOperator = getColor(R.color.text_auf_operator);
        setSupportActionBar(findViewById(R.id.toolbar));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        zeileOben = findViewById(R.id.zeileOben);
        zeileUnten = findViewById(R.id.zeileUnten);
        zahlensysteme = findViewById(R.id.zahlensysteme);

        tastenfeld = findViewById(R.id.tastenfeld);
        modusleiste = findViewById(R.id.modusleiste);
        if (savedInstanceState != null) {
            modus = savedInstanceState.getString("modus", "Standard");
        }
        baueModusleiste();
        fuelleTastenfeld();

        if (savedInstanceState != null) {
            eingabe = savedInstanceState.getString("eingabe", "");
            ergebnisSteht = savedInstanceState.getBoolean("ergebnisSteht", false);
            zeileOben.setText(savedInstanceState.getString("zeileOben", " "));
            zeileUnten.setText(savedInstanceState.getString("zeileUnten", "0"));
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle zustand) {
        super.onSaveInstanceState(zustand);
        zustand.putString("modus", modus);
        zustand.putString("eingabe", eingabe);
        zustand.putBoolean("ergebnisSteht", ergebnisSteht);
        zustand.putString("zeileOben", zeileOben.getText().toString());
        zustand.putString("zeileUnten", zeileUnten.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menue) {
        getMenuInflater().inflate(R.menu.hauptmenue, menue);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem eintrag) {
        int id = eintrag.getItemId();

        if (id == R.id.menue_kopieren) {
            kopiereErgebnis();
            return true;
        }
        if (id == R.id.menue_erweitert) {
            boolean an = !eintrag.isChecked();
            eintrag.setChecked(an);
            zahlensysteme.setVisibility(an ? View.VISIBLE : View.GONE);
            return true;
        }
        if (id == R.id.menue_design) {
            waehleDesign();
            return true;
        }
        if (id == R.id.menue_ueber) {
            new AlertDialog.Builder(this)
                    .setTitle("Über")
                    .setMessage("IT-Rechner\nSchkommodau, Franz")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        if (id == R.id.menue_schreibweise) {
            zeigeTextfenster("Schreibweise", SCHREIBWEISE);
            return true;
        }
        if (id == R.id.menue_nachschlagen) {
            zeigeTextfenster("Nachschlagen", NACHSCHLAGEN);
            return true;
        }
        if (id == R.id.menue_subnetz) {
            startActivity(new Intent(this, SubnetzActivity.class));
            return true;
        }
        if (id == R.id.menue_ascii) {
            startActivity(new Intent(this, AsciiActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(eintrag);
    }

    /** Auswahl zwischen Systemeinstellung, hell und dunkel. Die Wahl bleibt gespeichert. */
    private void waehleDesign() {
        final int[] modi = {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.MODE_NIGHT_YES };
        String[] namen = { "Wie das Handy", "Hell", "Dunkel" };

        int aktuell = 0;
        int gespeichert = ItRechnerApp.gespeichertesDesign(this);
        for (int i = 0; i < modi.length; i++) {
            if (modi[i] == gespeichert) {
                aktuell = i;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Design")
                .setSingleChoiceItems(namen, aktuell, (dialog, auswahl) -> {
                    dialog.dismiss();
                    ItRechnerApp.setzeDesign(this, modi[auswahl]);
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    /** Zeigt einen Text in Monospaced-Schrift, damit die Spalten stehen. */
    private void zeigeTextfenster(String titel, String inhalt) {
        TextView flaeche = new TextView(this);
        flaeche.setText(inhalt);
        flaeche.setTypeface(Typeface.MONOSPACE);
        flaeche.setTextSize(12);
        flaeche.setPadding(40, 30, 40, 10);
        flaeche.setTextIsSelectable(true);

        ScrollView roller = new ScrollView(this);
        roller.addView(flaeche);

        new AlertDialog.Builder(this)
                .setTitle(titel)
                .setView(roller)
                .setPositiveButton("OK", null)
                .show();
    }

    /** Legt das Ergebnis in die Zwischenablage. */
    private void kopiereErgebnis() {
        ClipboardManager ablage = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ablage.setPrimaryClip(ClipData.newPlainText("Ergebnis", zeileUnten.getText()));
        Toast.makeText(this, "Ergebnis kopiert", Toast.LENGTH_SHORT).show();
    }

    /** Erzeugt alle Tasten in einer Schleife - wie baueTastenbereich() in Swing. */
    /** Baut die drei Umschaltknoepfe ueber dem Tastenfeld. */
    private void baueModusleiste() {
        modusleiste.removeAllViews();
        for (int i = 0; i < MODI.length; i++) {
            final String name = MODI[i];
            Button knopf = new Button(this);
            knopf.setText(name);
            knopf.setTextSize(13);
            knopf.setAllCaps(false);
            knopf.setTextColor(name.equals(modus) ? Color.WHITE : farbeText);
            knopf.setBackgroundColor(name.equals(modus) ? farbeGleich : farbeTaste);
            knopf.setOnClickListener(v -> {
                modus = name;
                baueModusleiste();
                fuelleTastenfeld();
            });

            LinearLayout.LayoutParams masse = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            masse.setMargins(4, 0, 4, 0);
            knopf.setLayoutParams(masse);
            modusleiste.addView(knopf);
        }
    }

    /** Leert das Raster und fuellt es mit den Tasten des aktuellen Modus. */
    private void fuelleTastenfeld() {
        tastenfeld.removeAllViews();
        tastenListe.clear();

        int nummer = 0;
        for (int i = 0; i < MODI.length; i++) {
            if (MODI[i].equals(modus)) {
                nummer = i;
            }
        }
        String[] beschriftungen = TASTEN[nummer];

        int spalten = modus.equals("Wissenschaft") ? 3 : 4;
        tastenfeld.setColumnCount(spalten);

        for (int i = 0; i < beschriftungen.length; i++) {
            tastenfeld.addView(taste(beschriftungen[i]));
        }

        // Gleich-Taste nur im Standardmodus, ueber die volle Breite
        if (modus.equals("Standard")) {
            Button gleich = taste("=");
            GridLayout.LayoutParams masse = (GridLayout.LayoutParams) gleich.getLayoutParams();
            masse.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 4, 1f);
            gleich.setLayoutParams(masse);
            tastenfeld.addView(gleich);
        }
    }

    /** Baut eine einzelne Taste. */
    private Button taste(final String beschriftung) {
        Button knopf = new Button(this);
        knopf.setText(beschriftung);
        knopf.setTextSize(20);
        int hintergrund = farbeFuer(beschriftung);
        knopf.setTextColor(beschriftung.equals("=") ? Color.WHITE
                : hintergrund == farbeOperator ? farbeTextAufOperator : farbeText);
        knopf.setBackgroundColor(hintergrund);
        knopf.setOnClickListener(v -> tasteGedrueckt(beschriftung));

        GridLayout.LayoutParams masse = new GridLayout.LayoutParams();
        masse.width = 0;
        masse.height = 0;
        masse.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        masse.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        masse.setMargins(6, 6, 6, 6);
        knopf.setLayoutParams(masse);

        if (beschriftung.equals("C_")) {
            knopf.setText("C");
        }

        tastenListe.put(beschriftung, knopf);
        return knopf;
    }

    private int farbeFuer(String beschriftung) {
        if (beschriftung.equals("=")) {
            return farbeGleich;
        }

        if (beschriftung.equals("C_")) {
            return farbeTaste;
        }

        String[] operatoren = { "C", "DEL", "(", ")", "%", "+/-", "/", "*", "-", "+" };
        for (int i = 0; i < operatoren.length; i++) {
            if (beschriftung.equals(operatoren[i])) {
                return farbeOperator;
            }
        }
        return farbeTaste;
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

    /** Zeigt das Ergebnis in vier Zahlensystemen. */
    private void zeigeZahlensysteme(double wert) {
        if (wert != Math.floor(wert) || Double.isInfinite(wert) || Double.isNaN(wert)) {
            zahlensysteme.setText("HEX  -\nDEZ  " + Rechnerkern.formatiere(wert) + "\nOKT  -\nBIN  -");
            return;
        }
        long ganz = (long) wert;
        zahlensysteme.setText(
                "HEX  " + Long.toHexString(ganz).toUpperCase() + "\n" +
                        "DEZ  " + ganz + "\n" +
                        "OKT  " + Long.toOctalString(ganz) + "\n" +
                        "BIN  " + inViererGruppen(Long.toBinaryString(ganz)));
    }

    /** Aus 110110 wird 11 0110 - so liest sich Binaer leichter. */
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

    private void zeigeEingabe() {
        zeileUnten.setText(eingabe.isEmpty() ? "0" : eingabe);
    }

    private void zeigeErgebnis(String rechnung, String ergebnis) {
        zeileOben.setText(rechnung + " =");
        zeileUnten.setText(ergebnis);
    }

    private void berechne() {
        if (eingabe.isEmpty()) {
            return;
        }
        try {
            double wert = kern.auswerten(eingabe);
            String ergebnis = Rechnerkern.formatiere(wert);
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