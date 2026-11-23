package de.franzctl.itrechner;

import java.util.Locale;

public class Rechnerkern {

    private String text = "";
    private int pos = 0;
    private double ans = 0;

    public void setAns(double wert) {
        ans = wert;
    }

    public double auswerten(String eingabe) {
        text = eingabe.replace(" ", "");
        pos = 0;
        if (text.isEmpty()) {
            return 0;
        }
        double wert = bitOder();
        if (pos < text.length()) {
            throw new IllegalArgumentException("Unerwartetes Zeichen: " + text.charAt(pos));
        }
        return wert;
    }

    private double bitOder() {
        double wert = bitXor();
        while (pos < text.length() && text.charAt(pos) == '|') {
            pos++;
            wert = alsGanzzahl(wert) | alsGanzzahl(bitXor());
        }
        return wert;
    }

    private double bitXor() {
        double wert = bitUnd();
        while (pos < text.length() && text.charAt(pos) == '^') {
            pos++;
            wert = alsGanzzahl(wert) ^ alsGanzzahl(bitUnd());
        }
        return wert;
    }

    private double bitUnd() {
        double wert = schieben();
        while (pos < text.length() && text.charAt(pos) == '&') {
            pos++;
            wert = alsGanzzahl(wert) & alsGanzzahl(schieben());
        }
        return wert;
    }

    private double schieben() {
        double wert = summe();
        while (pos + 1 < text.length()) {
            if (text.charAt(pos) == '<' && text.charAt(pos + 1) == '<') {
                pos = pos + 2;
                wert = alsGanzzahl(wert) << alsGanzzahl(summe());
            } else if (text.charAt(pos) == '>' && text.charAt(pos + 1) == '>') {
                pos = pos + 2;
                wert = alsGanzzahl(wert) >> alsGanzzahl(summe());
            } else {
                break;
            }
        }
        return wert;
    }

    private double summe() {
        double wert = produkt();
        while (pos < text.length()) {
            char zeichen = text.charAt(pos);
            if(zeichen == '+') {
                pos++;
                wert = wert + produkt();
            } else if (zeichen == '-') {
                pos++;
                wert = wert - produkt();
            } else {
                break;
            }
        }
        return wert;
    }

    private double produkt() {
        double wert = potenz();
        while (pos < text.length()) {
            char zeichen = text.charAt(pos);
            if (zeichen == '*' && !(pos + 1 < text.length() && text.charAt(pos + 1) == '*')) {
                pos++;
                wert = wert * potenz();
            } else if (zeichen == '/') {
                pos++;
                double teiler = potenz();
                if (teiler == 0) {
                    throw new ArithmeticException("Division durch null");
                }
                wert = wert / teiler;
            } else if (zeichen == '%') {
                pos++;
                double teiler = potenz();
                if (teiler == 0) {
                    throw new ArithmeticException("Rest durch null");
                }
                wert = alsGanzzahl(wert) % alsGanzzahl(teiler);
            } else {
                break;
            }
        }
        return wert;
    }

    private double potenz() {
        double basis = faktor();
        if (pos + 1 < text.length() && text.charAt(pos) == '*' && text.charAt(pos + 1) == '*') {
            pos = pos + 2;
            return Math.pow(basis, potenz());
        }
        return basis;
    }

    private double faktor() {
        if (pos >= text.length()) {
            throw new IllegalArgumentException("Eingabe unvollstaendig");
        }
        char zeichen = text.charAt(pos);

        if (zeichen == '-') {
            pos++;
            return -faktor();
        }

        if (zeichen == '~') {
            pos++;
            return ~alsGanzzahl(faktor());
        }

        if (Character.isLetter(zeichen)) {
            return leseNamen();
        }

        if (zeichen == '(') {
            pos++;
            double wert = bitOder();
            if (pos >= text.length() || text.charAt(pos) != ')') {
                throw new IllegalArgumentException("Klammer nicht geschlossen");
            }
            pos++;
            return wert;
        }
        return leseZahl();
    }

