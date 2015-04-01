package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Order")
public class Order extends ParseObject{
    public static final int ORDER_CODE = 2;

    public static final String CART_SPECIAL_ID = "cart-order";
    public static final String CART_STATUS = "cart";
    public static final String DELIVERED_STATUS = "delivered";
    public static final String IN_DELIVERY_STATUS  = "in-delivery";
    public static final String PENDING_STATUS   = "pending";

    private Date transactionDate;
    private double priceBeforeTax;
    private double tax;
    private double priceAfterTax;
    private int totalOrder;
    private String deliveryStatus;
    private String deliveryAddressStr;// delivery address in string format
    private ParseGeoPoint deliveryAddressGeo; // delivery address in Geo format

    // delivery phase
    private ParseGeoPoint deliveryCurrentLocation; // driver location
    private Date shipDate;

    public Date getTransactionDate() {
        return getDate("transactionDate");
    }

    public void setTransactionDate(Date transactionDate) {
        put("transactionDate", transactionDate);
    }

    public double getPriceBeforeTax() {
        return getDouble("priceBeforeTax");
    }

    public void setPriceBeforeTax(double priceBeforeTax) {
        put("priceBeforeTax", priceBeforeTax);
    }

    public double getTax() {
        return getDouble("tax");
    }

    public void setTax(double tax) {
        put("tax", tax);
    }

    public double getPriceAfterTax() {
        return getDouble("priceAfterTax");
    }

    public void setPriceAfterTax(double priceAfterTax) {
        put("priceAfterTax", priceAfterTax);
    }

    public int getTotalOrder() {
        return getInt("totalOrder");
    }

    public void setTotalOrder(int totalOrder) {
        put("totalOrder", totalOrder);
    }

    public String getDeliveryAddressStr() {
        return getString("deliveryAddressStr");
    }

    public void setDeliveryAddressStr(String deliveryAddressStr) {
        put("deliveryAddressStr", deliveryAddressStr);
    }

    public ParseGeoPoint getDeliveryAddressGeo() {
        return getParseGeoPoint("deliveryAddressGeo");
    }

    public void setDeliveryAddressGeo(ParseGeoPoint deliveryAddressGeo) {
        put("deliveryAddressGeo", deliveryAddressGeo);
    }

    public String getDeliveryStatus() {
        return getString("deliveryStatus");
    }

    public void setDeliveryStatus(String deliveryStatus) {
        put("deliveryStatus", deliveryStatus);
    }

    public ParseGeoPoint getDeliveryCurrentLocation() {
        return getParseGeoPoint("deliveryCurrentLocation");
    }

    public void setDeliveryCurrentLocation(ParseGeoPoint deliveryCurrentLocation) {
        put("deliveryCurrentLocation", deliveryCurrentLocation);
    }


