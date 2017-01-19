package net.livingrecordings.giggermainapp;

import android.content.Context;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

/**
 * Created by Kraetzig Neu on 19.01.2017.
 */

public class Compability {

    public Compability(){

    }

    public static Compability getInstance(){
        return new Compability();
    }

    public void MenuButtonHack(Context context){
        //-------------------------------------------------------------
        // Abwärtskompabilität. Vor der 4.4. version
        // wurde der Menübutton noch nciht nagezeigt, hier aber
        // der code damit der MENÜ button auf jeden Fall ge-Forced wird.
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        } //menü hack ende
    }

}
