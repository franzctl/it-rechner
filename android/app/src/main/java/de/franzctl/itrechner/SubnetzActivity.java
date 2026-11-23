package de.franzctl.itrechner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class SubnetzActivity extends AppCompatActivity {

    private EditText feldIp;
    private EditText feldPraefix;
    private TextView ausgabe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subnetz);
        setSupportActionBar(findViewById(R.id.toolbar));
        setTitle("Subnetzrechner");

        Toolbar leiste = findViewById(R.id.toolbar);
        leiste.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        leiste.setNavigationOnClickListener(v -> finish());

        feldIp = findViewById(R.id.feldIp);
        feldPraefix = findViewById(R.id.feldPraefix);
        ausgabe = findViewById(R.id.ausgabe);

        Button rechnen = findViewById(R.id.knopfRechnen);
        rechnen.setOnClickListener(v -> berechneNetz());
    }

    /**
     * Liest die Eingabefelder und zeigt den Bericht an.
     */
    private void berechneNetz() {
        try {
            int praefix = Subnetting.leseAngabe(feldPraefix.getText().toString());
            ausgabe.setText(Subnetting.bericht(feldIp.getText().toString().trim(), praefix));
        } catch (NumberFormatException fehler) {
            ausgabe.setText("Präfix muss eine Zahl von 0 bis 32 oder eine Maske sein.");
        } catch (IllegalArgumentException fehler) {
            ausgabe.setText(fehler.getMessage());
        }
    }
}