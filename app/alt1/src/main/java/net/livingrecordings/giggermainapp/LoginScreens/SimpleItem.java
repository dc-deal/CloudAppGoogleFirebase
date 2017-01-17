package net.livingrecordings.giggermainapp.LoginScreens;

import java.io.Serializable;

/**
 * Created by Kraetzig Neu on 21.12.2016.
 */

public class SimpleItem  implements Serializable {

    private String val,key;

    public SimpleItem(String val, String key) {
        this.val = val;
        this.key = key;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    @Override
    public String toString() { return val; }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