    public static void getOrderInDelivery(final ParseQueryCallback callback, final int queryCode){
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("deliveryStatus", IN_DELIVERY_STATUS);
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orders, ParseException e) {
                callback.parseQueryDone(orders, e, queryCode);
            }
        });
    }

    public Date getShipDate() {
        return getDate("shipDate");
    }

    public void setShipDate(Date shipDate){
        put("shipDate", shipDate);
    }

    /**
     * Query locally, find the first shopping cart object on this device otherwise
     * create new one (locally)
     *
     */
    public static void prepareShoppingCart(Context ctx){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        if (pref.contains("shoppingCartId")){
            return ;
        }

        ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
        order.whereEqualTo("deliveryStatus", CART_STATUS);
        order.whereEqualTo("objectId", CART_SPECIAL_ID);
//        order.fromLocalDatastore();
        order.setLimit(1);

        final Context localContext = ctx;

        order.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> orders, ParseException e) {
                // setup into Shared Preferences
                final Order shoppingCart;
                if (orders.size() > 0){
                    // always get the first one
                    shoppingCart = orders.get(0);
                    Order.setupShoppingCartOnPreferences(localContext, shoppingCart.getObjectId());
                } else {
                    shoppingCart = new Order();
//                    shoppingCart.setObjectId(CART_SPECIAL_ID);
                    shoppingCart.setDeliveryStatus(Order.CART_STATUS);
//                    shoppingCart.pinInBackground(new SaveCallback() {
                    shoppingCart.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Order.setupShoppingCartOnPreferences(localContext,
                                        shoppingCart.getObjectId());
                            }
                        }
                    });
                }
            }
        });
    }

    public static void clearShoppingCart(Context localContext){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(localContext);
        SharedPreferences.Editor edit = pref.edit();
        edit.remove("shoppingCartId");
        edit.commit();
        Order.prepareShoppingCart(localContext);
    }

    public static void setupShoppingCartOnPreferences(Context localContext, String shoppingCartId){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(localContext);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("shoppingCartId", shoppingCartId);
        Log.d(MainActivity.APP, "shopping Cart ID record : " + shoppingCartId);
        edit.putInt("shoppingCartTotal", 0);
        edit.commit();
    }

    public static void addToShoppingCart(final DailyMenu orderItem, final int qty, Context ctx){
        // adding the order for specific DailyMenu item
        final Context localContext = ctx;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(localContext);
        final String orderId = pref.getString("shoppingCartId", "-");
        Log.d(MainActivity.APP, "Order ID : " + orderId);
        // query the order
        ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
        order.whereEqualTo("deliveryStatus", CART_STATUS);
        order.whereEqualTo("objectId", orderId);

        // only look in this device
//        order.fromLocalDatastore();

        ParseQuery<FoodMenu> foodMenu = ParseQuery.getQuery(FoodMenu.class);
        foodMenu.whereEqualTo("objectId", orderItem.getFoodMenu().getObjectId());

        ParseQuery<DailyMenu> dailyMenu = ParseQuery.getQuery(DailyMenu.class);
        dailyMenu.whereMatchesQuery("FoodMenu", foodMenu);

        // query the order line item (which match)
        ParseQuery<OrderLineItem> lineItems = ParseQuery.getQuery(OrderLineItem.class);
        lineItems.whereMatchesQuery("Order", order);
        lineItems.whereMatchesQuery("DailyMenu", dailyMenu);

        lineItems.include("Order");
        lineItems.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItems, ParseException e) {
                OrderLineItem newLineItem;
                if (orderLineItems != null && orderLineItems.size() > 0){
                    newLineItem = orderLineItems.get(0);
                    newLineItem.setQty(qty);
                } else {
                    // add new order line item
                    newLineItem = new OrderLineItem();
                    newLineItem.setMenu(orderItem);
                    newLineItem.setQty(qty);
                    newLineItem.put("Order", (ParseObject.createWithoutData("Order", orderId)));
                }
                if (qty <= 0){
                    newLineItem.deleteInBackground();
                } else {
//                newLineItem.pinInBackground();
                    newLineItem.saveInBackground();
                }
            }
        });
    }

    public static void getShoppingCart(final ParseQueryCallback callback, final int queryCode,Context ctx){
        // adding the order for specific DailyMenu item
        final Context localContext = ctx;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(localContext);
        final String orderId = pref.getString("shoppingCartId", "-");
        Log.d(MainActivity.APP, "Order ID : " + orderId);
        // query the order
        ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
        order.whereEqualTo("deliveryStatus", CART_STATUS);
        order.whereEqualTo("objectId", orderId);

        // only look in this device
//        order.fromLocalDatastore();

        // query the order line item (which match)
        ParseQuery<OrderLineItem> lineItems = ParseQuery.getQuery(OrderLineItem.class);
        lineItems.whereMatchesQuery("Order", order);
        lineItems.include("Order");
        lineItems.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItems, ParseException e) {
                callback.parseQueryDone(orderLineItems, e, queryCode);
            }
        });
    }
}
