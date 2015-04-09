package awsomethree.com.townkitchen;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

import awsomethree.com.townkitchen.models.Daily;
import awsomethree.com.townkitchen.models.DailyMenu;
import awsomethree.com.townkitchen.models.Feedback;
import awsomethree.com.townkitchen.models.FoodMenu;
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by smulyono on 3/23/15.
 */
public class TownKitchenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // all Parse data object
        ParseObject.registerSubclass(FoodMenu.class);
        ParseObject.registerSubclass(Order.class);
        ParseObject.registerSubclass(OrderLineItem.class);
        ParseObject.registerSubclass(Daily.class);
        ParseObject.registerSubclass(DailyMenu.class);
        ParseObject.registerSubclass(Feedback.class);

        // uncomment this to enable parse communication
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_app_id),
                getString(R.string.parse_client_key));
        ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/gtw.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

    }


}
