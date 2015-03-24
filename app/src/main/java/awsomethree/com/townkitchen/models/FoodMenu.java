package awsomethree.com.townkitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by smulyono on 3/23/15.
 */
@ParseClassName("FoodMenu")
public class FoodMenu extends ParseObject{
    // Some arbitrary code for the callback interface, to identify
    // what ParseObject is being operated on
    public static final int FOODMENU_CODE = 1;

    public FoodMenu(){ super();}

    private String name;
    private String description;
    private double price;
    private String imageUrl;

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public double getPrice() {
        return getDouble("price");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl", imageUrl);
    }


}
