package awsomethree.com.townkitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Feedback")
public class Feedback extends ParseObject{
    public Feedback() { super();}

    private FoodMenu foodMenu;
    private int rating;
    private String comment;

    public FoodMenu getFoodMenu() {
        return (FoodMenu) getParseObject("FoodMenu");
    }

    public void setFoodMenu(FoodMenu foodMenu) {
        put("foodMenu", foodMenu);
    }

    public int getRating() {
        return getInt("rating");
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public String getComment() {
        return getString("comment");
    }

    public void setComment(String comment) {
        put("comment", comment);
    }
}
