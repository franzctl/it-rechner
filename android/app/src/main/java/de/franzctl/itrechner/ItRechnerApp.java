package de.franzctl.itrechner;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

/** Setzt beim Start das gespeicherte Design, noch bevor das erste Fenster erscheint. */
public class ItRechnerApp extends Application {

    static final String EINSTELLUNGEN = "einstellungen";
    static final String SCHLUESSEL_DESIGN = "design";

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(gespeichertesDesign(this));
    }

    /** Standard ist "wie das Handy", also die Systemeinstellung. */
    static int gespeichertesDesign(Context kontext) {
        return kontext.getSharedPreferences(EINSTELLUNGEN, MODE_PRIVATE)
                .getInt(SCHLUESSEL_DESIGN, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    /** Speichert die Wahl und wendet sie an. Android baut die offenen Seiten dabei neu auf. */
    static void setzeDesign(Context kontext, int modus) {
        kontext.getSharedPreferences(EINSTELLUNGEN, MODE_PRIVATE)
                .edit()
                .putInt(SCHLUESSEL_DESIGN, modus)
                .apply();
        AppCompatDelegate.setDefaultNightMode(modus);
    }
}
