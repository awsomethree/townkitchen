package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Order")
public class Order extends ParseObject{
    public static final int ORDER_CODE = 2;

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
}
