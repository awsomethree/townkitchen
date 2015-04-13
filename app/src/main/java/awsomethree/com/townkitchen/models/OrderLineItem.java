package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 * coauthor long huynh
 */
@ParseClassName("OrderLineItem")
public class OrderLineItem extends ParseObject{
    public static final int ORDERLINEITEM_CODE = 3;

    private Order order;
    private DailyMenu menu;
    private int qty;
    private double price; // also need to keep this so that in later price changes, this history still intact
    private double priceTotal;

    public Order getOrder() {
        return (Order) getParseObject("Order");
    }

    public void setOrder(Order order) {
        put("Order", order);
    }

    public DailyMenu getMenu() {
        return (DailyMenu) getParseObject("DailyMenu");
    }

    public void setMenu(DailyMenu menu) {
        put("DailyMenu", menu);
    }

    public int getQty() {
        return getInt("qty");
    }

    public void setQty(int qty) {
        put("qty", qty);
    }

    public double getPrice() {
        return getDouble("price");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    public double getPriceTotal() {
        return getDouble("priceTotal");
    }

    public void setPriceTotal(double priceTotal) {
        put("priceTotal", priceTotal);
    }

    // Retrieve all menu by specific date
    public static void listAllOrderLineItemsByDates(int skip,
                                           final ParseQueryCallback callback, final int queryCode){
        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
        order.whereEqualTo("user", user);
        order.whereNotEqualTo("deliveryStatus", ShoppingCart.CART_STATUS);

        ParseQuery<OrderLineItem> query = ParseQuery.getQuery(OrderLineItem.class);
        query.orderByAscending("createdAt");
        query.whereMatchesQuery("Order",order);
        query.include("Order");
        query.include("DailyMenu");
        query.include("DailyMenu.FoodMenu");
        query.include("Feedback");

        query.setLimit(MainActivity.PAGE_SIZE);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItem, ParseException e) {
                callback.parseQueryDone(orderLineItem, e, queryCode);
            }
        });

    }
}
