package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("DailyMenu")
public class DailyMenu extends ParseObject{
    public static final int DAILYMENU_CODE = 4;

    private int qtyStock; // number of quantity before it sold out
    private Date menuDate;
    private FoodMenu foodMenu;

    public int getQtyStock() {
        return getInt("qtyStock");
    }

    public FoodMenu getFoodMenu() {
        return (FoodMenu) getParseObject("FoodMenu");
    }

    public void setFoodMenu(FoodMenu foodMenu) {
        put("FoodMenu", foodMenu);
    }

    public void setQtyStock(int qtyStock) {

        put("qtyStock", qtyStock);
    }

    public Date getMenuDate() {
        return getDate("menuDate");
    }

    public void setMenuDate(Date menuDate) {
        put("menuDate", menuDate);
    }

    // Retrieve all menu by specific date
    public static void listAllMenuByDates(Date menuDate,
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

        ParseQuery<DailyMenu> query = ParseQuery.getQuery(DailyMenu.class);
        query.setLimit(10);
        query.whereGreaterThanOrEqualTo("menuDate", qLowDate);
        query.whereLessThan("menuDate", qMaxDate);
        query.orderByAscending("createdAt");
        query.include("FoodMenu");
        query.findInBackground(new FindCallback<DailyMenu>() {
            @Override
            public void done(List<DailyMenu> foodMenus, ParseException e) {
                callback.parseQueryDone(foodMenus, e, queryCode);
            }
        });
    }

}
