package awsomethree.com.townkitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("OrderLineItem")
public class OrderLineItem extends ParseObject{
    public static final int ORDERLINEITEM_CODE = 3;

    private Order order;
    private FoodMenu menu;
    private int qty;
    private double price; // also need to keep this so that in later price changes, this history still intact
    private double priceTotal;

    // rating / feedback for the specific order
    private Feedback feedback;

    public Feedback getFeedback() {
        return (Feedback) getParseObject("feedback");
    }

    public void setFeedback(Feedback feedback) {
        put("feedback", feedback);
    }

    public Order getOrder() {
        return (Order) getParseObject("Order");
    }

    public void setOrder(Order order) {
        put("order", order);
    }

    public FoodMenu getMenu() {
        return (FoodMenu) getParseObject("FoodMenu");
    }

    public void setMenu(FoodMenu menu) {
        put("foodMenu", menu);
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
}