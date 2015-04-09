package awsomethree.com.townkitchen.helpers;

import android.app.Application;

import awsomethree.com.townkitchen.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
/**
 * Created by long on 4/9/15.
 */
public class CalligraphyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-ThinItalic.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }
}