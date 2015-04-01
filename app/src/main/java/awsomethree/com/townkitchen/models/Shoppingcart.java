package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by ktruong on 3/29/15.
 */
public class ShoppingCart {
    public static final String CART_SPECIAL_ID = "cart-order";
    public static final String CART_STATUS = "cart";

    public static final int SHOPPING_CART_CODE = 6;

    private List<OrderLineItem> items;

    private Shipping shipping;
    private Payment payment;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxTotal;
    private BigDecimal total;

    public ShoppingCart(List<OrderLineItem> orderLineItems) {
        this.items = orderLineItems;
    }

    public List<OrderLineItem> getItems() {
        return (items != null) ? items : Collections.EMPTY_LIST;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public void setTaxTotal(BigDecimal taxTotal) {
        this.taxTotal = taxTotal;
    }

    /**
     * stuff calculation, this should be from server side
     */
    public void calculateTotal() {
        subtotal = BigDecimal.ZERO;
        shippingCost = BigDecimal.TEN;
        taxTotal = BigDecimal.ZERO;
        total = BigDecimal.ZERO;

        for (OrderLineItem item : items) {
            subtotal = subtotal.add(BigDecimal.valueOf(item.getQty() * item.getPrice()));
        }

        taxTotal = BigDecimal.valueOf(.09 * subtotal.doubleValue());

        total = subtotal.add(shippingCost).add(taxTotal);
    }

    public String getShippingAddress() {
        StringBuilder builder = new StringBuilder();

        if(shipping != null) {
            builder.append(shipping.getAddressLine1());
            builder.append(shipping.getApt()).append("<br>");
            builder.append(shipping.getState()).append(" ").append(shipping.getZip());
        }

        return builder.toString();
    }

    public String getSubTotalString() {
        return "$" + subtotal;
    }

    public String getTaxString() {
        return "$" + taxTotal;
    }

    public String getShippingString() {
        return "$" + shippingCost;
    }

    public String getTotalString() {
        return "$" + total;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ShoppingCart{");
        sb.append("items=").append(items);
        sb.append(", shipping=").append(shipping);
        sb.append(", payment=").append(payment);
        sb.append(", subtotal=").append(subtotal);
        sb.append(", shippingCost=").append(shippingCost);
        sb.append(", taxTotal=").append(taxTotal);
        sb.append(", total=").append(total);
        sb.append('}');
        return sb.toString();
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
        order.fromLocalDatastore();
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
                    ShoppingCart.setupShoppingCartOnPreferences(localContext, shoppingCart.getObjectId());
                } else {
                    shoppingCart = new Order();
                    shoppingCart.setObjectId(CART_SPECIAL_ID);
                    shoppingCart.setDeliveryStatus(ShoppingCart.CART_STATUS);
                    shoppingCart.pinInBackground(new SaveCallback() {
//                    shoppingCart.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ShoppingCart.setupShoppingCartOnPreferences(localContext,
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
        // remove shopping cart model (unpin)
        Order.unpinAllInBackground();
        OrderLineItem.unpinAllInBackground();
        ShoppingCart.prepareShoppingCart(localContext);
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
        order.fromLocalDatastore();

        ParseQuery<FoodMenu> foodMenu = ParseQuery.getQuery(FoodMenu.class);
        foodMenu.whereEqualTo("objectId", orderItem.getFoodMenu().getObjectId());

        ParseQuery<DailyMenu> dailyMenu = ParseQuery.getQuery(DailyMenu.class);
        dailyMenu.whereMatchesQuery("FoodMenu", foodMenu);

        // query the order line item (which match)
        ParseQuery<OrderLineItem> lineItems = ParseQuery.getQuery(OrderLineItem.class);
        lineItems.whereMatchesQuery("Order", order);
        lineItems.whereMatchesQuery("DailyMenu", dailyMenu);
        lineItems.fromLocalDatastore();
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
                    newLineItem.unpinInBackground();
//                    newLineItem.deleteInBackground();
                } else {
                    newLineItem.pinInBackground();
//                    newLineItem.saveInBackground();
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
        order.fromLocalDatastore();

        // query the order line item (which match)
        ParseQuery<OrderLineItem> lineItems = ParseQuery.getQuery(OrderLineItem.class);
        lineItems.whereMatchesQuery("Order", order);
        lineItems.include("Order");
        lineItems.include("DailyMenu");
        lineItems.include("DailyMenu.FoodMenu");
        lineItems.fromLocalDatastore();
        lineItems.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItems, ParseException e) {
                callback.parseQueryDone(orderLineItems, e, queryCode);
            }
        });
    }
}
