package net.livingrecordings.giggermainapp.giggerMainClasses;

/**
 * Created by Kraetzig Neu on 18.01.2017.
 */

public class GiggerUserAPI {

    public static final String USERS_PUBLISHED = "USERS_PUBLISHED";
    public static final String BANDS_PUBLISHED = "BANDS_PUBLISHED";
    public static final String BANDS_GLOBAL_NAMESEARCH_INDEX = "BANDS_GLOBAL_NAMESEARCH_INDEX";

    public static GiggerUserAPI getInstance() {
        return new GiggerUserAPI();
    }




}
