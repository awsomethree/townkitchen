package awsomethree.com.townkitchen.fragments;

import com.parse.ParseException;
import com.parse.ParseObject;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import awsomethree.com.townkitchen.R;
import awsomethree.com.townkitchen.abstracts.TKFragment;
import awsomethree.com.townkitchen.activities.MainActivity;
import awsomethree.com.townkitchen.adapters.ShoppingCartAdapter;
import awsomethree.com.townkitchen.dialogs.PaymentDialog;
import awsomethree.com.townkitchen.interfaces.ParseQueryCallback;
import awsomethree.com.townkitchen.interfaces.dialogInterfaceListener;
import awsomethree.com.townkitchen.interfaces.fragmentNavigationInterface;
import awsomethree.com.townkitchen.models.OrderLineItem;
import awsomethree.com.townkitchen.models.Payment;
import awsomethree.com.townkitchen.models.Shipping;
import awsomethree.com.townkitchen.models.ShoppingCart;

/**
 * Created by smulyono on 3/22/15.
 */
public class ShoppingCartFragment extends TKFragment implements dialogInterfaceListener, ParseQueryCallback {
    protected ListView lvMenu;

    private Button btnCheckout;
    private ShoppingCartAdapter shoppingCartAdapter;
    private View shoppingCartFooterView;

    public TextView subTotalAmount;
    public TextView taxAmount;
    public TextView shippingAmount;
    public TextView totalAmount;
    public EditText shippingAddress;

    protected List<OrderLineItem> shoppingCartLists;
    protected ShoppingCart shoppingCartModel;
    protected int counter = 0;
    protected final int NUM_MAGIC_COUNTER = 5;
    protected double discount = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        //todo viewholder for footer
//        shoppingCartFooterView = getLayoutInflater(savedInstanceState).inflate(R.layout.shopping_cart_footer_layout, null);
        setupView(v);
        setupAdaptersAndListeners();

        return v;
    }

    private void setupView(View v) {
        lvMenu = (ListView) v.findViewById(R.id.lvShoppingCart);

        // setup on touch
        lvMenu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    counter++;
                    if (counter == NUM_MAGIC_COUNTER){
                        discount = 5.0;
                        Toast.makeText(v.getContext(), "Discount applied", Toast.LENGTH_SHORT).show();
                        getShoppingCart();
                    }
                }
                return false;
            }
        });
        shippingAddress = (EditText) v.findViewById(R.id.tvShippingAddress);
        subTotalAmount = (TextView) v.findViewById(R.id.tvSubTotalAmount);
        taxAmount = (TextView) v.findViewById(R.id.tvTaxAmount);
        shippingAmount = (TextView) v.findViewById(R.id.tvShippingAmount);
        totalAmount = (TextView) v.findViewById(R.id.tvTotalAmount);

        btnCheckout = (Button) v.findViewById(R.id.btnCheckout);

    }

    private void setupAdaptersAndListeners() {
        // initial
        shoppingCartLists = new ArrayList<>();

        shoppingCartAdapter = new ShoppingCartAdapter(getActivity(), getEmptyShoppingCart());
        lvMenu.setAdapter(shoppingCartAdapter);

//        lvMenu.addFooterView(shoppingCartFooterView);


        // on click listeners for btnCheckout
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // populate the shipping address
                Shipping shipInfo = new Shipping();
                shipInfo.setAddressLine1(shippingAddress.getText().toString());
                shipInfo.setApt("");
//            shipInfo.setZip();
                shipInfo.setState("");

                shoppingCartModel.setShipping(shipInfo);
                // open up new dialogs for paying
                PaymentDialog payDialog = PaymentDialog.newInstance(ShoppingCartFragment.this, shoppingCartModel);
                payDialog.show(getFragmentManager(), "Pay");
            }
        });

        getShoppingCart();
    }

    private void updateShoppingCartFooter(ShoppingCart shoppingCart) {
        // payment
        shippingAddress.setText(Html.fromHtml(shoppingCart.getShippingAddress()));
        subTotalAmount.setText(shoppingCart.getSubTotalString());
        taxAmount.setText(shoppingCart.getTaxString());
        shippingAmount.setText(shoppingCart.getShippingString());
        shippingAmount.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        totalAmount.setText(shoppingCart.getTotalString());

    }


    @Override
    public void onSuccessDialog() {
        // redirect to order history
        redirectFragmentTo(MainActivity.ORDERHISTORY_DRAWER_POSITION);
    }

    @Override
    public void onFailDialog() {
        Toast.makeText(getActivity(), "You have NOT make payment!!!!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Because of the async nature of Parse query, we will update the adapters after the
     * parse query is done. This method will provide empty placeholder
     * @return
     */
    public ShoppingCart getEmptyShoppingCart(){
        return new ShoppingCart(new ArrayList<OrderLineItem>());
    }

    /**
     * TODO read from Parse to get item added
     *
     * @return
     */
    public void getShoppingCart() {
        // stuff for now
        ShoppingCart.getShoppingCart(this, ShoppingCart.SHOPPING_CART_CODE, getActivity().getApplicationContext());
        // update the badge
        ShoppingCart.updateCartTotal(getActivity().getApplicationContext(),(fragmentNavigationInterface) getActivity());
    }

    @Override
    public void parseQueryDone(List<? extends ParseObject> parseObjects, ParseException e,
            int queryCode) {
        if (queryCode == ShoppingCart.SHOPPING_CART_CODE){
            shoppingCartLists = (List<OrderLineItem>) parseObjects;

            shoppingCartModel = new ShoppingCart(shoppingCartLists);

            Shipping shipInfo = new Shipping();
            shipInfo.setAddressLine1(shippingAddress.getText().toString());
            shipInfo.setApt("");
//            shipInfo.setZip();
            shipInfo.setState("");

            shoppingCartModel.setShipping(shipInfo);

            Payment payment = new Payment();
            shoppingCartModel.setPayment(payment);

            shoppingCartModel.calculateTotal(discount);

            Log.d(this.getClass().getName(), "stuff shopping cart " + shoppingCartModel);

            // update footer
            updateShoppingCartFooter(shoppingCartModel);
            shoppingCartAdapter.clear();
            shoppingCartAdapter.addAll(shoppingCartLists);
        }
    }
}
