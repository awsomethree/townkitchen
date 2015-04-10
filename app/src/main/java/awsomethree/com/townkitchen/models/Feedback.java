package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    private ParseUser user;

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

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

        ParseQuery<Feedback> query = ParseQuery.getQuery(Feedback.class);
        query.orderByDescending("createdAt");
        query.include("FoodMenu");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
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
        query.orderByDescending("createdAt");
        query.getFirstInBackground(new GetCallback<OrderLineItem>() {
            @Override
            public void done(OrderLineItem orderLineItem, ParseException e) {
                // get the food menu now
                String foodMenuId = orderLineItem.getMenu().getFoodMenu().getObjectId();

                // get user
                ParseUser user = ParseUser.getCurrentUser();

                // create new feedback
                Feedback savedFeedback = new Feedback();
                savedFeedback.setComment(feedbackModel.getComment());
                savedFeedback.setRating(feedbackModel.getRating());
                savedFeedback.put("FoodMenu", ParseObject.createWithoutData("FoodMenu", foodMenuId));
                savedFeedback.put("user", user);
                savedFeedback.saveInBackground();
                callback.parseQueryDone(null, null, queryCode);
            }
        });
    }
}