    private double leseZahl() {
        if (text.startsWith("0x", pos) || text.startsWith("0X", pos)) {
            pos = pos + 2;
            return leseZiffern(16, "0123456789abcdef");
        }
        if (text.startsWith("0b", pos) || text.startsWith("0B", pos)) {
            pos = pos + 2;
            return leseZiffern(2, "01");
        }
        if (text.startsWith("0o", pos) || text.startsWith("0O", pos)) {
            pos = pos + 2;
            return leseZiffern(8, "01234567");
        }

        int start = pos;
        while (pos < text.length()
                && (Character.isDigit(text.charAt(pos)) || text.charAt(pos) == '.')) {
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Keine Zahl bei Zeichen " + (pos + 1));
        }
        return Double.parseDouble(text.substring(start, pos));
    }


    private double leseZiffern(int basis, String erlaubt) {
        int start = pos;
        long wert = 0;
        while (pos < text.length()) {
            int ziffer = erlaubt.indexOf(Character.toLowerCase(text.charAt(pos)));
            if (ziffer < 0) {
                break;
            }
            wert = wert * basis + ziffer;
            pos++;
        }
        if (start == pos) {
            throw new IllegalArgumentException("Keine Ziffer zur Basis " + basis);
        }
        return wert;
    }

    private double leseNamen() {
        int start = pos;
        while (pos < text.length() && Character.isLetter(text.charAt(pos))) {
            pos++;
        }
        String name = text.substring(start, pos).toLowerCase();

        if (name.equals("pi")) {
            return Math.PI;
        }
        if (name.equals("ans")) {
            return ans;
        }

        if (pos >= text.length() || text.charAt(pos) != '(') {
            throw new IllegalArgumentException("Klammer fehlt nach " + name);
        }
        pos++;
        double argument = bitOder();
        if (pos >= text.length() || text.charAt(pos) != ')') {
            throw new IllegalArgumentException("Klammer nicht geschlossen");
        }
        pos++;
        return wendeFunktionAn(name, argument);
    }


    private double wendeFunktionAn(String name, double wert) {
        if (name.equals("sin")) {
            return Math.sin(Math.toRadians(wert));
        }
        if (name.equals("cos")) {
            return Math.cos(Math.toRadians(wert));
        }
        if (name.equals("tan")) {
            return Math.tan(Math.toRadians(wert));
        }
        if (name.equals("wurzel")) {
            if (wert < 0) {
                throw new ArithmeticException("Wurzel aus negativer Zahl");
            }
            return Math.sqrt(wert);
        }
        if (name.equals("ln")) {
            if (wert <= 0) {
                throw new ArithmeticException("Logarithmus braucht eine positive Zahl");
            }
            return Math.log(wert);
        }
        if (name.equals("log")) {
            if (wert <= 0) {
                throw new ArithmeticException("Logarithmus braucht eine positive Zahl");
            }
            return Math.log10(wert);
        }
        if (name.equals("abs")) {
            return Math.abs(wert);
        }
        throw new IllegalArgumentException("Unbekannte Funktion: " + name);
    }


    private long alsGanzzahl(double wert) {
        if (wert != Math.floor(wert) || Double.isInfinite(wert)) {
            throw new ArithmeticException("Bit-Rechnung nur mit ganzen Zahlen");
        }
        return (long) wert;
    }



    public static String formatiere(double wert) {
        if (Double.isNaN(wert)) {
            return "nicht definiert";
        }
        if (Double.isInfinite(wert)) {
            return wert > 0 ? "unendlich" : "-unendlich";
        }
        if (wert == Math.floor(wert) && Math.abs(wert) < 1e15) {
            return String.valueOf((long) wert);
        }

        String zahl = String.format(Locale.US, "%.2f", wert);
        if (Double.parseDouble(zahl) == 0) {
            zahl = String.format(Locale.US, "%.10f", wert);
        }
        while (zahl.endsWith("0")) {
            zahl = zahl.substring(0, zahl.length() - 1);
        }
        if (zahl.endsWith(".")) {
            zahl = zahl.substring(0, zahl.length() - 1);
        }
        return zahl;
    }

}
