package awsomethree.com.townkitchen;

import android.app.Application;

/**
 * Created by smulyono on 3/23/15.
 */
public class TownKitchenApplication extends Application {

    public static final String PARSE_APPLICATION_ID = "";
    public static final String PARSE_CLIENT_KEY = "";


    @Override
    public void onCreate() {
        super.onCreate();
        // uncomment this to enable parse communication
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
