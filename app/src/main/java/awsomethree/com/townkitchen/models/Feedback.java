package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Feedback")
public class Feedback extends ParseObject{
    public static final int FEED_CODE = 4;
    public Feedback() { super();}
    private List<Feedback> items;
    private FoodMenu foodMenu;
    private double rating;
    private String comment;

    // helper item
    private String orderLIid;

    public String getOrderLIid() {
        return orderLIid;
    }

    public void setOrderLIid(String orderLIid) {
        this.orderLIid = orderLIid;
    }

    public FoodMenu getFoodMenu() {
        return (FoodMenu) getParseObject("FoodMenu");
    }

    public void setFoodMenu(FoodMenu foodMenu) {
        put("foodMenu", foodMenu);
    }

    public double getRating() {
        return getDouble("rating");
    }

    public void setRating(double rating) {
        put("rating", rating);
    }

    public String getComment() {
        return getString("comment");
    }

    public void setComment(String comment) {
        put("comment", comment);
    }

    // Retrieve all menu by specific date
    public static void listAllFeedsByDates(Date menuDate,
                                          final ParseQueryCallback callback, final int queryCode){

        Calendar lowDate = Calendar.getInstance();
        lowDate.setTime(menuDate);
        lowDate.set(Calendar.HOUR_OF_DAY, 0);
        lowDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date qLowDate = lowDate.getTime();

        Calendar maxDate = Calendar.getInstance();
        maxDate.setTime(menuDate);
        maxDate.set(Calendar.HOUR_OF_DAY, 24);
        maxDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date qMaxDate = maxDate.getTime();

        ParseQuery<Feedback> query = ParseQuery.getQuery(Feedback.class);
        //query.setLimit(10);
        //query.whereGreaterThanOrEqualTo("menuDate", qLowDate);
        //query.whereLessThan("menuDate", qMaxDate);
        query.orderByDescending("createdAt");
        query.include("FoodMenu");
        query.findInBackground(new FindCallback<Feedback>() {
            @Override
            public void done(List<Feedback> feedbacks, ParseException e) {
                callback.parseQueryDone(feedbacks, e, queryCode);
            }
        });
    }

    public List<OrderLineItem> getItems() {
        return (items != null) ? items : Collections.EMPTY_LIST;
    }

    //Save the feedback
    public static void saveFeedback(final ParseQueryCallback callback,
                                    final int queryCode,
                                    final Feedback feedbackModel,
                                    Context ctx){
        // query the orderlineItem id to get the actual food menu
        ParseQuery<OrderLineItem> query = ParseQuery.getQuery(OrderLineItem.class);
        query.whereEqualTo("objectId", feedbackModel.getOrderLIid());
        query.include("DailyMenu");
        query.include("DailyMenu.FoodMenu");
        query.getFirstInBackground(new GetCallback<OrderLineItem>() {
            @Override
            public void done(OrderLineItem orderLineItem, ParseException e) {
                // get the food menu now
                String foodMenuId = orderLineItem.getMenu().getFoodMenu().getObjectId();

                // create new feedback
                Feedback savedFeedback = new Feedback();
                savedFeedback.setComment(feedbackModel.getComment());
                savedFeedback.setRating(feedbackModel.getRating());
                savedFeedback.put("FoodMenu", ParseObject.createWithoutData("FoodMenu", foodMenuId));
                savedFeedback.saveInBackground();
                callback.parseQueryDone(null, null, queryCode);
            }
        });
    }
}
