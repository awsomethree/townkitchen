package awsomethree.com.townkitchen;

import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

import awsomethree.com.townkitchen.models.Daily;
import awsomethree.com.townkitchen.models.DailyMenu;
import awsomethree.com.townkitchen.models.Feedback;
import awsomethree.com.townkitchen.models.FoodMenu;
import awsomethree.com.townkitchen.models.Order;
import awsomethree.com.townkitchen.models.OrderLineItem;

/**
 * Created by smulyono on 3/23/15.
 */
public class TownKitchenApplication extends Application {
//    TownKitchenSB
//    public static final String PARSE_APPLICATION_ID = "DrECmIAzwm5sp9qGouQzZnNC0Ya0uWoemHnRhxbS";
//    public static final String PARSE_CLIENT_KEY = "52OMxk1lUCqYBpVtSqjompMtxtYCKlbrQGvtE3AE";

    public static final String PARSE_APPLICATION_ID = "rFoHa7dbffxXh8qbEgiAwcc5KpT1lGt0E3srKzzK";
    public static final String PARSE_CLIENT_KEY = "6SUzIyD1sYP99qB2BK4LvklKR07Xfydoh1GdXm00";
//
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
        Parse.initialize(this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY);
    }
}
