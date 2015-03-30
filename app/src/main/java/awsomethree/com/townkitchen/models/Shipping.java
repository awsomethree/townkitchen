package awsomethree.com.townkitchen.models;

import java.io.Serializable;

/**
 * Created by ktruong on 3/29/15.
 */
public class Shipping implements Serializable {
    private String addressLine1;
    private String apt;
    private int zip;
    private String state;

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setApt(String apt) {
        this.apt = apt;
    }

    public String getApt() {
        return apt;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public int getZip() {
        return zip;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}
