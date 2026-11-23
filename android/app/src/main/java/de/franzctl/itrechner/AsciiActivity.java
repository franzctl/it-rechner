package de.franzctl.itrechner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AsciiActivity extends AppCompatActivity {

    private EditText feld;
    private TextView ausgabe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ascii);
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setTitle("Zeichen und Codes");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        feld = findViewById(R.id.feldAscii);
        ausgabe = findViewById(R.id.ausgabeAscii);

        Button umrechnen = findViewById(R.id.knopfUmrechnen);
        umrechnen.setOnClickListener(v -> rechne());
    }

    /** Versteht eine Zahl oder ein Zeichen und zeigt beide Richtungen. */
    private void rechne() {
        String eingegeben = feld.getText().toString().trim();
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
            ausgabe.setText("Zahl liegt außerhalb des gültigen Bereichs.");
            return;
        }

        ausgabe.setText(
                "Zeichen   " + zeichenName(code) + "\n" +
                        "DEZ       " + code + "\n" +
                        "HEX       " + Integer.toHexString(code).toUpperCase() + "\n" +
                        "OKT       " + Integer.toOctalString(code) + "\n" +
                        "BIN       " + Integer.toBinaryString(code));
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}