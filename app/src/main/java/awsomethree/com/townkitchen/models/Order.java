package awsomethree.com.townkitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Order")
public class Order extends ParseObject{
    public static final int ORDER_CODE = 2;

    private Date transactionDate;
    private double priceBeforeTax;
    private double tax;
    private double priceAfterTax;
    private int totalOrder;
    private String deliveryAddressStr;// delivery address in string format
    private ParseGeoPoint deliveryAddressGeo; // delivery address in Geo format

    // delivery phase
    private boolean isDelivered;
    private ParseGeoPoint deliveryCurrentLocation; // driver location

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

    public boolean isDelivered() {
        return getBoolean("isDelivered");
    }

    public void setDelivered(boolean isDelivered) {
        put("isDelivered", isDelivered);
    }

    public ParseGeoPoint getDeliveryCurrentLocation() {
        return getParseGeoPoint("deliveryCurrentLocation");
    }

    public void setDeliveryCurrentLocation(ParseGeoPoint deliveryCurrentLocation) {
        put("deliveryCurrentLocation", deliveryCurrentLocation);
    }
}
