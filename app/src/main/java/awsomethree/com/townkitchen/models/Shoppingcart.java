package awsomethree.com.townkitchen.models;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by ktruong on 3/29/15.
 */
public class ShoppingCart {
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
}
