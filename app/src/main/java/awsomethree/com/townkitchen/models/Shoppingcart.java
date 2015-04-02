package awsomethree.com.townkitchen.models;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;

import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;

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
    private Double subtotal;
    private Double shippingCost;
    private Double taxTotal;
    private Double total;

    public Double getTotal() {
        return total;
    }

    public Double getTaxTotal() {
        return taxTotal;
    }

    public Double getShippingCost() {
        return shippingCost;
    }

    public Double getSubtotal() {
        return subtotal;
    }



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

    public void setShippingCost(Double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public void setTaxTotal(Double taxTotal) {
        this.taxTotal = taxTotal;
    }

    /**
     * stuff calculation, this should be from server side
     */
    public void calculateTotal() {
        subtotal = 0.0;
        shippingCost = 10.0;
        taxTotal = 0.0;
        total = 0.0;

        for (OrderLineItem item : items) {
            subtotal += Double.valueOf(item.getQty() * item.getMenu().getFoodMenu().getPrice());
        }

        taxTotal = Double.valueOf(.09 * subtotal.doubleValue());

        total = (subtotal + shippingCost + taxTotal);
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
        return "$" + String.format("%.2f", subtotal);
    }

    public String getTaxString() {
        return "$ " + String.format("%.2f", taxTotal);
    }

    public String getShippingString() {
        return "$ " + String.format("%.2f", shippingCost);
    }

    public String getTotalString() {
        return "$ " + String.format("%.2f", total);
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

        final Context localContext = ctx;

        if (pref.contains("shoppingCartId")){
            // make sure the id is still in Parse
            String objectId = pref.getString("shoppingCartId", "-");
            ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
            order.whereEqualTo("deliveryStatus", CART_STATUS);
            order.whereEqualTo("objectId", objectId);
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
                        createNewShoppingCartObjectInParse(localContext);
                    }
                }
            });
        } else {
            createNewShoppingCartObjectInParse(localContext);
        }
    }

    private static void createNewShoppingCartObjectInParse(Context ctx){
        final Context localContext = ctx;
        // create new Order object
        final Order shoppingCart = new Order();
        shoppingCart.setDeliveryStatus(ShoppingCart.CART_STATUS);
        shoppingCart.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ShoppingCart.setupShoppingCartOnPreferences(localContext,
                            shoppingCart.getObjectId());
                }
            }
        });

    }

    public static void clearShoppingCart(Context localContext){
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(localContext);
        String objectId = pref.getString("shoppingCartId", "-");
        SharedPreferences.Editor edit = pref.edit();

        edit.remove("shoppingCartId");
        edit.remove("shoppingCartTotal");
        edit.commit();
        final Context ctx = localContext;
        // remove shopping cart model (unpin)
        ParseObject po = ParseObject.createWithoutData("Order", objectId);
        po.deleteEventually(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                OrderLineItem.unpinAllInBackground();
                ShoppingCart.prepareShoppingCart(ctx);
            }
        });

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

    public static void updateCartTotal(Context localContext,fragmentNavigationInterface mainActivityParent){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(localContext);
        final String orderId = pref.getString("shoppingCartId", "-");
        final Context ctx = localContext;
        final fragmentNavigationInterface fragmentParent = mainActivityParent;

        // query the order
        ParseQuery<Order> order = ParseQuery.getQuery(Order.class);
        order.whereEqualTo("deliveryStatus", CART_STATUS);
        order.whereEqualTo("objectId", orderId);

        // only look in this device
//        order.fromLocalDatastore();

        // query all line items(which match)
        ParseQuery<OrderLineItem> lineItems = ParseQuery.getQuery(OrderLineItem.class);
        lineItems.whereMatchesQuery("Order", order);
//        lineItems.fromLocalDatastore();
        lineItems.include("Order");
        lineItems.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItems, ParseException e) {
                int newQty = 0;
                for (OrderLineItem orderLineItem : orderLineItems){
                    newQty += orderLineItem.getQty();
                }
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt("shoppingCartTotal", newQty);
                edit.commit();
                String newQtyString = Integer.toString(newQty);
                fragmentParent.updateCartBadge(newQtyString);
            }
        });

    }

    public static void addToShoppingCart(final DailyMenu orderItem, final int qty, Context ctx){
        addToShoppingCart(orderItem, qty, ctx, null);
    }
    public static void addToShoppingCart(final DailyMenu orderItem, final int qty, Context ctx, fragmentNavigationInterface mainActivityParent){
        // adding the order for specific DailyMenu item
        final Context localContext = ctx;
        final fragmentNavigationInterface parentFragment = mainActivityParent;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(localContext);
        final String orderId = pref.getString("shoppingCartId", "-");


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
//        lineItems.fromLocalDatastore();
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
//                    newLineItem.unpinInBackground();
                    newLineItem.deleteInBackground();
                } else {
//                    newLineItem.pinInBackground();
                    newLineItem.saveInBackground();
                }
                ShoppingCart.updateCartTotal(localContext, parentFragment);

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
        lineItems.include("DailyMenu");
        lineItems.include("DailyMenu.FoodMenu");
//        lineItems.fromLocalDatastore();
        lineItems.findInBackground(new FindCallback<OrderLineItem>() {
            @Override
            public void done(List<OrderLineItem> orderLineItems, ParseException e) {
                // recalculate the shopping cart subtotal and such

                callback.parseQueryDone(orderLineItems, e, queryCode);
            }
        });
    }

    public static void flushShoppingCart(final ParseQueryCallback callback,
            final int queryCode,
            final ShoppingCart shoppingCartModel,
            Context ctx){
        // flush the order object
        if (shoppingCartModel.getItems().size() > 0){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            int orderQty = pref.getInt("shoppingCartTotal", 0);

            OrderLineItem orderLI = shoppingCartModel.getItems().get(0);

            Order newOrder = new Order();
            newOrder.setDeliveryAddressStr(shoppingCartModel.getShippingAddress());
            newOrder.setDeliveryStatus(Order.PENDING_STATUS);
            newOrder.setPriceAfterTax(shoppingCartModel.getTotal());
            newOrder.setTotalOrder(orderQty);
            newOrder.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    // now save the line items
                    List<OrderLineItem> orderLineItems = shoppingCartModel.getItems();
                    ParseObject.saveAllInBackground(orderLineItems, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            callback.parseQueryDone(null, null, queryCode);
                        }
                    });
                }
            });
        }
    }
}
