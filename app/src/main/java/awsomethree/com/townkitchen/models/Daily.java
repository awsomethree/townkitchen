package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/30/15.
 */
@ParseClassName("Daily")
public class Daily extends ParseObject {
    public static int DAILY_CODE = 5;

    private Date menuDate;
    private String imageUrl;
    private String description;
    private Boolean active;

    public Boolean getActive() {
        return getBoolean("active");
    }

    public void setActive(Boolean active) {
        put("active", active);
    }

    public Date getMenuDate() {
        return getDate("menuDate");
    }

    public void setMenuDate(Date menuDate) {
        put("menuDate", menuDate);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl", imageUrl);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public static void listAllAvailableDays(
            final ParseQueryCallback callback, final int queryCode){

        ParseQuery<Daily> query = ParseQuery.getQuery(Daily.class);
        query.whereEqualTo("active", true);
        query.include("Daily");
        query.orderByAscending("menuDate");
        query.findInBackground(new FindCallback<Daily>() {
            @Override
            public void done(List<Daily> dailies, ParseException e) {
                callback.parseQueryDone(dailies, e, queryCode);
            }
        });
    }
}
