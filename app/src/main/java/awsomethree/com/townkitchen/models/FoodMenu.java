package awsomethree.com.townkitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by smulyono on 3/23/15.
 * coauthor long huynh
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


    // Deserialize the JSON and build menu option objects
    // FoodMenu.fromJSON("{...}"} => <FoodMenu>
    public  static FoodMenu fromJSON(JSONObject jsonObject){
        FoodMenu option = new FoodMenu();
        // Extract the values from the json, and store them
        try {
            option.name = jsonObject.getString("name");
            //option.uid = jsonObject.getLong("id");
            option.description = jsonObject.getString("description");
            option.imageUrl = jsonObject.getString("imageUrl");
            option.price = jsonObject.getDouble("price");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //return the menu option object
        return option;
    }

    //Return an arraylist of <FoodMenu>
    // FoodMenu.fromJSONArray([{...}, {...}]) => List<FoodMenu>
    public static ArrayList<FoodMenu> fromJSONArray(JSONArray jsonArray){
        ArrayList<FoodMenu> options = new ArrayList<>();
        // Iterate the raw json array and create tweets
        for(int i = 0; i<jsonArray.length(); i++){
            try {
                JSONObject optionJson = jsonArray.getJSONObject(i);//getting each of the JSONObject
                FoodMenu option = FoodMenu.fromJSON(optionJson);// create the tweet (POJO) form the JSONObject
                if(option != null){
                    options.add(option);//add option to the ArrayList
                }
            } catch (JSONException e) {
                e.printStackTrace();
                continue;//if one fail, continue to next and others
            }

        }
        return options;//finished list
    }

}
