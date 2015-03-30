package awsomethree.com.townkitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.ShoppingCartAdapter;
import awsomethree.com.townkitchen.dialogs.PaymentDialog;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;
import awsomethree.com.townkitchen.models.OrderUtils;
import awsomethree.com.townkitchen.models.OrderLineItem;
import awsomethree.com.townkitchen.models.Payment;
import awsomethree.com.townkitchen.models.Shipping;
import awsomethree.com.townkitchen.models.ShoppingCart;

/**
 * Created by smulyono on 3/22/15.
 */
public class ShoppingCartFragment extends TKFragment implements dialogInterfaceListener {
    protected ListView lvMenu;

    //    private ArrayAdapter<String> menuAdapters;
    private Button btnCheckout;
    private ShoppingCartAdapter shoppingCartAdapter;
    private View shoppingCartFooterView;

    public TextView subTotalAmount;
    public TextView taxAmount;
    public TextView shippingAmount;
    public TextView totalAmount;
    public TextView shippingAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        //todo viewholder for footer
        shoppingCartFooterView = getLayoutInflater(savedInstanceState).inflate(R.layout.shopping_cart_footer_layout, null);
        shippingAddress = (TextView) shoppingCartFooterView.findViewById(R.id.tvShippingAddress);
        subTotalAmount = (TextView) shoppingCartFooterView.findViewById(R.id.tvSubTotalAmount);
        taxAmount = (TextView) shoppingCartFooterView.findViewById(R.id.tvTaxAmount);
        shippingAmount = (TextView) shoppingCartFooterView.findViewById(R.id.tvShippingAmount);
        totalAmount = (TextView) shoppingCartFooterView.findViewById(R.id.tvTotalAmount);

        btnCheckout = (Button) shoppingCartFooterView.findViewById(R.id.btnCheckout);

        setupView(v);
        setupAdaptersAndListeners();
        return v;
    }

    private void setupView(View v) {
        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);
//        btnCheckout = (Button) v.findViewById(R.id.btnCheckout);
    }

    private void setupAdaptersAndListeners() {
        // arbitrary menu
//        String[] menus = {"Selection 1", "Selection 2", "Selection 3", "Selection 4", "Selection 5"};
        // setup the adapters (Using basic)
//        menuAdapters = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menus);

        ShoppingCart shoppingCart = getShoppingCart();
        shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), shoppingCart);
        lvMenu.setAdapter(shoppingCartAdapter);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // move to cart or something
                Toast.makeText(getActivity(), "clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        lvMenu.addFooterView(shoppingCartFooterView);


        // on click listeners for btnCheckout
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open up new dialogs for paying
                PaymentDialog payDialog = PaymentDialog.newInstance(getCurrentFragment());
                payDialog.show(getFragmentManager(), "Pay");
            }
        });

        updateShoppingCartFooter(shoppingCart);
    }

    private void updateShoppingCartFooter(ShoppingCart shoppingCart) {
        // payment
        shippingAddress.setText(Html.fromHtml(shoppingCart.getShippingAddress()));
        subTotalAmount.setText(shoppingCart.getSubTotalString());
        taxAmount.setText(shoppingCart.getTaxString());
        shippingAmount.setText(shoppingCart.getShippingString());
        totalAmount.setText(shoppingCart.getTotalString());
    }


    @Override
    public void onSuccessDialog() {
        Toast.makeText(getActivity(), "You make payment and redirected to Order History", Toast.LENGTH_SHORT).show();
        // redirect to order history
        redirectFragmentTo(MainActivity.ORDERHISTORY_DRAWER_POSITION);
    }

    @Override
    public void onFailDialog() {
        Toast.makeText(getActivity(), "You have NOT make payment!!!!", Toast.LENGTH_SHORT).show();
    }

    /**
     * TODO read from Parse to get item added
     *
     * @return
     */
    public ShoppingCart getShoppingCart() {
        // stuff for now

        List<OrderLineItem> orderLineItems = OrderUtils.getStuffOrder();
        ShoppingCart shoppingCart = new ShoppingCart(orderLineItems);

        Shipping shipInfo = new Shipping();
        shipInfo.setAddressLine1("1235 Bay St");
        shipInfo.setApt("");
        shipInfo.setZip(94404);
        shipInfo.setState("CA");

        shoppingCart.setShipping(shipInfo);

        Payment payment = new Payment();
        shoppingCart.setPayment(payment);

        shoppingCart.calculateTotal();

        Log.i(this.getClass().getName(), "stuff shopping cart " + shoppingCart);

        return shoppingCart;
    }
}
