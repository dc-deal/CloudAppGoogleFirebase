package net.livingrecordings.giggermainapp;

/**
 * Created by Kraetzig Neu on 25.10.2016.
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // preferences bildschirm hinzufügen, als xml datei
        addPreferencesFromResource(R.xml.preferences);

        // preferenz "packen" und
        Preference aktienlistePref = findPreference(getString(R.string.preference_key));
        // machen, das der on preference change listener auch aufgerufen wird,
        //wenn wir das dign ändern.
        aktienlistePref.setOnPreferenceChangeListener(this);


        // Damit die summary auch sofort beim Erzeugen der Activity EinstellungenActivity angezeigt wird,
        // müssen wir noch die folgenden Zeilen in die onCreate-Methode einfügen.
        // ->> onPreferenceChange sofort aufrufen mit der in SharedPreferences gespeicherten Aktienliste
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String gespeicherteAktienliste = sharedPrefs.getString(aktienlistePref.getKey(), ""); // genau das was in der xml datei verzeichnet ist.
        // hier wird direkt die on change abgefahren, man könnte uach aktienlistePref.setSummary(gespeicherteAktienliste.toString()); machen.
        onPreferenceChange(aktienlistePref, gespeicherteAktienliste);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        // das summary kannn man auch in der xml datei beschriften,
        // aber so wird es immer aktualisiert wenn wir den wert ändern!!
        // quasi ein shcnellansicht
        // das summary ist ein text unter dem element.
        preference.setSummary(value.toString());

        return true;
    }
}