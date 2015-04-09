package awsomethree.com.townkitchen.models;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;

/**
 * Created by smulyono on 3/24/15.
 */
@ParseClassName("Order")
public class Order extends ParseObject implements Parcelable {
    public static final int ORDER_CODE = 2;

    public static final String DELIVERED_STATUS = "delivered";
    public static final String OUT_FOR_DELIVERY_STATUS = "out-for-delivery";
    public static final String PENDING_STATUS   = "pending";

    public static final String START_DELIVERY_ADDRESS = "Oakland, CA 94602";

    private Date transactionDate;
    private double priceBeforeTax;
    private double tax;
    private double priceAfterTax;
    private int totalOrder;
    private String deliveryStatus;
    private String deliveryAddressStr;// delivery address in string format
    private String deliveryAddressState;
    private String deliveryAddressZip;
    private ParseUser user;

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public String getDeliveryAddressState() {
        return getString("deliveryAddressState");
    }

    public void setDeliveryAddressState(String deliveryAddressState) {
        put("deliveryAddressState", deliveryAddressState);
    }

    public String getDeliveryAddressZip() {
        return getString("deliveryAddressZip");
    }

    public void setDeliveryAddressZip(String deliveryAddressZip) {
        put("deliveryAddressZip", deliveryAddressZip);
    }

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
        String[] notIncludedStatus = {ShoppingCart.CART_STATUS, PENDING_STATUS};
        query.whereNotContainedIn("deliveryStatus", Arrays.asList(notIncludedStatus));
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.addDescendingOrder("deliveryStatus");
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(transactionDate != null ? transactionDate.getTime() : -1);
        dest.writeDouble(this.priceBeforeTax);
        dest.writeDouble(this.tax);
        dest.writeDouble(this.priceAfterTax);
        dest.writeInt(this.totalOrder);
        dest.writeString(this.deliveryStatus);
        dest.writeString(this.deliveryAddressStr);
        dest.writeString(this.deliveryAddressState);
        dest.writeString(this.deliveryAddressZip);
        dest.writeLong(shipDate != null ? shipDate.getTime() : -1);
    }

    public Order() {
    }

    private Order(Parcel in) {
        long tmpTransactionDate = in.readLong();
        this.transactionDate = tmpTransactionDate == -1 ? null : new Date(tmpTransactionDate);
        this.priceBeforeTax = in.readDouble();
        this.tax = in.readDouble();
        this.priceAfterTax = in.readDouble();
        this.totalOrder = in.readInt();
        this.deliveryStatus = in.readString();
        this.deliveryAddressStr = in.readString();
        this.deliveryAddressState = in.readString();
        this.deliveryAddressZip = in.readString();
        long tmpShipDate = in.readLong();
        this.shipDate = tmpShipDate == -1 ? null : new Date(tmpShipDate);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
